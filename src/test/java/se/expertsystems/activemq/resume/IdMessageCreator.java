package se.expertsystems.activemq.resume;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class IdMessageCreator implements MessageCreator {

  private static final String KEY_ID = "id";

  private long id;

  public IdMessageCreator(long id) {
    this.id = id;
  }

  @Override
  public Message createMessage(Session session) throws JMSException {
    MapMessage message = session.createMapMessage();
    message.setObject(KEY_ID, id);
    return message;
  }

  public static long parseMessage(Message message) {
    if (! (message instanceof MapMessage)) {
      throw new IllegalArgumentException("Expected message of type MapMessage, got: " + message.getClass().getName());
    }
    MapMessage mapMessage = (MapMessage) message;
    try {
      if (! mapMessage.itemExists(KEY_ID)) {
        throw new IllegalArgumentException("Expected key '" + KEY_ID + "' not found in the message");
      }
      return mapMessage.getLong(KEY_ID);
    } catch (JMSException e) {
      throw new RuntimeException("Could not read the message", e);
    }
  }

}
