package org.apache.cmueller.camel.apachecon.na2013;

import java.io.FileInputStream;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class HeaderRouteTest extends CamelTestSupport {
	
	private int repeatCounter = 10000;
	
	@Test
	public void measureHeaderExecution() throws Exception {
		template.setDefaultEndpointUri("direct:header");
		String paylaod = IOUtils.toString(new FileInputStream("src/test/data/10K_buyStocks.xml"), "UTF-8");

		// warm up
		for (int i = 0; i < repeatCounter; i++) {
			template.sendBodyAndHeader(paylaod, "ROUTING_CONDITION", "IBM");
		}

		getMockEndpoint("mock:header").reset();
		getMockEndpoint("mock:header").expectedMessageCount(repeatCounter);
		
		StopWatch watch = new StopWatch();
		for (int i = 0; i < repeatCounter; i++) {
			template.sendBodyAndHeader(paylaod, "ROUTING_CONDITION", "IBM");
		}
		watch.stop();
		
		System.out.println("measureHeaderExecution duration: " + watch.taken() + "ms");
		
		assertMockEndpointsSatisfied();
	}
	
	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from("direct:header")
				   .choice()
				      .when(header("ROUTING_CONDITION").isEqualTo("IBM"))
				      	.to("mock:header")
				   .end(); 
			}
		};
	}
}