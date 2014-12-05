package com.danthe.dogeescape;

import java.util.Collections;
import java.util.LinkedList;

import com.danthe.dogeescape.interfaces.IResourceProvider;

public class SupportiveMessageManager {

	private static SupportiveMessageManager instance;
	private IResourceProvider resourceProvider;
	private LinkedList<String> messages;

	private int lastID = 0;

	private SupportiveMessageManager(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;

		initMessages();
	}

	private void initMessages() {
		if (messages == null) {
			String[] messagesArray = {
					resourceProvider.getString(R.string.SM_0),
					resourceProvider.getString(R.string.SM_1),
					resourceProvider.getString(R.string.SM_2),
					resourceProvider.getString(R.string.SM_3),
					resourceProvider.getString(R.string.SM_4),
					resourceProvider.getString(R.string.SM_5),
					resourceProvider.getString(R.string.SM_6),
					resourceProvider.getString(R.string.SM_7),
					resourceProvider.getString(R.string.SM_8),
					resourceProvider.getString(R.string.SM_9),
					resourceProvider.getString(R.string.SM_10),
					resourceProvider.getString(R.string.SM_11),
					resourceProvider.getString(R.string.SM_12) };
			messages = new LinkedList<String>();
			for (String s : messagesArray)
				messages.add(s);
		}

		Collections.shuffle(messages);
	}

	/**
	 * @return supportiveMessage might consist of several lines in different
	 *         fields of the array.
	 */
	public String getSupportiveMessage() {
		if (lastID >= messages.size())
			initMessages();
		// since its lastID++ it won't increment until after the message has
		// been returned
		return messages.get(lastID++);
	}

	public static SupportiveMessageManager getInstance() {
		if (instance == null)
			throw new RuntimeException("Hasnt been initialized");
		return instance;
	}

	public static void init(IResourceProvider resourceProvider) {
		instance = new SupportiveMessageManager(resourceProvider);
	}
}
