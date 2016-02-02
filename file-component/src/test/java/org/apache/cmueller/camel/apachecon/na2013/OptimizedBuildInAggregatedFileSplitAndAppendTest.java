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

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class OptimizedBuildInAggregatedFileSplitAndAppendTest extends AbstractSplitterTest {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            ValueBuilder splitter = new ValueBuilder(new Expression() {

                @Override
                public <T> T evaluate(Exchange exchange, Class<T> type) {
                    String body = exchange.getIn().getBody(String.class);
                    String[] lines = body.split("\\n");

                    return exchange.getContext().getTypeConverter().convertTo(type, exchange, lines);
                }
            });

            public void configure() throws Exception {
                from("file://src/test/data?charset=UTF-8&noop=true&initialDelay=0").routeId("splitter").autoStartup(false)
                    .split(splitter, new StringBuilderAggregatingStrategy())
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                // do some processing here
                            }
                        })
                    .end()
                    .convertBodyTo(String.class)
                    .to("file://target?charset=UTF-8&fileExist=Append")
                    .to("mock:end");
            }
        };
    }
}