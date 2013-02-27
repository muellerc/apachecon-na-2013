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

import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CxfDataFormatTest extends CamelSpringTestSupport {

    private int repeatCounter = 10000;

    @Test
    public void measureExecution() throws Exception {
        template.setDefaultEndpointUri("http://localhost:8999/DirectProxy");
        String paylaod = IOUtils.toString(new FileInputStream("src/test/data/1K_buyStocks.xml"), "UTF-8");

        warmUp(paylaod);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        System.out.println("measureHeaderExecution duration: " + watch.stop() + "ms");
    }

    private void warmUp(String paylaod) throws Exception {
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
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
    }
}
