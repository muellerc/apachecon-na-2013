package org.apache.cmueller.camel.apachecon.na2013;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class StringBuilderAggregatingStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            newExchange.getIn().setBody(new StringBuilder("\n").append(newExchange.getIn().getBody(String.class)));
            return newExchange;
        }
 
        oldExchange.getIn().getBody(StringBuilder.class).append("\n").append(newExchange.getIn().getBody(String.class));
        
        return oldExchange;
    }
}