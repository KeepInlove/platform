package com.gxy.service;

/**
 * @Classname openAiService
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
public interface OpenAiService {

     String openAiReq(String prompt);

     String openQwAiReq(String sessionId,String prompt, String roleDesc);
}
