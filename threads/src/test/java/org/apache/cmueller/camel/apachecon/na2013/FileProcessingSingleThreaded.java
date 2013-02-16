package org.apache.cmueller.camel.apachecon.na2013;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.StopWatch;
import org.junit.Test;

public class FileProcessingSingleThreaded extends CamelTestSupport {
	
    @Test
    public void measureFileProcessingSingleThreaded() throws Exception {
        getMockEndpoint("mock:end").setExpectedMessageCount(10);
        
        StopWatch watch = new StopWatch();
        
        context.startRoute("file-reader");
        assertMockEndpointsSatisfied(1, TimeUnit.MINUTES);
        
        watch.stop();
        
        System.out.println("measureFileProcessingSingleThreaded duration: " + watch.taken() + "ms");
    }

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
                from("file:src/test/data?noop=true&initialDelay=0").routeId("file-reader").autoStartup(false)
	                .process(new Processor() {
	                    public void process(Exchange exchange) throws Exception {
	                    	// simulate expensive processing
	                        Thread.sleep(1000);
	                    }
	                }).to("mock:end");
			}
		};
	}
}