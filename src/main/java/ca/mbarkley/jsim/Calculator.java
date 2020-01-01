package ca.mbarkley.jsim;

import ca.mbarkley.jsim.model.Question;
import ca.mbarkley.jsim.prob.Event;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Map;
import java.util.stream.Stream;

import static ca.mbarkley.jsim.prob.Event.productOfIndependent;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Builder
public class Calculator {
    private final int scale;

    public Map<Boolean, Event<Boolean>> calculateResult(Question question) {
        final Stream<Event<Integer>> left = question.getLeft().events(scale);
        final Stream<Event<Integer>> right = question.getRight().events(scale);

        return productOfIndependent(left,
                                    right,
                                    (l, r) -> question.getComparator().evaluate(l, r))
                .collect(toMap(Event::getValue, identity()));
    }
}
