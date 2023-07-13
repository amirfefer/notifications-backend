package com.redhat.cloud.notifications.templates;

import com.redhat.cloud.notifications.EmailTemplatesInDbHelper;
import com.redhat.cloud.notifications.TestHelpers;
import com.redhat.cloud.notifications.ingress.Action;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class TestImageBuilderTemplate extends EmailTemplatesInDbHelper {

    static final String LAUNCH_SUCCESS = "launch-success";
    static final String LAUNCH_FAILURE = "launch-failed";

    private static final Action SUCCESS_ACTION = TestHelpers.createImageBuilderAction(LAUNCH_SUCCESS);
    private static final Action FAILURE_ACTION = TestHelpers.createImageBuilderAction(LAUNCH_FAILURE);

    @Override
    protected String getBundle() {
        return "rhel";
    }

    @Override
    protected String getApp() {
        return "image-builder";
    }

    @Override
    protected List<String> getUsedEventTypeNames() {
        return List.of(LAUNCH_SUCCESS, LAUNCH_FAILURE);
    }

    @Test
    public void testSuccessLaunchEmailTitle() {
        statelessSessionFactory.withSession(statelessSession -> {
            String result = generateEmailSubject(LAUNCH_SUCCESS, SUCCESS_ACTION);
            assertEquals("Instant notification - successful image launch - Red Hat Enterprise Linux", result);
        });
    }

    @Test
    public void testSuccessLaunchEmailBody() {
        statelessSessionFactory.withSession(statelessSession -> {
            String result = generateEmailBody(LAUNCH_SUCCESS, SUCCESS_ACTION);
            assertTrue(result.contains("Instances launched successfully"));
            assertTrue(result.contains("91.123.32.4"));
        });
    }

    @Test
    public void testFailedLaunchEmailTitle() {
        statelessSessionFactory.withSession(statelessSession -> {
            String result = generateEmailSubject(LAUNCH_FAILURE, FAILURE_ACTION);
            assertEquals("Instant notification - image launch failed - Red Hat Enterprise Linux", result);
        });
    }

    @Test
    public void testFailedLaunchEmailBody() {
        statelessSessionFactory.withSession(statelessSession -> {
            String result = generateEmailBody(LAUNCH_FAILURE, FAILURE_ACTION);
            assertTrue(result.contains("An image failed to launch"));
            assertTrue(result.contains("Some launch error"));
        });
    }
}
