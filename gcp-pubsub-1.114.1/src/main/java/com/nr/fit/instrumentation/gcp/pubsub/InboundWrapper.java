package com.nr.fit.instrumentation.gcp.pubsub;

import java.util.Map;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {
	
	Map<String, String> attributes = null;
	
	public InboundWrapper(Map<String, String> attrs) {
		attributes = attrs;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return attributes.get(name);
	}

}
