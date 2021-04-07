package com.google.cloud.pubsub.v1;

import com.google.api.core.ApiFuture;
import com.google.pubsub.v1.PubsubMessage;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.fit.instrumentation.gcp.pubsub.Utils;

@Weave
public abstract class Publisher {

	public abstract String getTopicNameString();
	
	@Trace
	public ApiFuture<String> publish(PubsubMessage message) {
		String topicName = Utils.parseTopic(getTopicNameString()).replace('/', '_');
		message = Utils.populateHeaders(message);
		MessageProduceParameters params = MessageProduceParameters.library("GCP-PubSub").destinationType(DestinationType.NAMED_TOPIC).destinationName(topicName).outboundHeaders(null).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_LOW, false, "GCP-PubSub", "GCP-PubSub","Publish",topicName);
		return Weaver.callOriginal();
	}
}
