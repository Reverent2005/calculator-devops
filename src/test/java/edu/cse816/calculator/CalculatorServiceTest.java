package edu.cse816.calculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CalculatorServiceTest {
    private final CalculatorService service = new CalculatorService();

    @Test void addsNumbers() { assertEquals(new BigDecimal("5"), service.add(new BigDecimal("2"), new BigDecimal("3"))); }
    @Test void subtractsNumbers() { assertEquals(new BigDecimal("-1"), service.subtract(new BigDecimal("2"), new BigDecimal("3"))); }
    @Test void multipliesNumbers() { assertEquals(new BigDecimal("6"), service.multiply(new BigDecimal("2"), new BigDecimal("3"))); }
    @Test void dividesNumbers() { assertEquals(0, new BigDecimal("2.5").compareTo(service.divide(new BigDecimal("5"), new BigDecimal("2")))); }
    @Test void rejectsDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> service.divide(BigDecimal.ONE, BigDecimal.ZERO));
    }
}
