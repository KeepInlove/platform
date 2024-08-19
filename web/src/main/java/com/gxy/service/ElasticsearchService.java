package com.gxy.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;

import java.util.Map;

/**
 * @Classname ElasticsearchService
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
public interface ElasticsearchService {

    public void indexFileContent(String fileName, byte[] fileContent);

    public SearchResponse<Map> searchFileContent(String query);
}
