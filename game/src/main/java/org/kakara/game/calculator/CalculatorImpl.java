package org.kakara.game.calculator;

import org.kakara.core.common.calculator.Calculator;
import org.kakara.core.common.calculator.CalculatorKey;

import java.util.List;

public class CalculatorImpl {
    private List<Calculator> calculatorList;
    private Calculator setCalculator;
    private CalculatorKey key;

    public CalculatorImpl(CalculatorKey key) {
        this.key = key;
    }

    public void add(Calculator calculator) {
        calculatorList.add(calculator);
    }

    public void setSetCalculator(Calculator calculator) {
        setCalculator = calculator;
        calculatorList.add(calculator);
    }

    public List<Calculator> getCalculatorList() {
        return calculatorList;
    }

    public Calculator getSetCalculator() {
        return setCalculator;
    }

    public CalculatorKey getKey() {
        return key;
    }
}
