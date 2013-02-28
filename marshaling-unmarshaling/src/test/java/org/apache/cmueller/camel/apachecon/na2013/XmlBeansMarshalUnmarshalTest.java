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

import samples.services.xsd.BuyStocksDocument;
import samples.services.xsd.BuyStocksDocument.BuyStocks;
import samples.services.xsd.Order;

public class XmlBeansMarshalUnmarshalTest extends MarshalUnmarshalBaseTest {

    protected BuyStocksDocument createBuyStocks() {
        BuyStocksDocument document = BuyStocksDocument.Factory.newInstance();
        BuyStocks payload = document.addNewBuyStocks();
        Order order = payload.addNewOrder();
        order.setSymbol("IBM");
        order.setBuyerID("asankha");
        order.setPrice(140.34);
        order.setVolume(2000);
        
        order = payload.addNewOrder();
        order.setSymbol("MSFT");
        order.setBuyerID("ruwan");
        order.setPrice(23.56);
        order.setVolume(8030);
        
        order = payload.addNewOrder();
        order.setSymbol("SUN");
        order.setBuyerID("indika");
        order.setPrice(14.56);
        order.setVolume(50000);
        
        order = payload.addNewOrder();
        order.setSymbol("GOOG");
        order.setBuyerID("chathura");
        order.setPrice(60.24);
        order.setVolume(40000);
        
        return document;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:start")
                    .marshal().xmlBeans()
                    .unmarshal().xmlBeans()
                    .to("mock:end");
            }
        };
    }
}