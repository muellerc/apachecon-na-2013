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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MustacheTest extends CamelTestSupport {

    private int repeatCounter = 10000;

    @Test
    public void measureFreeMarkerExecution() throws Exception {
        template.setDefaultEndpointUri("direct:start");

        String payload = "The Camel riders";
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("name", "cmueller");
        headers.put("volume", "200");
        headers.put("symbol", "IBM");

        warmUp(payload, headers);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeaders(payload, headers);
        }
        assertMockEndpointsSatisfied();

        System.out.println("measureFreeMarkerExecution duration: " + watch.stop() + "ms");
    }

    private void warmUp(String payload, Map<String, Object> headers) throws Exception {
        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeaders(payload, headers);
        }

        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        getMockEndpoint("mock:end").reset();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                    .to("mustache:template.mustache")
                    .to("mock:end");
            }
        };
    }
}
