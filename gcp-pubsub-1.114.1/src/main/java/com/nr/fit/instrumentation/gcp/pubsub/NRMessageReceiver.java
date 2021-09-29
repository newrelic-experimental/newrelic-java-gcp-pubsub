package com.nr.fit.instrumentation.gcp.pubsub;

import java.util.HashMap;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageConsumeParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;

public class NRMessageReceiver implements MessageReceiver {
	
	private MessageReceiver delegate = null;
	private String subName = null;
	private String topicName = null;
	
	private static boolean isTransformed = false;
	
	public NRMessageReceiver(MessageReceiver rec, String sName) {
		delegate = rec;
		subName = sName;
		if(!isTransformed) {
			isTransformed = true;
			AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
		}
	}

	@Override
	@Trace(dispatcher=true)
	public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		Utils.addAttribute(attributes, "TopicName", topicName);
		Utils.addAttribute(attributes, "SubName", subName);
		Utils.addMessage(attributes, message);
		
		if(topicName == null) {
			topicName = Utils.getTopic(subName);
		}
		String tName = topicName != null ? topicName : subName;
		InboundWrapper wrapper = new InboundWrapper(message.getAttributesMap());
		MessageConsumeParameters params = MessageConsumeParameters.library("GCP-PubSub").destinationType(DestinationType.NAMED_TOPIC).destinationName(tName).inboundHeaders(wrapper).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		if(delegate != null) {
			delegate.receiveMessage(message, consumer);
		}
	}

}
