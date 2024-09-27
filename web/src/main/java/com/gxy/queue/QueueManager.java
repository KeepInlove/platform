package com.gxy.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.gxy.constant.CallMessageConstant;
import com.gxy.service.RedisService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 队列管理器
 * @Classname QueueManager
 * @Date 2024/9/19
 * @Created by guoxinyu
 */
@Component
@Slf4j
public class QueueManager {

    @Resource
    private RedisService redisService;  // 使// RedisServiceImpl 代替 RedisTemplate

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();


    @PostConstruct
    public void initGpuConfig() {
        // 清空之前的配置
        List<QueueInfoDTO> existingAGpus = getGpuConfigFromRedis(CallMessageConstant.A_CARD_KEY);
        for (QueueInfoDTO gpu : existingAGpus) {
            redisService.deleteHashField(CallMessageConstant.A_CARD_KEY, gpu.getIdentifier());
        }
        List<QueueInfoDTO>  existingNGpus = getGpuConfigFromRedis(CallMessageConstant.N_CARD_KEY);
        for (QueueInfoDTO gpu : existingNGpus) {
            redisService.deleteHashField(CallMessageConstant.N_CARD_KEY, gpu.getIdentifier());
        }
        QueueInfoDTO gpu3 = new QueueInfoDTO("gpu4090-1", "tag_N", 1);
        QueueInfoDTO gpu1 = new QueueInfoDTO("gpu4090-2", "tag_N", 1);
        QueueInfoDTO gpu2 = new QueueInfoDTO("gpu4090-3", "tag_N", 1);

        QueueInfoDTO gpu4 = new QueueInfoDTO("v100-1", "tag_A", 3);
        QueueInfoDTO gpu5 = new QueueInfoDTO("v100-2", "tag_A", 3);
        QueueInfoDTO gpu6 = new QueueInfoDTO("v100-3", "tag_A", 3);

        redisService.setHashField(CallMessageConstant.N_CARD_KEY, gpu1.getIdentifier(), GSON.toJson(gpu1));
        redisService.setHashField(CallMessageConstant.N_CARD_KEY, gpu2.getIdentifier(), GSON.toJson(gpu2));
        redisService.setHashField(CallMessageConstant.N_CARD_KEY, gpu3.getIdentifier(), GSON.toJson(gpu3));
        // 使用 Gson 将数据序列化为 JSON 并存储到 Redis 的哈希表中
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu1.getIdentifier(), GSON.toJson(gpu1));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu2.getIdentifier(), GSON.toJson(gpu2));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu3.getIdentifier(), GSON.toJson(gpu3));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu4.getIdentifier(), GSON.toJson(gpu4));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu5.getIdentifier(), GSON.toJson(gpu5));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu6.getIdentifier(), GSON.toJson(gpu6));
    }

    // 从 Redis 获取 N 卡队列
    public List<QueueInfoDTO> getNQueues() {
        return getGpuConfigFromRedis(CallMessageConstant.N_CARD_KEY);
    }

    // 从 Redis 获取 N + A 卡队列
    public List<QueueInfoDTO> getAQueues() {
        return getGpuConfigFromRedis(CallMessageConstant.A_CARD_KEY);
    }


    // 从 Redis 获取 GPU 配置
    private List<QueueInfoDTO> getGpuConfigFromRedis(String key) {
        Map<String, String> gpuMap = redisService.getHashEntries(key);
        if (gpuMap == null || gpuMap.isEmpty()) {
            log.warn("从 Redis 中未获取到队列配置: {}", key);
            return new ArrayList<>();
        }

        List<QueueInfoDTO> gpuList = new ArrayList<>();
        for (Map.Entry<String, String> entry : gpuMap.entrySet()) {
            String gpuJson = entry.getValue();
            QueueInfoDTO TagServiceDTO = GSON.fromJson(gpuJson, QueueInfoDTO.class);
            gpuList.add(TagServiceDTO);
        }
        return gpuList;
    }

}
