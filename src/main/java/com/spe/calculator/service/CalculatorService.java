package com.spe.calculator.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.spe.calculator.exception.DivisionByZeroException;

@Service
public class CalculatorService {

    private static final MathContext DIV_CONTEXT = new MathContext(15, RoundingMode.HALF_UP);

    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new DivisionByZeroException("Division by zero is not allowed");
        }
        return a.divide(b, DIV_CONTEXT);
    }
}
