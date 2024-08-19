package com.gxy.resp;

import java.util.List;
import java.util.Map;

/**
 * @author pengyonglei
 * @version 1.0.0
 */
public class ChatCompletionResp {

	private String id;
	private String object;
	private long created;
	private String model;
	private List<ChatCompletionChoice> choices;
	private Map<String, Integer> usage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChatCompletionChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<ChatCompletionChoice> choices) {
		this.choices = choices;
	}

	public Map<String, Integer> getUsage() {
		return usage;
	}

	public void setUsage(Map<String, Integer> usage) {
		this.usage = usage;
	}
}
