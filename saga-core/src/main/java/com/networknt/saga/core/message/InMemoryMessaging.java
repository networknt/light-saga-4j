package com.networknt.saga.core.message;



import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.eventuate.jdbc.IdGeneratorImpl;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.consumer.MessageConsumer;
import com.networknt.saga.core.message.consumer.MessageHandler;
import com.networknt.saga.core.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InMemoryMessaging implements MessageProducer, MessageConsumer {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private IdGenerator idGenerator = new IdGeneratorImpl();

//  private TransactionTemplate transactionTemplate;

  private Executor executor = Executors.newCachedThreadPool();

  @Override
  public void send(String destination, Message message) {
    String id = idGenerator.genId().asString();
    message.getHeaders().put(Message.ID, id);
  /*  if (TransactionSynchronizationManager.isActualTransactionActive()) {
      logger.info("Transaction active");
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
        @Override
        public void afterCommit() {
          reallySend(destination, message);
        }
      });
    } else {
      logger.info("No transaction active");
      reallySend(destination, message);
    }*/
    //TODO handle local transaction here
    logger.info("No transaction active");
    reallySend(destination, message);
  }

  private void reallySend(String destination, Message message) {
    List<MessageHandler> handlers = subscriptions.getOrDefault(destination, Collections.emptyList());
    logger.info("sending to channel {} that has {} subscriptions this message {} ", destination, handlers.size(), message);
    for (MessageHandler handler : handlers) {
  /*    executor.execute(() -> transactionTemplate.execute(ts -> {
        try {
          handler.accept(message);
        } catch (Throwable t) {
          logger.error("message handler " + destination, t);
        }
        return null;
      }));*/

    executor.execute(() ->{
        try {
          handler.accept(message);
        } catch (Throwable t) {
          logger.error("message handler " + destination, t);
        }
     //   return null;
      });

    }
  }

  private Map<String, List<MessageHandler>> subscriptions = new HashMap<>();

  @Override
  public void subscribe(String subscriberId, Set<String> channels, MessageHandler handler) {
    logger.info("subscribing {} to channels {}", subscriberId, channels);
    for (String channel : channels) {
      List<MessageHandler> handlers = subscriptions.get(channel);
      if (handlers == null) {
        handlers = new ArrayList<>();
        subscriptions.put(channel, handlers);
      }
      handlers.add(handler);
    }
  }
}
