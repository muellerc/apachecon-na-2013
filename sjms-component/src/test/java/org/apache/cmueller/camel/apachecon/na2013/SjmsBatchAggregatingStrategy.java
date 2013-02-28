package org.apache.cmueller.camel.apachecon.na2013;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.component.sjms.BatchMessage;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class SjmsBatchAggregatingStrategy implements AggregationStrategy {

    @SuppressWarnings("unchecked")
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            List<BatchMessage<String>> messages = new ArrayList<BatchMessage<String>>();
            messages.add(new BatchMessage<String>(newExchange.getIn().getBody(String.class), null));
            newExchange.getIn().setBody(messages);

            return newExchange;
        }

        oldExchange.getIn().getBody(List.class).add(new BatchMessage<String>(newExchange.getIn().getBody(String.class), null));

        return oldExchange;
    }
}