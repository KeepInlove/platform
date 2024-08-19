package com.gxy.constant;

/**
 * 大模型角色
 *
 * @author pengyonglei
 * @version 1.0.0
 */
public enum LLMRole {

	SYSTEM("system", "系统"),
	USER("user", "用户"),
	ASSISTANT("assistant", "助手")
	;

	private String role;
	private String desc;

	LLMRole(String role, String desc) {
		this.role = role;
		this.desc = desc;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
