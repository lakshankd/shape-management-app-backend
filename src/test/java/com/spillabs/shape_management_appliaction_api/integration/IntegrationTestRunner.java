package com.spillabs.shape_management_appliaction_api.integration;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Shape Management Application Integration Tests")
@SelectPackages({
        "com.spillabs.shape_management_appliaction_api.integration"
})
public class IntegrationTestRunner {
}