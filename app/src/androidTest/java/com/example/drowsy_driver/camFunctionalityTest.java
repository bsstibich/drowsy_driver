package com.example.drowsy_driver;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

public class camFunctionalityTest extends TestCase {
    @RunWith(AndroidJUnit4.class)
    class MyTestSuite {
        @Test
        public void testEvent() {
            ActivityScenario<camFunctionality> scenario = ActivityScenario.launch(camFunctionality.class);
        }
    }
}