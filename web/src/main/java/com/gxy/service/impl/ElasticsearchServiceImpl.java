package com.gxy.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.json.JsonData;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gxy.service.ElasticsearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname ElasticsearchServiceImpl
 * @Date 2024/8/16
 * @Created by guoxinyu
 */

@Service
@Slf4j
public class ElasticsearchServiceImpl implements ElasticsearchService {


    @Autowired
    private ElasticsearchClient client;

    @Override
    public void indexFileContent(String fileName, byte[] fileContent) {
        try {
            // 将文件内容编码为 Base64 字符串
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            // 创建文档内容，直接将 base64Content 放入 file 字段中
            Map<String, Object> document = new HashMap<>();
            document.put("file", base64Content); // 注意这里不使用 _content，而是直接放入 file 字段

            // 使用 FastJSON 将 Map 转换为 JSON 字符串
            String jsonString = JSON.toJSONString(document, SerializerFeature.WriteMapNullValue);

            // 创建索引请求并指定使用的 pipeline
            IndexRequest<Void> request = IndexRequest.of(i -> i
                    .index("documents")
                    .id(fileName)
                    .withJson(new StringReader(jsonString))  // 使用 JSON 字符串
                    .pipeline("attachment")  // 使用 Ingest Pipeline
            );

            // 索引文档
            IndexResponse response = client.index(request);
            log.info("Indexed document ID: " + response.id());
        } catch (IOException e) {
            log.error("Failed to index file content for file: " + fileName, e);
        }
    }


    @Override
    public SearchResponse<Map> searchFileContent(String query) {
        try {
            // 创建搜索请求
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("documents")
                    .query(q -> q
                            .matchPhrase(m -> m
                                    .field("file_details.content")  // 搜索解析后的文件内容
                                    .query(query)
                            )
                    )
            );
            // 执行搜索
            SearchResponse<Map> searchResponse = client.search(searchRequest, Map.class);
            // 处理搜索结果
            HitsMetadata<Map> hits = searchResponse.hits();
            for (Hit<Map> hit : hits.hits()) {
                log.info("Found document ID: " + hit.id());
            }
            return searchResponse;
        } catch (IOException e) {
            log.error("Failed to search file content for query: " + query, e);
            return null;  // 或者返回空的搜索结果
        }
    }

}

