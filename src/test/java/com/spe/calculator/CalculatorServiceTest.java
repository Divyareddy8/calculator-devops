package com.spe.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.spe.calculator.exception.DivisionByZeroException;
import com.spe.calculator.service.CalculatorService;

class CalculatorServiceTest {

    private final CalculatorService svc = new CalculatorService();

    private static BigDecimal n(String s) {
        return new BigDecimal(s);
    }

    @Test
    void addReturnsSum() {
        assertEquals(0, n("5").compareTo(svc.add(n("2"), n("3"))));
    }

    @Test
    void addHandlesNegativesAndDecimals() {
        assertEquals(0, n("-0.5").compareTo(svc.add(n("-2"), n("1.5"))));
    }

    @Test
    void subtractReturnsDifference() {
        assertEquals(0, n("-1").compareTo(svc.subtract(n("2"), n("3"))));
    }

    @Test
    void multiplyReturnsProduct() {
        assertEquals(0, n("6").compareTo(svc.multiply(n("2"), n("3"))));
    }

    @Test
    void multiplyByZeroIsZero() {
        assertEquals(0, BigDecimal.ZERO.compareTo(svc.multiply(n("99"), n("0"))));
    }

    @Test
    void divideReturnsQuotient() {
        assertEquals(0, n("2.5").compareTo(svc.divide(n("5"), n("2"))));
    }

    @Test
    void divideNonTerminatingIsRounded() {
        assertEquals(0, n("0.333333333333333").compareTo(svc.divide(n("1"), n("3"))));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(DivisionByZeroException.class, () -> svc.divide(n("1"), n("0")));
    }

    @Test
    void divideByZeroDecimalFormThrows() {
        assertThrows(DivisionByZeroException.class, () -> svc.divide(n("1"), n("0.00")));
    }
}
