package com.gxy.req;

import java.util.List;

/**
 * @author pengyonglei
 * @version 1.0.0
 */
public class ChatCompletionReq {

	private String model;		// 模型名称
	private List<ChatCompletionMessage> messages;	// 消息列表
	private int max_tokens = 512;	// 最大生成Tokens
	private boolean stream;	// 是否流式输出

	private String mode;	// 模式
	private String instruction_template;	// 指令模版

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getInstruction_template() {
		return instruction_template;
	}

	public void setInstruction_template(String instruction_template) {
		this.instruction_template = instruction_template;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public List<ChatCompletionMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatCompletionMessage> messages) {
		this.messages = messages;
	}

	public int getMax_tokens() {
		return max_tokens;
	}

	public void setMax_tokens(int max_tokens) {
		this.max_tokens = max_tokens;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}
}
