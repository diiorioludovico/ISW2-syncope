/*
 * Compatibility support for the EvoSuite-generated ZookeeperConfParamOps tests.
 * Uses JUnit 4 and the project's Mockito, without EvoSuite instrumentation.
 */
package org.apache.syncope.common.keymaster.client.zookeeper;

import java.io.Serializable;
import org.junit.AssumptionViolatedException;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ZookeeperConfParamOps_ESTest_scaffolding {

    /**
     * Legacy string matcher: preserves matching of null String arguments.
     * Inherited methods take precedence over static imports in the test class.
     */
    protected static String anyString() {
        return ArgumentMatchers.nullable(String.class);
    }

    /**
     * Preserve the generated default-answer policy: an unstubbed non-void
     * invocation invalidates the generated assumption, rather than returning null.
     * Void methods need no default return value.
     */
    protected static final class ViolatedAssumptionAnswer
            implements Answer<Object>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public Object answer(final InvocationOnMock invocation) {
            if (invocation.getMethod().getReturnType() == Void.TYPE) {
                return null;
            }
            throw new AssumptionViolatedException(
                    "Unstubbed mock invocation invalidates the generated test assumption: "
                    + invocation.getMethod().toGenericString());
        }
    }

    /**
     * Keep the generated check on the originating class of the caught exception.
     * This is not merely an assertThrows(RuntimeException.class) replacement.
     */
    protected static void verifyException(final String expectedOrigin, final Throwable exception) {
        if (exception instanceof AssumptionViolatedException) {
            throw (AssumptionViolatedException) exception;
        }
        assertNotNull("Expected a non-null exception", exception);
        StackTraceElement[] trace = exception.getStackTrace();
        assertTrue("The exception must have a stack trace", trace.length > 0);
        assertEquals("Unexpected origin of the caught exception", expectedOrigin,
                trace[0].getClassName());
    }
}
