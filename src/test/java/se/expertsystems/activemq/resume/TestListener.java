package se.expertsystems.activemq.resume;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
* @author Mattias Jiderhamn
*/
public class TestListener implements MessageListener {
  
  private List<Long> receivedMessages = new ArrayList<Long>();
  
  /** No of failures that should cause a retry, left to use before accepting */
  private int failuresToRetry = 0;
  
  public List<Long> getReceivedMessages() {
    return receivedMessages;
  }
  
  public void reset() {
    receivedMessages.clear();
  }

  public int getFailuresToRetry() {
    return failuresToRetry;
  }

  public void setFailuresToRetry(int failuresToRetry) {
    this.failuresToRetry = failuresToRetry;
  }

  @Override
  public void onMessage(Message message) {
    long id = IdMessageCreator.parseMessage(message);
    
    /////////////////////////////////////////////////////
    // NOTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /////////////////////////////////////////////////////
    innerTx(); // Remove this line and the test will pass

    if(failuresToRetry > 0) {
      System.out.println("Message received: " + id + " - RETRY");
      throw new RuntimeException("Retry! " + (failuresToRetry--));
    }
    else {
      System.out.println("Message received: " + id);
      receivedMessages.add(id);
      
      synchronized (this) {
        this.notifyAll();
      }
    }
  }
  
  @Transactional(propagation = Propagation.REQUIRES_NEW) // This will cause suspend + resume
  private void innerTx() {
    System.out.println("JMS TX should now be suspended");
  } 
}