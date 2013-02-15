package org.apache.cmueller.camel.apachecon.na2013;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class StringAggregatingStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            newExchange.getIn().setBody("\n" + newExchange.getIn().getBody(String.class));
            return newExchange;
        }
 
        oldExchange.getIn().setBody(oldExchange.getIn().getBody(String.class) + "\n" + newExchange.getIn().getBody(String.class));
        
        return oldExchange;
    }
}