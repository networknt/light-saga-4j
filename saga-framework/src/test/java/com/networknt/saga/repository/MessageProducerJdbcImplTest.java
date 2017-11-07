package com.networknt.saga.repository;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.core.command.common.CommandMessageHeaders;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageBuilder;
import com.networknt.saga.core.message.producer.MessageProducer;
import com.networknt.service.SingletonServiceFactory;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Junit test class for MessageProducerJdbcImpl.
 * use H2 test database for data source
 */
public class MessageProducerJdbcImplTest {

    public static DataSource ds;

    static {
        ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
       try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/saga_repository_ddl.sql";
            InputStream in = MessageProducerJdbcImplTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in);
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private MessageProducer  messageProducer= (MessageProducer) SingletonServiceFactory.getBean(MessageProducer.class);


    private static String destination;
    private static Message message;

    @BeforeClass
    public static void setUp() {
        destination = "message_send";
        Map<String, String> headers = new HashMap<>();
        headers.put("title", "test");
        OrderCommand command = new OrderCommand();
        MessageBuilder builder = MessageBuilder.withPayload(JSonMapper.toJson(command))
                .withExtraHeaders("", headers) // TODO should these be prefixed??!
                .withHeader(CommandMessageHeaders.DESTINATION, "orderService")
                .withHeader(CommandMessageHeaders.COMMAND_TYPE, command.getClass().getName())
                .withHeader(CommandMessageHeaders.REPLY_TO, "reply_message");
        message= builder.build();
    }

    @Test
    public void testSend() {
        messageProducer.send(destination, message);
    }

}
