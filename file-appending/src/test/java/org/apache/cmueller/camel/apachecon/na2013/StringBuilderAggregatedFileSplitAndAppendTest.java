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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

public class StringBuilderAggregatedFileSplitAndAppendTest extends CamelTestSupport {

    @Test
    public void measureStringBuilderAggregatedFileSplitAndAppend() throws Exception {
        getMockEndpoint("mock:end").setExpectedMessageCount(1);

        StopWatch watch = new StopWatch();

        context.startRoute("splitter");
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);

        watch.stop();

        System.out.println("measureStringBuilderAggregatedFileSplitAndAppend duration: " + watch.taken() + "ms");
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("file://src/test/data?charset=UTF-8&noop=true&initialDelay=0").routeId("splitter").autoStartup(false)
                    .split(body().tokenize("\n")).streaming()
                        // do some processing
                        .aggregate(header("CamelFileName"), new StringBuilderAggregatingStrategy()).completionSize(1000).completionTimeout(200)
                            .convertBodyTo(String.class)
                            .to("file://target?charset=UTF-8&fileExist=Append")
                        .end()
                    .end()
                    .to("mock:end");
            }
        };
    }
}