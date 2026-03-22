package com.org.framework;

import com.yourcompany.automation.framework.listener.TestResultExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
/**
 *  Sample Test
 */
//@ExtendWith(TestResultExtension.class)
public class SampleFrameworkTest2 {

    @Test
    void testAddition_Pass() {
        int result = 2 + 3;
        assertEquals(5, result, "Addition should be correct");
    }

    @Test
    void testSubtraction_Fail() {
        int result = 5 - 3;
        assertEquals(1, result, "This test is intentionally failing");
    }

    @Test
    void testBoolean() {
        assertTrue(10 > 5, "10 should be greater than 5");
    }
}