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
package org.apache.cmueller.camel.apachecon.na2013.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order", propOrder = {
    "symbol",
    "buyerID",
    "price",
    "volume"
})
public class Order {

    @XmlElement(required = true)
    protected String symbol;
    @XmlElement(required = true)
    protected String buyerID;
    protected double price;
    protected int volume;

    public Order() {
    }

    public Order(String symbol, String buyerID, double price, int volume) {
        super();
        this.symbol = symbol;
        this.buyerID = buyerID;
        this.price = price;
        this.volume = volume;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String value) {
        this.symbol = value;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String value) {
        this.buyerID = value;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double value) {
        this.price = value;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int value) {
        this.volume = value;
    }
}