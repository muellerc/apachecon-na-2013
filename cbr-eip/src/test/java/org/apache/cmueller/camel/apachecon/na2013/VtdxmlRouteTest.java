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
import org.apacheextras.camel.component.vtdxml.VtdXmlXPathBuilder;

public class VtdxmlRouteTest extends BaseRouteTest {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                VtdXmlXPathBuilder predicate =
                    new VtdXmlXPathBuilder("/soapenv:Envelope/soapenv:Body/s:buyStocks/order[5]/symbol='IBM'")
                        .namespace("soapenv", "http://schemas.xmlsoap.org/soap/envelope/")
                        .namespace("s", "http://services.samples/xsd");

                from("direct:start")
                    .choice()
                        .when(predicate)
                            .to("mock:end")
                    .end();
            }
        };
    }
}