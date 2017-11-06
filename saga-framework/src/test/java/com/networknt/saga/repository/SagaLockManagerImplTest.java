package com.networknt.saga.repository;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.core.command.common.CommandMessageHeaders;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageBuilder;
import com.networknt.saga.core.message.producer.MessageProducer;
import com.networknt.saga.participant.SagaLockManager;
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
 * Junit test class for SagaLockManagerImplTest.
 * use H2 test database for data source
 */
public class SagaLockManagerImplTest {

    public static DataSource ds;

    static {
        ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
       try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/queryside_ddl.sql";
            InputStream in = SagaLockManagerImplTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in);
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private SagaLockManager  sagaLockManager= (SagaLockManager) SingletonServiceFactory.getBean(SagaLockManager.class);



    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testClaimLock() {
        sagaLockManager.claimLock("order.service","22222", "target");

    }

}
