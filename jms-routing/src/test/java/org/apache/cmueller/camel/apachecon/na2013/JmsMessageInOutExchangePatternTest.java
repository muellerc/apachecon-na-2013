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

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JmsMessageInOutExchangePatternTest extends CamelTestSupport {

    private BrokerService broker;
    private int repeatCounter = 100;

    @Before
    public void setUp() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector("tcp://localhost:61616");
        broker.start();
                
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        
        broker.stop();
    }

    @Test
    public void measureJmsInOutExecution() throws Exception {
        template.setDefaultEndpointUri("direct:start");
        String paylaod = IOUtils.toString(new FileInputStream("src/test/data/1K_buyStocks.xml"), "UTF-8");

        warmUp(paylaod);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }
        assertMockEndpointsSatisfied(5, TimeUnit.MINUTES);
        watch.stop();

        System.out.println("measureJmsInOutExecution duration: " + watch.taken() + "ms");
    }

    private void warmUp(String paylaod) throws InterruptedException {
        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }
        assertMockEndpointsSatisfied(5, TimeUnit.MINUTES);
        getMockEndpoint("mock:end").reset();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                    .inOut("activemq:queue:INOUT")
                    .to("seda:end")
                    .to("mock:end");
                
                from("activemq:queue:INOUT?concurrentConsumers=5") // no performance improvement
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            Thread.sleep(50);
                        }
                    });
            }
        };
    }
}