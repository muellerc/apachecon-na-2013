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

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

public class TimeConsumingProcessingSingleThreaded extends CamelTestSupport {

    private int repeatCounter = 1000;

    @Test
    public void measureTimeConsumingProcessingSingleThreaded() throws Exception {
        template.setDefaultEndpointUri("seda:start");

        warmUp(100);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody("PAYLOAD");
        }
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("measureTimeConsumingProcessingSingleThreaded duration: " + watch.stop() + "ms");
    }

    private void warmUp(int count) throws Exception {
        getMockEndpoint("mock:end").expectedMessageCount(count);

        for (int i = 0; i < count; i++) {
            template.sendBody("PAYLOAD");
        }

        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        getMockEndpoint("mock:end").reset();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("seda:start")
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            Thread.sleep(10);
                        }
                    }).to("mock:end");
            }
        };
    }
}