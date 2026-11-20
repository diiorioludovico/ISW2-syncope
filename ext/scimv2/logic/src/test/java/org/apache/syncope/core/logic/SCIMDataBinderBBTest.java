/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.syncope.core.logic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.apache.syncope.core.logic.SCIMDataBinder;
import org.apache.syncope.core.logic.SCIMDataBinder;
import org.apache.syncope.core.logic.scim.SCIMConfManager;
import org.apache.syncope.core.spring.security.AuthDataAccessor;
import org.apache.syncope.core.persistence.api.dao.GroupDAO;
import org.apache.syncope.core.provisioning.api.jexl.JexlTools;

import org.apache.syncope.ext.scimv2.api.data.SCIMAnyObject;

public class SCIMDataBinderBBTest {

    private static SCIMDataBinder scimDataBinder;
    private static SCIMConfManager confManager = mock(SCIMConfManager.class);
    private static UserLogicOp userLogic = mock(UserLogicOp.class);
    private static AuthDataAccessor authDataAccessor = mock(AuthDataAccessor.class);
    private static GroupDAO groupDAO = mock(GroupDAO.class);
    private static JexlTools jexlTools = mock(JexlTools.class);

    private static BufferedWriter logWriter;

    private static void log(String message) throws IOException {
        logWriter.write(message);
        logWriter.newLine();
        logWriter.flush();
    }

    @BeforeAll
    public static void setUp() throws IOException {
        Path logFile = Paths.get("target","test-logs","SCIMDataBinder.log");
        Files.createDirectories(logFile.getParent());
        logWriter = Files.newBufferedWriter(
                logFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
        log("File di log: " + logFile.toAbsolutePath());

        scimDataBinder = new SCIMDataBinder(confManager, userLogic, authDataAccessor, groupDAO, jexlTools);
    }

    @BeforeEach
    void logTestName(TestInfo testInfo) throws IOException {
        log("");
        log("==================================================");
        log("TEST: " + testInfo.getDisplayName());
        log("==================================================");
    }

    @AfterAll
    static void closeLog() throws IOException {
        if (logWriter != null) {
            logWriter.close();
        }
    }

    /* _____________________________________
       |                                    |
       |  test per filter2JexlExpression()  |
       |____________________________________| */

    @Test 
    public void filter2JexlExpressionCorrectFilterTest() throws IOException {
        String filter = "attr sw \"val\"";
        String jexlExpression = "attr =^ \"val\"";

        String result = SCIMDataBinder.filter2JexlExpression(filter);
        
        log("input: " + filter);
        log("expected:" + jexlExpression);
        log("result: " + result);

        assertEquals(result, jexlExpression);
    }

    @Test
    public void filter2JexlExpressionIncorrectFilterTest() throws IOException {
        String filter = "bho in bal";
        String jexlExpression = "bho in bal";

        String result = SCIMDataBinder.filter2JexlExpression(filter);

        log("input: " + filter);
        log("expected:" + jexlExpression);
        log("result: " + result);

        assertEquals(result, jexlExpression);
    }

    @Test
    public void filter2JexlExpressionNullFilterTest() throws IOException {
        String filter = null;
        String jexlExpression = "attr =^ \"val\"";


        assertThrows(NullPointerException.class, () -> SCIMDataBinder.filter2JexlExpression(null));
    }

    /* _____________________________________
       |                                    |
       |        test per toSCIMUser()       |
       |____________________________________| */

}