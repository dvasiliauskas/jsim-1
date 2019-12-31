package ca.mbarkley.jsim.model;

import ca.mbarkley.jsim.prob.Event;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ca.mbarkley.jsim.prob.Event.productOfIndependent;
import static com.codepoetics.protonpack.StreamUtils.unfold;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public abstract class Expression extends Statement {
    private Expression() {}

    public abstract ExpressionType getType();

    public abstract Stream<Event<Integer>> events(int scale);

    @Value
    public static class Constant extends Expression {
        int value;

        @Override
        public ExpressionType getType() {
            return ExpressionType.CONSTANT;
        }

        @Override
        public Stream<Event<Integer>> events(int scale) {
            return Stream.of(new Event<>(value, BigDecimal.ONE));
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }

    @Value
    public static class HomogeneousDicePool extends Expression {
        int numberOfDice;
        int diceSides;

        @Override
        public ExpressionType getType() {
            return ExpressionType.HOMOGENEOUS;
        }

        @Override
        public Stream<Event<Integer>> events(int scale) {
            final List<Stream<Event<Integer>>> singleDieStreams = Stream.generate(() -> singleDieEvents(scale))
                                                                        .limit(numberOfDice)
                                                                        .collect(toList());

            return productOfIndependent(singleDieStreams, Integer::sum);
        }

        private Stream<Event<Integer>> singleDieEvents(int scale) {
            return unfold(new Event(1, BigDecimal.ONE.divide(BigDecimal.valueOf(diceSides), scale, RoundingMode.HALF_EVEN)), e -> {
                if (e.getValue() < diceSides) {
                    return Optional.of(new Event(e.getValue() + 1, e.getProbability()));
                } else {
                    return Optional.empty();
                }
            });
        }

        @Override
        public String toString() {
            return format("%dd%d", numberOfDice, diceSides);
        }
    }

    @Value
    public static class BinaryOpExpression extends Expression {
        Expression left;
        Operator operator;
        Expression right;

        @Override
        public ExpressionType getType() {
            return ExpressionType.BINARY;
        }

        @Override
        public Stream<Event<Integer>> events(int scale) {
            return productOfIndependent(left.events(scale), right.events(scale), operator::apply);
        }

        @Override
        public String toString() {
            return format("%s %s %s", left, operator, right);
        }
    }

    public enum Operator {
        PLUS("+") {
            @Override
            public int apply(int left, int right) {
                return left + right;
            }
        }, MINUS("-") {
            @Override
            public int apply(int left, int right) {
                return left - right;
            }
        };

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol;
        }

        public abstract int apply(int left, int right);
    }

    public enum ExpressionType {
        CONSTANT, HOMOGENEOUS, BINARY;
    }
}