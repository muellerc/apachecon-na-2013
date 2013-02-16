package org.apache.cmueller.camel.apachecon.na2013;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

public class TimeConsumingProcessingSingleThreaded extends CamelTestSupport {
	
	private int repeatCounter = 1000;
	
    @Test
    public void measureTimeConsumingProcessingSingleThreaded() throws Exception {
		template.setDefaultEndpointUri("seda:start");

		warmUp(100);
		
		getMockEndpoint("mock:end").expectedMessageCount(repeatCounter);
		
		StopWatch watch = new StopWatch();
		for (int i = 0; i < repeatCounter; i++) {
			template.sendBody("PAYLOAD");
		}
		assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
		watch.stop();
		
		System.out.println("measureTimeConsumingProcessingSingleThreaded duration: " + watch.taken() + "ms");
    }
    
    private void warmUp(int count) throws Exception {
    	getMockEndpoint("mock:end").expectedMessageCount(count);
    	
		for (int i = 0; i < count; i++) {
			template.sendBody("PAYLOAD");
		}
		
		assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
		getMockEndpoint("mock:end").reset();
    }

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from("seda:start")
				   .process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							Thread.sleep(10);
						}
				   })
				   .to("mock:end");
			}
		};
	}
}