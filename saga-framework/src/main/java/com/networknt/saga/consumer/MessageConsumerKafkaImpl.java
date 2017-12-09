package com.networknt.saga.consumer;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.kafka.consumer.EventuateKafkaConsumer;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.common.MessageImpl;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.consumer.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class MessageConsumerKafkaImpl implements MessageConsumer {

  private Logger logger = LoggerFactory.getLogger(getClass());

  //private String bootstrapServers;
  private List<EventuateKafkaConsumer> consumers = new ArrayList<>();

 /* public MessageConsumerKafkaImpl(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }*/

   public MessageConsumerKafkaImpl() {

  }

  //private TransactionTemplate transactionTemplate;


  private DuplicateMessageDetector duplicateMessageDetector = (DuplicateMessageDetector) SingletonServiceFactory.getBean(DuplicateMessageDetector.class);;

  @Override
  public void subscribe(String subscriberId, Set<String> channels, MessageHandler handler) {
    BiConsumer<ConsumerRecord<String, String>, BiConsumer<Void, Throwable>> kcHandler = (record, callback) -> {
      Message m = toMessage(record);

      // TODO If we do that here then remove TT from higher-levels

      if (duplicateMessageDetector.isDuplicate(subscriberId, m.getId())) {
        logger.trace("Duplicate message {} {}", subscriberId, m.getId());
        callback.accept(null, null);
      }
      try {
        logger.trace("Invoking handler {} {}", subscriberId, m.getId());
        handler.accept(m);
      } catch (Throwable t) {
        logger.trace("Got exception {} {}", subscriberId, m.getId());
        logger.trace("Got exception ", t);
        callback.accept(null, t);
      }
      logger.trace("handled message {} {}", subscriberId, m.getId());
      callback.accept(null, null);
  /*    transactionTemplate.execute(ts -> {
        if (duplicateMessageDetector.isDuplicate(subscriberId, m.getId())) {
          logger.trace("Duplicate message {} {}", subscriberId, m.getId());
          callback.accept(null, null);
          return null;
        }
        try {
          logger.trace("Invoking handler {} {}", subscriberId, m.getId());
          handler.accept(m);
        } catch (Throwable t) {
          logger.trace("Got exception {} {}", subscriberId, m.getId());
          logger.trace("Got exception ", t);
          callback.accept(null, t);
          return null;
        }
        logger.trace("handled message {} {}", subscriberId, m.getId());
        callback.accept(null, null);
        return null;
      });*/
    };

    EventuateKafkaConsumer kc = new EventuateKafkaConsumer(subscriberId, kcHandler, new ArrayList<>(channels));
    consumers.add(kc);
    kc.start();
  }

  public void close() {
    consumers.forEach(EventuateKafkaConsumer::stop);
  }

  private Message toMessage(ConsumerRecord<String, String> record) {
    return JSonMapper.fromJson(record.value(), MessageImpl.class);
  }
}
