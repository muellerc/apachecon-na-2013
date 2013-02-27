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
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class SqlSimpleTest extends CamelTestSupport {

    private int repeatCounter = 10000;
    private DataSource datasource;

    @Before
    public void setUp() throws Exception {
        datasource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.DERBY)
            .addScript("sql/init_database.sql")
            .build();

        super.setUp();
    }
    
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("datasource", datasource);
        return registry;
    }

    @Test
    public void measureSqlSimpleExecution() throws Exception {
        template.setDefaultEndpointUri("direct:start");
        List<Object> paylaod = new ArrayList<Object>();
        paylaod.add("IBM");
        paylaod.add("cmueller");
        paylaod.add(140.34);
        paylaod.add(2000);

        warmUp(paylaod);

        getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBody(paylaod);
        }
        assertMockEndpointsSatisfied(5, TimeUnit.MINUTES);

        System.out.println("measureSqlSimpleExecution duration: " + watch.stop() + "ms");
    }

    private void warmUp(List<Object> paylaod) throws InterruptedException {
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
                    .to("sql:insert into orders (symbol, buyer, price, volume) values (#, #, #, #)?dataSourceRef=datasource")
                    .to("mock:end");
            }
        };
    }
}
