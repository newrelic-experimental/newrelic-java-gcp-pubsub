package com.google.cloud.pubsub.v1;

import com.google.pubsub.v1.ProjectSubscriptionName;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.fit.instrumentation.gcp.pubsub.NRMessageReceiver;
import com.nr.fit.instrumentation.gcp.pubsub.Utils;

@Weave
public abstract class Subscriber {
	
	public static Builder newBuilder(String subscription, MessageReceiver receiver) {
		ProjectSubscriptionName subName = ProjectSubscriptionName.parse(subscription);
		Utils.addProjectName(subName);
		NRMessageReceiver wrapper = new NRMessageReceiver(receiver, subscription);
		receiver = wrapper;
		return Weaver.callOriginal();
	}
	
	@Weave
	public static final class Builder {
		
	}
}
