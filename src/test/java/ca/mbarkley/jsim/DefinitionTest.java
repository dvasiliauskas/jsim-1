package ca.mbarkley.jsim;

import ca.mbarkley.jsim.eval.Evaluation;
import ca.mbarkley.jsim.eval.Parser;
import ca.mbarkley.jsim.model.Expression;
import ca.mbarkley.jsim.prob.Event;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

public class DefinitionTest {
    Parser parser = new Parser();

    @Test
    public void definitionYieldsNoExpressions() {
        final List<Expression<?>> stmts = parser.parse("define myRoll = 2d6 + 1").getExpressions();

        assertThat(stmts).isEmpty();
    }

    @Test
    public void definitionInContext() {
        final Evaluation eval = parser.parse("define myRoll = 2d6 + 1");

        assertThat(eval.getContext()
                       .getDefinitions()).containsOnlyKeys("myRoll")
                                         .hasEntrySatisfying("myRoll", exp -> assertThat(exp).hasToString("2d6 + 1"));
    }

    @Test
    public void evaluateDefinition() {
        final Evaluation eval = parser.parse("define myRoll = d6 + 1; myRoll > 6");

        assertThat(eval.getExpressions()).hasSize(1);
        final Map<Boolean, Double> result = eval.getExpressions()
                                                .get(0)
                                                .calculateResults()
                                                .entrySet()
                                                .stream()
                                                .collect(toMap(e -> (Boolean) e.getKey(), e -> e.getValue().getProbability()));
        final Offset<Double> offset = offset(0.0001);
        assertThat(result).hasEntrySatisfying(true, prob -> assertThat(prob).isCloseTo(1.0 / 6.0, offset))
                          .hasEntrySatisfying(false, prob -> assertThat(prob).isCloseTo(5.0 / 6.0, offset))
                          .containsOnlyKeys(true, false);
    }
}
