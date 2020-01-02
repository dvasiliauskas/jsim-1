package ca.mbarkley.jsim;

import ca.mbarkley.jsim.model.Statement;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DisplayerTest {

    Parser parser = new Parser();
    Displayer displayer = new Displayer(120);

    @Test
    public void simpleHistogram() {
        final Statement<?> stmt = parser.parse("2d6");
        final String sortedHistogram = displayer.createSortedHistogram(stmt.calculateResults().values().stream());
        assertThat(sortedHistogram).isEqualTo(
                        "2  |******************                                                                                                  2.78%\n" +
                        "3  |*************************************                                                                               5.56%\n" +
                        "4  |*******************************************************                                                             8.33%\n" +
                        "5  |**************************************************************************                                          11.11%\n" +
                        "6  |********************************************************************************************                        13.89%\n" +
                        "7  |***************************************************************************************************************     16.67%\n" +
                        "8  |********************************************************************************************                        13.89%\n" +
                        "9  |**************************************************************************                                          11.11%\n" +
                        "10 |*******************************************************                                                             8.33%\n" +
                        "11 |*************************************                                                                               5.56%\n" +
                        "12 |******************                                                                                                  2.78%\n"
        );
    }
}