package com.gxy.resp;

import com.gxy.req.ChatCompletionMessage;

/**
 * @author pengyonglei
 * @version 1.0.0
 */
public class ChatCompletionChoice {

	private int index;
	private String finish_reason;
	private ChatCompletionMessage message;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getFinish_reason() {
		return finish_reason;
	}

	public void setFinish_reason(String finish_reason) {
		this.finish_reason = finish_reason;
	}

	public ChatCompletionMessage getMessage() {
		return message;
	}

	public void setMessage(ChatCompletionMessage message) {
		this.message = message;
	}
}
