/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cmueller.camel.apachecon.na2013;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.jibx.JibxDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.cmueller.camel.apachecon.na2013.model.BuyStocks;
import org.apache.cmueller.camel.apachecon.na2013.model.Order;
import org.junit.Test;

public class JibxMarshalUnmarshalTest extends CamelTestSupport {

    private int repeatCounter = 10000;

    @Test
    public void measureJibxMarshalUnmarshalExecution() throws Exception {
        // http://jira.codehaus.org/browse/JIBX-465
        
        template.setDefaultEndpointUri("direct:start");
        BuyStocks payload = createBuyStocks();

        warmUp(payload);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(payload);
        }
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("measureJibxMarshalUnmarshal duration: " + watch.stop() + "ms");
    }

    private BuyStocks createBuyStocks() {
        BuyStocks payload = new BuyStocks();
        payload.setOrder(new ArrayList<Order>());
        payload.getOrder().add(new Order("IBM", "asankha", 140.34, 2000));
        payload.getOrder().add(new Order("MSFT", "ruwan", 23.56, 8030));
        payload.getOrder().add(new Order("SUN", "indika", 14.56, 50000));
        payload.getOrder().add(new Order("GOOG", "chathura", 60.24, 40000));
        return payload;
    }

    private void warmUp(Object paylaod) throws Exception {
        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }

        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        getMockEndpoint("mock:end").reset();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                JibxDataFormat jibx = new JibxDataFormat(BuyStocks.class);

                from("direct:start")
                    .marshal(jibx)
                    .unmarshal(jibx)
                    .to("mock:end");
            }
        };
    }
}