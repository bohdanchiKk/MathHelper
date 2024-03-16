package org.example;

import org.example.service.MathHelperService;
import org.example.swing.MathHelperApp;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class MathHelperServiceTest {

    private MathHelperService mathHelperService = new MathHelperService();

    @Test
    public void divide_ValidEquation_ReturnsTrue() {
        assertTrue(mathHelperService.divide("2*x+5=17"));
        assertTrue(mathHelperService.divide("-1.3*5/x=1.2"));
        assertTrue(mathHelperService.divide("2*x*x=10"));
        assertTrue(mathHelperService.divide("17=2*x+5"));
    }

    @Test
    public void divide_InvalidEquation_ReturnsFalse() {
        assertFalse(mathHelperService.divide("2*x+5==17")); // Double equals sign
        assertFalse(mathHelperService.divide("2*x+5")); // Missing equals sign
        assertFalse(mathHelperService.divide("2*x+5=17=2*x+5")); // Multiple equals sign
        assertFalse(mathHelperService.divide("2*x+5==")); // Invalid end of equation
        assertFalse(mathHelperService.divide("x+5=17=")); // Invalid end of equation
    }
}
