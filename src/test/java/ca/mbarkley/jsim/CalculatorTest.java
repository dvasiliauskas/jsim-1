package ca.mbarkley.jsim;

import ca.mbarkley.jsim.model.Question;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CalculatorTest {
    Calculator calculator = new Calculator(1000);
    Parser parser = new Parser();

    @Test
    public void rollLessThanConstant() {
        final Question question = parser.parseQuestion("d6 < 4");

        final double result = calculator.calculateProbability(question);

        Assert.assertEquals(0.5, result, 0.0001);
    }

    @Test
    public void complexRollGreaterThanConstant() {
        final Question question = parser.parseQuestion("2d6 + 1 > 6");

        final double result = calculator.calculateProbability(question);

        Assert.assertEquals(0.722, result, 0.001);
    }

    @Test
    public void complexRollLessThanComplexRoll() {
        final Question question = parser.parseQuestion("2d6 + 1 < 2d8 - 4");

        final double result = calculator.calculateProbability(question);

        Assert.assertEquals(0.20095, result, 0.00001);
    }

    @Test
    public void constantLessThanConstant() {
        final Question question = parser.parseQuestion("1 < 2");

        final double result = calculator.calculateProbability(question);

        Assert.assertEquals(1.0, result, 0.00001);
    }

    @Test
    @Ignore
    public void bigQuestion() {
        final Question question = parser.parseQuestion("2d6 + d8 - d12 + 3d20 < 20 + 5d4");

        final double result = calculator.calculateProbability(question);

        Assert.assertEquals(1.0, result, 0.00001);
    }
}
