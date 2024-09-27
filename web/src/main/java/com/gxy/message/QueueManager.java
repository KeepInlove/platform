package com.gxy.message;
import com.google.gson.Gson;
import com.gxy.constant.CallMessageConstant;
import com.gxy.queue.QueueInfoDTO;
import com.gxy.service.RedisService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname QueueManager
 * @Date 2024/9/18
 * @Created by guoxinyu
 */
@Component
@Slf4j
public class QueueManager {

    @Resource
    private RedisService redisService;  // 使用 RedisServiceImpl 代替 RedisTemplate
    private static final Gson gson = new Gson();  // 使用 Gson 代替 ObjectMapper


    @PostConstruct
    public void initGpuConfig() {
        QueueInfoDTO gpu1 = new QueueInfoDTO("gpu4090-1", "full_case_analysis_call_tag_N", 1);
        QueueInfoDTO gpu2 = new QueueInfoDTO("gpu4090-2", "full_case_analysis_call_tag_N", 1);
        QueueInfoDTO gpu3 = new QueueInfoDTO("v100-1", "full_case_analysis_call_tag_A", 3);
        QueueInfoDTO gpu4 = new QueueInfoDTO("v100-2", "full_case_analysis_call_tag_A", 3);
        QueueInfoDTO gpu5 = new QueueInfoDTO("v100-3", "full_case_analysis_call_tag_A", 3);

        // 使用 Gson 将数据序列化为 JSON 并存储到 Redis 的哈希表中
        redisService.setHashField(CallMessageConstant.N_CARD_KEY, gpu1.getIdentifier(), gson.toJson(gpu1));
        redisService.setHashField(CallMessageConstant.N_CARD_KEY, gpu2.getIdentifier(), gson.toJson(gpu2));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu3.getIdentifier(), gson.toJson(gpu3));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu4.getIdentifier(), gson.toJson(gpu4));
        redisService.setHashField(CallMessageConstant.A_CARD_KEY, gpu5.getIdentifier(), gson.toJson(gpu5));
    }

    // 从 Redis 获取 N 卡队列
    public List<QueueInfoDTO> getNQueues() {
        return getGpuConfigFromRedis(CallMessageConstant.N_CARD_KEY);
    }

    // 从 Redis 获取 N + A 卡队列
    public List<QueueInfoDTO> getAQueues() {
        return getGpuConfigFromRedis(CallMessageConstant.A_CARD_KEY);
    }

    // 从 Redis 获取所有队列（N 卡 + N+A 卡共享队列）
    public List<QueueInfoDTO> getAllQueues() {
        List<QueueInfoDTO> allQueues = new ArrayList<>();
        List<QueueInfoDTO> nQueues = getNQueues();
        List<QueueInfoDTO> aQueues = getAQueues();

        if (nQueues != null && !nQueues.isEmpty()) {
            allQueues.addAll(nQueues);
        }
        if (aQueues != null && !aQueues.isEmpty()) {
            allQueues.addAll(aQueues);
        }
        return allQueues;
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
            QueueInfoDTO TagServiceDTO = gson.fromJson(gpuJson, QueueInfoDTO.class);
            gpuList.add(TagServiceDTO);
        }
        return gpuList;
    }

}
