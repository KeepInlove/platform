package com.gxy.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.gxy.constant.LLMRole;
import com.gxy.entity.ModelRequestsRespEntity;
import com.gxy.mapper.mysql.ModelRequestsRespMapper;
import com.gxy.req.ChatCompletionMessage;
import com.gxy.req.ChatCompletionReq;
import com.gxy.resp.ChatCompletionChoice;
import com.gxy.resp.ChatCompletionResp;
import com.gxy.service.ModelRequestsRespService;
import com.gxy.service.OpenAiService;
import com.gxy.utils.HttpClientUtils;
import com.mybatisflex.core.query.QueryWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gxy.entity.table.ModelRequestsRespEntityTableDef.MODEL_REQUESTS_RESP_ENTITY;

/**
 * @Classname openAiServiceImpl
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
@Slf4j
@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.apiKey}")
    private String apiKey;
    @Value("${openai.api.baseUrl}")
    private String baseUrl;
    @Value("${openai.api.model}")
    public String openAiModel;


    @Resource
    private ModelRequestsRespMapper modelRequestsRespMapper;

    @Override
    public String openAiReq(String prompt) {
        long startTime = System.currentTimeMillis();
        ChatCompletionReq request = new ChatCompletionReq();
        request.setModel("Qwen2-72B-Instruct");
        request.setStream(false);
        request.setMax_tokens(3734);
        List<ChatCompletionMessage> messages = new ArrayList<>();
        ChatCompletionMessage user = new ChatCompletionMessage();
        user.setRole(LLMRole.USER.getRole());
        user.setContent(prompt);
        messages.add(user);
        request.setMessages(messages);
        String content = "";
        try {
            log.info("AI请求参数={}", JSON.toJSON(request));
            String resultJson = HttpClientUtils.postJson(baseUrl, JSON.toJSONString(request), 60000, 10000);
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


    public String openQwAiReq(String sessionId,String prompt, String roleDesc) {
        List<ModelRequestsRespEntity> list=new ArrayList<>();
        List<Message> historyList = new ArrayList<>();
        // 创建数据库实体对象
        ModelRequestsRespEntity entity = new ModelRequestsRespEntity();
        long startTime = System.currentTimeMillis();
        entity.setQuestion(prompt);
        entity.setRequestTime(new Timestamp(startTime));

        if (!StrUtil.isBlank(sessionId)) {
            list = modelRequestsRespMapper.selectListByQuery(QueryWrapper.create().where(MODEL_REQUESTS_RESP_ENTITY.SESSION_ID.eq(sessionId)));
            if (!CollectionUtils.isEmpty(list) ) {
                ModelRequestsRespEntity modelRequestsResp = list.getFirst();
                if (modelRequestsResp != null&&StrUtil.isBlank(roleDesc)&&StrUtil.isBlank(modelRequestsResp.getRoleDesc())) {
                    roleDesc = modelRequestsResp.getRoleDesc();
                }
                for (ModelRequestsRespEntity respEntity : list) {
                    Message userMsg = Message.builder()
                            .role(Role.USER.getValue())
                            .content(respEntity.getQuestion())
                            .build();
                    historyList.add(userMsg);
                    Message systemMsg = Message.builder()
                            .role(Role.SYSTEM.getValue())
                            .content(respEntity.getResponse())
                            .build();
                    historyList.add(systemMsg);
                }
                entity.setSessionId(modelRequestsResp.getSessionId());
            }
        }

        String content = "";
        roleDesc =  StrUtil.isBlank(roleDesc)? "You are a helpful assistant" : roleDesc;
        entity.setRoleDesc(roleDesc);
        sessionId= StrUtil.isBlank(sessionId)? UUID.fastUUID().toString(false) : sessionId;
        entity.setSessionId(sessionId);
        modelRequestsRespMapper.insertSelective(entity);

        // 构建生成器对象
        Generation gen = new Generation();
        // 构建系统消息 (可选)
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(roleDesc)
                .build();
        historyList.add(systemMsg);
        // 构建用户消息
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(prompt)
                .build();
        historyList.add(userMsg);
        // 构建请求参数
        GenerationParam param = GenerationParam.builder()
                .model(openAiModel)
                .messages(historyList)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .apiKey(apiKey)
                .maxTokens(2000)
                .build();
        log.info("AI请求参数={}", JSONUtil.toJsonStr(param));
        // 调用API
        GenerationResult result = null;
        try {
            result = gen.call(param);
            log.info("AI返回结果={}", JSONUtil.toJsonStr(result));
            // 提取模型结果并存储到实体对象中
            entity.setRequestId(result.getRequestId());
            entity.setInputTokens(result.getUsage().getInputTokens());
            entity.setOutputTokens(result.getUsage().getOutputTokens());
            entity.setTotalTokens(result.getUsage().getTotalTokens());
            long endTime = System.currentTimeMillis();
            entity.setResponseTime(new Timestamp(endTime));
            if (result.getOutput() != null && !result.getOutput().getChoices().isEmpty()) {
                content = result.getOutput().getChoices().get(0).getMessage().getContent();
                entity.setResponse(content);
            }
        } catch (Exception e) {
            entity.setResponse(e.getMessage());
            modelRequestsRespMapper.update(entity);
            log.error(">>>> 【AI推理错误】对话信息={}, 错误信息=", prompt, e);
        }
        long endTime = System.currentTimeMillis();
        log.info("单次对话请求耗时: {}ms，返回结果：{}", endTime - startTime, content);
        // 存储数据到数据库
        modelRequestsRespMapper.update(entity);
        return content;
    }


}
