package edu.cse816.calculator;

import java.math.BigDecimal;

public class CalculationResponse {
    private final String operation;
    private final BigDecimal a;
    private final BigDecimal b;
    private final BigDecimal result;

    public CalculationResponse(String operation, BigDecimal a, BigDecimal b, BigDecimal result) {
        this.operation = operation;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    public String getOperation() { return operation; }
    public BigDecimal getA() { return a; }
    public BigDecimal getB() { return b; }
    public BigDecimal getResult() { return result; }
}
