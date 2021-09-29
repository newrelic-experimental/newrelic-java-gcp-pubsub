package com.google.cloud.pubsub.v1;

import com.google.pubsub.v1.DeleteSubscriptionRequest;
import com.google.pubsub.v1.Subscription;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.fit.instrumentation.gcp.pubsub.Utils;

@Weave(originalName="com.google.cloud.pubsub.v1.SubscriptionAdminClient")
public abstract class SubscriptionAdminClient_instrumentation {
	
	public Subscription createSubscription(Subscription request) {
		Utils.checkSubscription(request);
		Subscription response = Weaver.callOriginal();
		Utils.checkSubscription(response);
		return response;
	}
	
	public final void deleteSubscription(DeleteSubscriptionRequest request) {
		String subName = request.getSubscription();
		Utils.remove(subName);
		Weaver.callOriginal();
	}

}
