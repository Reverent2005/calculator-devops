package edu.cse816.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService {
    public BigDecimal add(BigDecimal a, BigDecimal b) { return a.add(b); }
    public BigDecimal subtract(BigDecimal a, BigDecimal b) { return a.subtract(b); }
    public BigDecimal multiply(BigDecimal a, BigDecimal b) { return a.multiply(b); }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (BigDecimal.ZERO.compareTo(b) == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return a.divide(b, 10, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}
