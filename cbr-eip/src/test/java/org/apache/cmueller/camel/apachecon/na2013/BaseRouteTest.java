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

import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public abstract class BaseRouteTest extends CamelTestSupport {

    protected int repeatCounter = 10000;

    @Test
    public void test() throws Exception {
        template.setDefaultEndpointUri("direct:start");
        String paylaod = IOUtils.toString(new FileInputStream("src/test/data/10K_buyStocks.xml"), "UTF-8");

        warmUp(paylaod);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeader(paylaod, "ROUTING_CONDITION", "IBM");
        }
        assertMockEndpointsSatisfied();

        System.out.println("duration: " + watch.stop() + "ms");
    }

    protected void warmUp(String paylaod) throws InterruptedException {
        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeader(paylaod, "ROUTING_CONDITION", "IBM");
        }

        assertMockEndpointsSatisfied();
        getMockEndpoint("mock:end").reset();
    }
}