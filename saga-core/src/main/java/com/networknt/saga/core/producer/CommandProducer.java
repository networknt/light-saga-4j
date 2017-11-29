package com.networknt.saga.core.producer;



import com.networknt.eventuate.common.Command;

import java.util.Map;

public interface CommandProducer {

  /**
   * Sends a command
   * @param channel the channel of message
   * @param command the command to send
   * @param replyTo the reply to target
   * @param headers additional headers  @return the id of the sent command
   * @return String result
   */
  String send(String channel, Command command, String replyTo, Map<String, String> headers);

  /**
   * Sends a command
   * @param channel the channel of message
   * @param resource the resource
   * @param command the command to send
   * @param replyTo the reply to target
   * @param headers additional headers  @return the id of the sent command
   * @return String result
   */
  String send(String channel, String resource, Command command, String replyTo, Map<String, String> headers);
}
