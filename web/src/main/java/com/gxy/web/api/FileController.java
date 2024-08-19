package com.gxy.web.api;

/**
 * @Classname FileController
 * @Date 2024/8/16
 * @Created by guoxinyu
 */
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.gxy.service.ElasticsearchService;
import com.gxy.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = fileStorageService.storeFile(file);
            elasticsearchService.indexFileContent(fileName, file.getBytes());
            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(@RequestParam("query") String query) {
        // 使用 elasticsearch-java 客户端执行搜索
        SearchResponse<Map> searchResponse = elasticsearchService.searchFileContent(query);
        // 从搜索结果中提取文件名
        List<String> fileNames = searchResponse.hits().hits().stream()
                .map(Hit::id)  // 使用 elasticsearch-java 的 Hit 类获取文档 ID
                .collect(Collectors.toList());

        // 根据文件名生成文件路径
        List<String> filePaths = fileNames.stream()
                .map(fileStorageService::getFilePath)
                .collect(Collectors.toList());
        return ResponseEntity.ok(filePaths);
    }

}
