package com.networknt.saga.repository;


import com.networknt.saga.participant.SagaLockManager;
import com.networknt.service.SingletonServiceFactory;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Junit test class for SagaLockManagerImplTest.
 * use H2 test database for data source
 */
public class SagaLockManagerImplTest {

    private static DataSource ds;

    static {
        ds = SingletonServiceFactory.getBean(DataSource.class);
       try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/saga_repository_ddl.sql";
            InputStream in = SagaLockManagerImplTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            RunScript.execute(connection, reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SagaLockManager  sagaLockManager= (SagaLockManager) SingletonServiceFactory.getBean(SagaLockManager.class);



    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testClaimLock() {
        boolean firstLock  = sagaLockManager.claimLock("order.service","22222", "target");
        assertTrue(firstLock);
        boolean lockSameSagaId  = sagaLockManager.claimLock("order.service","22222", "target");
        assertTrue(lockSameSagaId);
        boolean lockWithDiffSaga  = sagaLockManager.claimLock("order.service","23456", "target");
        assertFalse(lockWithDiffSaga);
    }

}
