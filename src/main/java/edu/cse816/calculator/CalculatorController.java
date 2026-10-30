package edu.cse816.calculator;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CalculatorController {
    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/add")
    public CalculationResponse add(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return calculate("add", a, b, calculatorService::add);
    }

    @GetMapping("/subtract")
    public CalculationResponse subtract(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return calculate("subtract", a, b, calculatorService::subtract);
    }

    @GetMapping("/multiply")
    public CalculationResponse multiply(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return calculate("multiply", a, b, calculatorService::multiply);
    }

    @GetMapping("/divide")
    public CalculationResponse divide(@RequestParam BigDecimal a, @RequestParam BigDecimal b) {
        return calculate("divide", a, b, calculatorService::divide);
    }

    private CalculationResponse calculate(String operation, BigDecimal a, BigDecimal b,
                                          BiFunction<BigDecimal, BigDecimal, BigDecimal> fn) {
        return new CalculationResponse(operation, a, b, fn.apply(a, b));
    }
}
