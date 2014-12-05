package com.danthe.dogeescape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.danthe.dogeescape.interfaces.IResourceProvider;

public class SupportiveMessageManager {

	private static SupportiveMessageManager instance;
	private IResourceProvider resourceProvider;
	private Queue<String[]> messages;
	
	private int lastID = 0;
	
	private SupportiveMessageManager(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;

		initMessages();
	}

	private void initMessages() {
		String [][] messages = {
				{resourceProvider.getString(R.string.SM_0)},
				{resourceProvider.getString(R.string.SM_1)},
				{resourceProvider.getString(R.string.SM_2)},
				{resourceProvider.getString(R.string.SM_3)},
				{resourceProvider.getString(R.string.SM_4)},
				{resourceProvider.getString(R.string.SM_5)},
				{resourceProvider.getString(R.string.SM_6)},
				{resourceProvider.getString(R.string.SM_7)},
				{resourceProvider.getString(R.string.SM_8)},
				{resourceProvider.getString(R.string.SM_9)},
				{resourceProvider.getString(R.string.SM_10)},
				{resourceProvider.getString(R.string.SM_11)},
				{resourceProvider.getString(R.string.SM_12)}
		};
		List<String[]> lMessages = new LinkedList<String[]>();
		for(String[] s: messages) lMessages.add(s);
		
		Collections.shuffle(lMessages);
		this.messages =new LinkedList<String[]>(lMessages);
	}

	/**
	 * @return supportiveMessage Might consist of several lines in different fields of the array.
	 */
	public String[] getSupportiveMessage() {
		if (messages.isEmpty()) initMessages();
		return messages.poll();
	}
	
	public static SupportiveMessageManager getInstance() {
		if (instance == null) throw new RuntimeException("Hasnt been initialized");
		return instance;
	}
	
	public static void init(IResourceProvider resourceProvider) {
		instance = new SupportiveMessageManager(resourceProvider);
		
		
	}
}
