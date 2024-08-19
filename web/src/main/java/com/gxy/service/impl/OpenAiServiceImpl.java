package com.gxy.service.impl;

import com.alibaba.fastjson.JSON;
import com.gxy.constant.LLMRole;
import com.gxy.req.ChatCompletionMessage;
import com.gxy.req.ChatCompletionReq;
import com.gxy.resp.ChatCompletionChoice;
import com.gxy.resp.ChatCompletionResp;
import com.gxy.service.OpenAiService;
import com.gxy.utils.HttpClientUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname openAiServiceImpl
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.baseUrl}")
    private String baseUrl;

    @Override
    public String openAiReq(String prompt) {
        long startTime = System.currentTimeMillis();
        ChatCompletionReq request = new ChatCompletionReq();
        request.setModel("Qwen2-72B-Instruct");
        request.setStream(false);
        request.setMax_tokens(8192);
        List<ChatCompletionMessage> messages = new ArrayList<>();
        ChatCompletionMessage user = new ChatCompletionMessage();
        user.setRole(LLMRole.USER.getRole());
        user.setContent(prompt);
        messages.add(user);
        request.setMessages(messages);
        String content = "";
        try {
            log.info("AI请求参数={}", JSON.toJSON(request));
            String resultJson = HttpClientUtils.postJson(baseUrl,  JSON.toJSONString(request), 60000, 10000);
            log.info("AI返回结果={}", resultJson);
            ChatCompletionResp response = JSON.parseObject(resultJson, ChatCompletionResp.class);
            List<ChatCompletionChoice> choices = response.getChoices();
            String modelResult = "";    // 模型结果
            if (null != choices) {
                ChatCompletionChoice choice = choices.get(0);
                ChatCompletionMessage message = choice.getMessage();
                if (StringUtils.isNotBlank(message.getContent())) {
                    modelResult = message.getContent();
                }
            }
            if (StringUtils.isNotBlank(modelResult)) {
                content = modelResult;
            }
        } catch (Exception e) {
            log.error(">>>> 【AI推理错误】对话信息={}, 错误信息=", prompt, e);
        }
        long endTime = System.currentTimeMillis();
        log.info("单次标签请求耗时: {}ms，返回结果：{}", endTime - startTime, content);
        return content;
    }
}
