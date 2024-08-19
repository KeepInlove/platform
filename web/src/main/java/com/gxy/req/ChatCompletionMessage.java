package com.gxy.req;

/**
 * @author pengyonglei
 * @version 1.0.0
 */
public class ChatCompletionMessage {

	private String role;		// 角色，取值："user""assistant""system""function""tool"
	private String content;		// 内容

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
