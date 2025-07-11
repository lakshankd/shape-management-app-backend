package com.spillabs.shape_management_appliaction_api;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Shape Management Application Unit Tests")
@SelectPackages({
        "com.spillabs.shape_management_appliaction_api.service",
        "com.spillabs.shape_management_appliaction_api.util"
})
public class TestRunner {
}