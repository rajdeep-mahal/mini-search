package com.minisearch;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test class for the Mini Search Engine
 */
public class MainTest {
    
    @Test
    public void testMainClassExists() {
        assertNotNull(Main.class);
    }
    
    @Test
    public void testBasicAssertion() {
        assertTrue(true, "Basic assertion should pass");
    }
    
    @Test
    public void testStringOperations() {
        String testString = "Mini Search Engine";
        assertNotNull(testString);
        assertEquals("Mini Search Engine", testString);
        assertTrue(testString.contains("Search"));
    }
}

