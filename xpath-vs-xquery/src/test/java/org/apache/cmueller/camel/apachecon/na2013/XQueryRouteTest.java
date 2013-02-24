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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XQueryRouteTest extends CamelTestSupport {

    private int repeatCounter = 10000;

    @Test
    public void measureXQueryExecution() throws Exception {
        template.setDefaultEndpointUri("direct:xquery");
        String paylaod = IOUtils.toString(new FileInputStream("src/test/data/10K_buyStocks.xml"), "UTF-8");

        // warm up
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }

        getMockEndpoint("mock:xquery").reset();
        getMockEndpoint("mock:xquery").expectedMessageCount(repeatCounter);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }
        assertMockEndpointsSatisfied();

        System.out.println("measureXQueryExecution duration: " + watch.stop() + "ms");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                Namespaces ns = new Namespaces("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
                ns.add("s", "http://services.samples/xsd");

                from("direct:xquery")
                    .choice()
                        .when().xquery("/soapenv:Envelope/soapenv:Body/s:buyStocks/order[5]/symbol='IBM'", ns)
                            .to("mock:xquery")
                    .end();
            }
        };
    }
}