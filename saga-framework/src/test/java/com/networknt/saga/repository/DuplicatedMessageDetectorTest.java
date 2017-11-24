package com.networknt.saga.repository;


import com.networknt.saga.consumer.DuplicateMessageDetector;

import com.networknt.service.SingletonServiceFactory;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Junit test class for SagaLockManagerImplTest.
 * use H2 test database for data source
 */
public class DuplicatedMessageDetectorTest {

    public static DataSource ds;

    static {
        ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
       try (Connection connection = ds.getConnection()) {
            // Runscript doesn't work need to execute batch here.
            String schemaResourceName = "/saga_repository_ddl.sql";
            InputStream in = DuplicatedMessageDetectorTest.class.getResourceAsStream(schemaResourceName);

            if (in == null) {
                throw new RuntimeException("Failed to load resource: " + schemaResourceName);
            }
            InputStreamReader reader = new InputStreamReader(in);
            RunScript.execute(connection, reader);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DuplicateMessageDetector duplicateMessageDetector = (DuplicateMessageDetector) SingletonServiceFactory.getBean(DuplicateMessageDetector.class);;



    @BeforeClass
    public static void setUp() {

    }

    @Test
    public void testIsDuplicate() {
        boolean firstMessage  = duplicateMessageDetector.isDuplicate("111", "222");
        assertFalse(firstMessage);
        boolean sesondMessage  = duplicateMessageDetector.isDuplicate("111", "333");
        assertFalse(sesondMessage);
        boolean duplicatedFirst  = duplicateMessageDetector.isDuplicate("111", "222");
        assertTrue(duplicatedFirst);
    }

}
