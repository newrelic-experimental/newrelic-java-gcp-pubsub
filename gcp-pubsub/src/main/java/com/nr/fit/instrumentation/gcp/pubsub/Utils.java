package com.nr.fit.instrumentation.gcp.pubsub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient.ListSubscriptionsPagedResponse;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.Subscription;
import com.newrelic.api.agent.NewRelic;

public class Utils implements Runnable {

	private static HashMap<String, String> subscriptionToTopic = new HashMap<String, String>();

	private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static final List<ProjectSubscriptionName> projectNames = new ArrayList<ProjectSubscriptionName>();


	private static boolean initialized = false;
	private static Utils instance = null;
	private static SubscriptionAdminClient client = null;

	static {
		if(!initialized) {
			instance = new Utils();
			try {
				client = SubscriptionAdminClient.create();
			} catch (IOException e) {
			}

			executor.scheduleAtFixedRate(instance, 0, 30, TimeUnit.SECONDS);
		}
	}


	private Utils() {

	}

	public static void addProjectName(ProjectSubscriptionName project) {
		if(!projectNames.contains(project)) {
			projectNames.add(project);
		}
	}

	public static void add(String subName, String topicName) {
		String tName = topicName.replace('/', '_');
		subscriptionToTopic.put(subName, tName);
	}

	public static String getTopic(String subName) {
		return subscriptionToTopic.get(subName);
	}

	public static void remove(String subName) {
		if(subName != null) {
			subscriptionToTopic.remove(subName);
		}
	}

	public static boolean contains(String subName) {
		return subscriptionToTopic.containsKey(subName);
	}

	public static void checkSubscription(Subscription sub) {
		String subName = sub.getName();
		if(subName == null || subscriptionToTopic.containsKey(subName)) return;

		String topicName = sub.getTopic();
		if(topicName == null || topicName.isEmpty()) return;

		String tName = parseTopic(topicName).replace('/', '_');
		subscriptionToTopic.put(subName, tName);
	}
	
	public static String parseTopic(String tName) {
		int index = tName.indexOf("/topics/");
		if(index > -1) {
			return tName.substring(index+"/topics/".length());
		}
		return tName;
	}

	public static PubsubMessage populateHeaders(PubsubMessage message) {
		Map<String, String> attrs = message.getAttributesMap();
		OutboundWrapper wrapper = new OutboundWrapper(attrs);
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		return PubsubMessage.newBuilder(message).clearAttributes().putAllAttributes(wrapper.getCurrent()).build();

	}

	@Override
	public void run() {

		for(ProjectSubscriptionName project : projectNames) {
			String pName = project.getProject();
			ProjectName projectName = ProjectName.of(pName);
			try {

				ListSubscriptionsPagedResponse response = client.listSubscriptions(projectName);
				Iterable<Subscription> iterable = response.iterateAll();
				Iterator<Subscription> iterator = iterable.iterator();
				while(iterator.hasNext()) {
					Subscription sub = iterator.next();
					checkSubscription(sub);
				}
			} catch (Exception e) {
				NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Failed to get subscriptions due to error");
			}
		}
	}

}
