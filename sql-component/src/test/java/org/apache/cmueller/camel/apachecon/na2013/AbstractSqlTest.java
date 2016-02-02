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

import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSqlTest extends CamelTestSupport {

    int repeatCounter = 10000;
    private EmbeddedDatabase datasource;

    @Before
    public void setUp() throws Exception {
        datasource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.DERBY)
            .addScript("sql/init_database.sql")
            .build();

        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();

        datasource.shutdown();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        registry.bind("datasource", datasource);
        return registry;
    }

    public abstract int getExpectedMessageCount();

    @Test
    public void measureSqlSimpleExecution() throws Exception {
        template.setDefaultEndpointUri("direct:start");
        List<Object> paylaod = new ArrayList<Object>();
        paylaod.add("IBM");
        paylaod.add("cmueller");
        paylaod.add(140.34);
        paylaod.add(2000);

        // warm up
        execute(paylaod);

        long duration = execute(paylaod);
        System.out.println("duration: " + duration + "ms");
    }

    private long execute(List<Object> paylaod) throws InterruptedException {
        getMockEndpoint("mock:end").reset();
        getMockEndpoint("mock:end").expectedMessageCount(getExpectedMessageCount());
        getMockEndpoint("mock:end").setRetainFirst(0);
        getMockEndpoint("mock:end").setRetainLast(0);

        StopWatch watch = new StopWatch();
        for (int i = 0; i < repeatCounter; i++) {
            template.sendBodyAndHeader(paylaod, "AGG_KEY", "BATCH");
        }

        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        return watch.stop();
    }
}