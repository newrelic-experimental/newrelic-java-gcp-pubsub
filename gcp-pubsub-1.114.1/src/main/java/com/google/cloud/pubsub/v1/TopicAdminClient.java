package com.google.cloud.pubsub.v1;

import java.util.HashMap;

import com.google.pubsub.v1.PublishRequest;
import com.google.pubsub.v1.PublishResponse;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.fit.instrumentation.gcp.pubsub.Utils;

@Weave
public abstract class TopicAdminClient {

	@Trace(dispatcher=true)
	public PublishResponse publish(PublishRequest request) {
		String topicName = request.getTopic();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("Topic", topicName);
		Utils.addAttribute(attributes, "MessageCount", request.getMessagesCount());
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		if(topicName != null) {
			topicName = topicName.replace('/', '_');
			MessageProduceParameters params = MessageProduceParameters.library("GPC-PubSub").destinationType(DestinationType.NAMED_TOPIC).destinationName(topicName).outboundHeaders(null).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		return Weaver.callOriginal();
	}
}
