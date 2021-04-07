package com.google.cloud.pubsub.v1;

import com.google.pubsub.v1.PublishRequest;
import com.google.pubsub.v1.PublishResponse;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageProduceParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class TopicAdminClient {

	@Trace(dispatcher=true)
	PublishResponse publish(PublishRequest request) {
		String topicName = request.getTopic();
		if(topicName != null) {
			topicName = topicName.replace('/', '_');
			MessageProduceParameters params = MessageProduceParameters.library("GPC-PubSub").destinationType(DestinationType.NAMED_TOPIC).destinationName(topicName).outboundHeaders(null).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		return Weaver.callOriginal();
	}
}
