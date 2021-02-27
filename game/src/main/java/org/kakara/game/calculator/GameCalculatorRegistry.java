package org.kakara.game.calculator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import org.kakara.core.common.calculator.Calculator;
import org.kakara.core.common.calculator.CalculatorKey;
import org.kakara.core.common.calculator.CalculatorRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GameCalculatorRegistry implements CalculatorRegistry {
    private List<CalculatorImpl> calculatorList = new ArrayList<>();

    @Override
    public void registerNewCalculator(Calculator calculator) {

    }

    @Override
    public List<Calculator> getCalculators(CalculatorKey key) {
        for (CalculatorImpl calculator : calculatorList) {
            if (calculator.getKey().equals(key)) {
                return calculator.getCalculatorList();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Calculator getSetCalculator(CalculatorKey key) {
        for (CalculatorImpl calculator : calculatorList) {
            if (calculator.getKey().equals(key)) {
                return calculator.getSetCalculator();
            }
        }
        return null;
    }
}
