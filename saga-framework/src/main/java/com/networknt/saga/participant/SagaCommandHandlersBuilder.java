package com.networknt.saga.participant;




import com.networknt.tram.command.consumer.CommandHandler;
import com.networknt.tram.command.consumer.CommandHandlers;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.message.common.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class SagaCommandHandlersBuilder implements ISagaCommandHandlersBuilder {
  private String channel;

  private List<CommandHandler> handlers = new ArrayList<>();

  public static SagaCommandHandlersBuilder fromChannel(String channel) {
    return new SagaCommandHandlersBuilder().andFromChannel(channel);
  }

  private SagaCommandHandlersBuilder andFromChannel(String channel) {
    this.channel = channel;
    return this;
  }

  @Override
  public <C> SagaCommandHandlerBuilder<C> onMessageReturningMessages(Class<C> commandClass,
                                                                     Function<CommandMessage<C>, List<Message>> handler) {
    SagaCommandHandler h = new SagaCommandHandler(channel, commandClass, handler);
    this.handlers.add(h);
    return new SagaCommandHandlerBuilder<C>(this, h);
  }

  @Override
  public <C> SagaCommandHandlerBuilder<C> onMessageReturningOptionalMessage(Class<C> commandClass,
                                                                            Function<CommandMessage<C>, Optional<Message>> handler) {
    SagaCommandHandler h = new SagaCommandHandler(channel, commandClass, (c) -> handler.apply(c).map(Collections::singletonList).orElse(Collections.EMPTY_LIST));
    this.handlers.add(h);
    return new SagaCommandHandlerBuilder<C>(this, h);
  }

  @Override
  public <C> SagaCommandHandlerBuilder<C> onMessage(Class<C> commandClass,
                                                    Function<CommandMessage<C>, Message> handler) {
    SagaCommandHandler h = new SagaCommandHandler(channel, commandClass,
            (c) -> Collections.singletonList(handler.apply(c)));
    this.handlers.add(h);
    return new SagaCommandHandlerBuilder<C>(this, h);
  }

  @Override
  public <C> SagaCommandHandlerBuilder<C> onMessage(Class<C> commandClass, Consumer<CommandMessage<C>> handler) {
    SagaCommandHandler h = new SagaCommandHandler(channel, commandClass,
            (c) -> {
              handler.accept(c);
              return Collections.emptyList();
            });
    this.handlers.add(h);
    return new SagaCommandHandlerBuilder<C>(this, h);
  }

  public CommandHandlers build() {
    return new CommandHandlers(handlers);
  }

}
