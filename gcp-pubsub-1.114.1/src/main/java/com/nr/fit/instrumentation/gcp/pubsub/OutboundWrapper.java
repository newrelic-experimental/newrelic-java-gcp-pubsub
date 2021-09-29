package com.nr.fit.instrumentation.gcp.pubsub;

import java.util.HashMap;
import java.util.Map;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {
	
	Map<String, String> attributes = null;
	
	public OutboundWrapper(Map<String, String> attrs) {
		attributes = new HashMap<String, String>(attrs);
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		attributes.put(name, value);
	}

	public Map<String, String> getCurrent() {
		return attributes;
	}
}
