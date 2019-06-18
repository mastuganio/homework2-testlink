package com.mastugin.rule;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DriverSetupRule implements TestRule {

    private static final Logger LOGGER = LogManager.getLogger(DriverSetupRule.class);

    private DriverSetupRule() {
    }

    public static DriverSetupRule getInstance(){
        return new DriverSetupRule();
    }

    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                driverSetup();
                base.evaluate();
            }
        };
    }

    private void driverSetup(){
        WebDriverManager.chromedriver().setup();
        LOGGER.info("Setup ChromeDriver");
    }
}
