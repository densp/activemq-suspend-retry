package se.expertsystems.activemq.resume;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * Test case to test ActiveMQ configuration
 * @author Mattias Jiderhamn
 */
@SuppressWarnings("SynchronizeOnNonFinalField")
public abstract class JmsConfigTestBase {
  
  private static final long WAIT = 10 * 1000; // = 10 s
  
  @Autowired
  protected JmsTemplate testJmsTemplate;
  
  private static DefaultMessageListenerContainer messageListenerContainer;
  
  @Autowired
  protected TestListener listener;

  @Autowired
  public void setMessageListenerContainer(DefaultMessageListenerContainer messageListenerContainer) {
    JmsConfigTestBase.messageListenerContainer = messageListenerContainer;
  }

  @Before
  public void reset() {
    listener.reset(); // Clear up any previous tests
  }
  
  @AfterClass
  public static void tearDown() {
    System.out.println("Stopping DefaultMessageListenerContainer");
    messageListenerContainer.stop(); // Stop the listener to prepare for next test
  }
  
  @Transactional
  protected void send(long message) {
    testJmsTemplate.send(new IdMessageCreator(message));
  }

  @Transactional
  protected void sendFail(long message) {
    testJmsTemplate.send(new IdMessageCreator(message));
    
    throw new RuntimeException("rollback!");
  }

  @Test
  public void commitEnqueue() throws Exception {
    send(1L);
    
    synchronized (listener) {
      if (listener.getReceivedMessages().isEmpty()) // Unless already received
        listener.wait(WAIT);
    }

    assertEquals(1, listener.getReceivedMessages().size());
    assertThat(listener.getReceivedMessages(), hasItems(1L));
  }

  @Test
  public void rollbackEnqueue() throws Exception {
    try {
      sendFail(99L);
    }
    catch (Exception ex) {
      // Silent ignore
    }
    
    synchronized (listener) {
      listener.wait(WAIT); // Wait for non-rolled back message to be received
    }

    assertEquals(0, listener.getReceivedMessages().size());
  }
  
  @Test
  public void retryOnce() throws Exception {
    listener.setFailuresToRetry(1); // Fail once before success
    
    send(2L);
    
    synchronized (listener) {
      if (listener.getReceivedMessages().isEmpty()) // Unless already received
        listener.wait(WAIT);
    }
    
    assertEquals("All retries performed", 0, listener.getFailuresToRetry());
    assertEquals("Received 1 message after retry", 1, listener.getReceivedMessages().size());
    assertThat(listener.getReceivedMessages(), hasItems(2L));
  }
  
  @Test
  public void retryThreeTimes() throws Exception {
    listener.setFailuresToRetry(3); // Fail three times before success
    
    send(3L);
    
    synchronized (listener) {
      if (listener.getReceivedMessages().isEmpty()) // Unless already received
        listener.wait(WAIT);
    }
    
    assertEquals("All retries performed", 0, listener.getFailuresToRetry());
    assertEquals("Message only received once", 1, listener.getReceivedMessages().size());
    assertThat(listener.getReceivedMessages(), hasItems(3L));
  }

  @Test
  public void retryTenTimes() throws Exception {
    listener.setFailuresToRetry(10); // Fail ten times before success
    
    send(10L);
    
    synchronized (listener) {
      if (listener.getReceivedMessages().isEmpty()) // Unless already received
        listener.wait(WAIT);
    }
    
    assertEquals("Nothing received", 0, listener.getReceivedMessages().size()); // Nothing received
    System.out.println("Retries left: " + listener.getFailuresToRetry());
    assertTrue("Not all retries performed", listener.getFailuresToRetry() > 0 && listener.getFailuresToRetry() < 9);
  }

}