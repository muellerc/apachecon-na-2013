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

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SjmsTransactionalBatchTest extends CamelTestSupport {

    private BrokerService broker;
    private int repeatCounter = 10000;

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

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        SjmsComponent component = new SjmsComponent();
        component.setConnectionFactory(connectionFactory);
        registry.bind("sjms", component);

        return registry;
    }

    @Test
    public void measureSjmsMessageInOnlySimpleExecution() throws Exception {
        template.setDefaultEndpointUri("direct:start");
        String paylaod = IOUtils.toString(new FileInputStream("src/test/data/1K_buyStocks.xml"), "UTF-8");

        warmUp(paylaod);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeader(paylaod, "AGG_KEY", "SJMS");
        }
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("measureSjmsMessageInOnlySimpleExecution duration: " + watch.stop() + "ms");
    }

    private void warmUp(String paylaod) throws InterruptedException {
        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeader(paylaod, "AGG_KEY", "SJMS");
        }
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        getMockEndpoint("mock:end").reset();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                    .aggregate(header("AGG_KEY"), new SjmsBatchAggregatingStrategy()).completionSize(1000).completionTimeout(100)
                        .inOnly("sjms:queue:INONLY");

                from("sjms:queue:INONLY?transacted=true&transactionBatchCount=1000")
                    .to("mock:end");
            }
        };
    }
}