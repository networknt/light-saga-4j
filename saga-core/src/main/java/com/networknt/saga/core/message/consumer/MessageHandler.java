package com.networknt.saga.core.message.consumer;

import com.networknt.saga.core.message.common.Message;

import java.util.function.Consumer;

public interface MessageHandler extends Consumer<Message> {
}
