package ru.hse.routemood;

import org.junit.jupiter.api.Test;

class ServerAppTest {
    @Test void appHasAGreeting() {
        ServerApp classUnderTest = new ServerApp();
        assertNotNull(classUnderTest.getGreeting(), "app should have a greeting");
    }
}
