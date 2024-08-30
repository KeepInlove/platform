package com.gxy.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.codegeneration.CodeGenerationOutput;
import com.alibaba.dashscope.aigc.completion.ChatCompletion;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.tools.*;
import com.alibaba.dashscope.utils.JsonUtils;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.*;
import com.gxy.constant.LLMRole;
import com.gxy.entity.ModelRequestsRespEntity;
import com.gxy.mapper.mysql.ModelRequestsRespMapper;
import com.gxy.modelTool.GetTimeTool;
import com.gxy.modelTool.GetWhetherTool;
import com.gxy.req.ChatCompletionMessage;
import com.gxy.req.ChatCompletionReq;
import com.gxy.resp.ChatCompletionChoice;
import com.gxy.resp.ChatCompletionResp;
import com.gxy.service.OpenAiService;
import com.gxy.utils.HttpClientUtils;
import com.mybatisflex.core.query.QueryWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.dashscope.aigc.generation.GenerationOutput.Choice;
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
        List<Message> messageList = new ArrayList<>();
        // 创建数据库实体对象
        ModelRequestsRespEntity entity = new ModelRequestsRespEntity();
        long startTime = System.currentTimeMillis();
        entity.setQuestion(prompt);
        entity.setRequestTime(new Timestamp(startTime));

        if (!StrUtil.isBlank(sessionId)) {
            list = modelRequestsRespMapper.selectListByQuery(QueryWrapper.create().where(MODEL_REQUESTS_RESP_ENTITY.SESSION_ID.eq(sessionId)));
            if (!CollectionUtils.isEmpty(list) ) {
                ModelRequestsRespEntity modelRequestsResp = list.getFirst();
                if (modelRequestsResp != null && StrUtil.isBlank(roleDesc) && !StrUtil.isBlank(modelRequestsResp.getRoleDesc())) {
                    roleDesc = modelRequestsResp.getRoleDesc();
                }
                for (ModelRequestsRespEntity respEntity : list) {
                    Message userMsg = Message.builder()
                            .role(Role.USER.getValue())
                            .content(respEntity.getQuestion())
                            .build();
                    messageList.add(userMsg);
                    Message systemMsg = Message.builder()
                            .role(Role.SYSTEM.getValue())
                            .content(respEntity.getResponse())
                            .build();
                    messageList.add(systemMsg);
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
        if (messageList.isEmpty()||!messageList.getLast().getRole().equals(Role.SYSTEM.getValue())) {
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(roleDesc)
                    .build();
            messageList.add(systemMsg);
        }
        // 构建用户消息
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(prompt)
                .build();
        messageList.add(userMsg);
        // 构建请求参数
        GenerationParam param = GenerationParam.builder()
                .model(openAiModel)
                .messages(messageList)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .apiKey(apiKey)
                .maxTokens(2000)
                .tools(getToolFunctionList())
                .build();
        log.info("AI请求参数={}", JSONUtil.toJsonStr(param));
        // 调用API
        GenerationResult result = null;
        try {
            result = gen.call(param);
            log.info("AI返回结果={}", JSONUtil.toJsonStr(result));
            if (null!=result.getOutput()&&null!=result.getOutput().getChoices()&&!result.getOutput().getChoices().isEmpty()) {
                for (Choice choice : result.getOutput().getChoices()) {
                    if (result.getOutput().getChoices().get(0).getMessage().getToolCalls() != null) {
                        messageList.add(choice.getMessage());
                        for (ToolCallBase toolCall : result.getOutput().getChoices().get(0).getMessage().getToolCalls()) {
                            if (toolCall.getType().equals("function")) {
                                // 获取工具函数名称和入参
                                String functionName = ((ToolCallFunction) toolCall).getFunction().getName();
                                String functionArgument = ((ToolCallFunction) toolCall).getFunction().getArguments();
                                // 大模型判断调用天气查询工具的情况
                                if (functionName.equals("get_current_whether")) {
                                    GetWhetherTool GetWhetherFunction = JsonUtils.fromJson(functionArgument, GetWhetherTool.class);
                                    String whether = GetWhetherFunction.call();
                                    Message toolResultMessage = Message.builder()
                                            .role(Role.TOOL.getValue())
                                            .content(String.valueOf(whether))
                                            .toolCallId(toolCall.getId())
                                            .build();
                                    messageList.add(toolResultMessage);
                                    log.info("工具输出信息：{}" , whether);
                                }
                                // 大模型判断调用时间查询工具的情况
                                else if (functionName.equals("get_current_time")) {
                                    GetTimeTool GetTimeFunction =
                                            JsonUtils.fromJson(functionArgument, GetTimeTool.class);
                                    String time = GetTimeFunction.call();
                                    Message toolResultMessage = Message.builder()
                                            .role(Role.TOOL.getValue())
                                            .content(String.valueOf(time))
                                            .toolCallId(toolCall.getId())
                                            .build();
                                    messageList.add(toolResultMessage);
                                    log.info("工具输出信息：{}" , time);
                                }
                            }
                        }
                    }else {
                        // 提取模型结果并存储到实体对象中
                        entity.setRequestId(result.getRequestId());
                        entity.setInputTokens(result.getUsage().getInputTokens());
                        entity.setOutputTokens(result.getUsage().getOutputTokens());
                        entity.setTotalTokens(result.getUsage().getTotalTokens());
                        long endTime = System.currentTimeMillis();
                        entity.setResponseTime(new Timestamp(endTime));
                        if (result.getOutput() != null && !result.getOutput().getChoices().isEmpty()) {
                            content = result.getOutput().getChoices().getFirst().getMessage().getContent();
                            entity.setResponse(content);
                        }
                        log.info("单次对话请求耗时: {}ms，返回结果：{}", endTime - startTime, content);
                        // 存储数据到数据库
                        modelRequestsRespMapper.update(entity);
                        return content;
                    }
                }
            }
            Integer inputTokens= result.getUsage().getInputTokens();
            Integer outputTokens = result.getUsage().getOutputTokens();
            Integer totalTokens = result.getUsage().getTotalTokens();

            //大模型的第二轮调用 包含工具输出信息
            result = gen.call(param);
            inputTokens += result.getUsage().getInputTokens();
            outputTokens += result.getUsage().getOutputTokens();
            totalTokens += result.getUsage().getTotalTokens();
            log.info("再次AI返回结果={}", JSONUtil.toJsonStr(result));

            // 提取模型结果并存储到实体对象中
            entity.setRequestId(result.getRequestId());
            entity.setInputTokens(inputTokens);
            entity.setOutputTokens(outputTokens);
            entity.setTotalTokens(totalTokens);
            long endTime = System.currentTimeMillis();
            entity.setResponseTime(new Timestamp(endTime));
            if (result.getOutput() != null && !result.getOutput().getChoices().isEmpty()) {
                content = result.getOutput().getChoices().getFirst().getMessage().getContent();
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

    private List<ToolBase> getToolFunctionList(){
        List<ToolBase> toolList = new ArrayList<>();
        // 创建SchemaGeneratorConfigBuilder实例，指定使用JSON格式的模式版本
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);

        // 构建SchemaGeneratorConfig配置，包含额外的OpenAPI格式值，但不使用枚举的toString方法进行展平
        SchemaGeneratorConfig config = configBuilder.with(Option.EXTRA_OPEN_API_FORMAT_VALUES)
                .without(Option.FLATTENED_ENUMS_FROM_TOSTRING)
                .build();

        // 根据配置创建SchemaGenerator实例，用于生成模式
        SchemaGenerator generator = new SchemaGenerator(config);

        // 生成GetWhetherTool类的JSON Schema
        ObjectNode jsonSchema_whether = generator.generateSchema(GetWhetherTool.class);
        // 生成GetTimeTool类的JSON Schema
        ObjectNode jsonSchema_time = generator.generateSchema(GetTimeTool.class);


        // 构建获取指定地区天气的函数定义
        FunctionDefinition fd_whether = FunctionDefinition.builder()
                .name("get_current_whether") // 设置函数名称
                .description("被询问指定地区的天气") // 设置函数描述
                .parameters(JsonUtils.parseString(jsonSchema_whether.toString()).getAsJsonObject()) // 设置函数参数
                .build();

//        // 构建获取当前时刻时间的函数定义
        FunctionDefinition fd_time = FunctionDefinition.builder()
                .name("get_current_time") // 设置函数名称
                .description("被询问当前时间时使用") // 设置函数描述
                .parameters(JsonUtils.parseString(jsonSchema_time.toString()).getAsJsonObject()) // 设置函数参数
                .build();

        toolList.add(ToolFunction.builder().function(fd_whether).build());
        toolList.add(ToolFunction.builder().function(fd_time).build());
        return toolList;
    }


}
