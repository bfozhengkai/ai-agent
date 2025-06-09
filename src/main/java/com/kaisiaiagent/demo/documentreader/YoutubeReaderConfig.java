package com.kaisiaiagent.demo.documentreader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class YoutubeReaderConfig {

    @Value("${youtube.video.url}")
    private String youtubeVideoUrl; // 在 application.properties/yaml 中配置此项

    @Bean
    public YouTubeDocumentReader youTubeDocumentReader() {
        return new YouTubeDocumentReader();
    }


    public DocumentTransformer tokenTextSplitter() {
        // 根据内容类型配置分块大小和重叠
        // 注意：TokenTextSplitter 的构造函数参数可能因 Spring AI 版本而异。
        // 较新版本可能支持 builder().withChunkSize().withOverlapSize().build()
        // 此处使用一个通用构造函数示例，可能需要根据实际版本调整
        return new TokenTextSplitter(1000, 0, 0, Integer.MAX_VALUE, true); // chunkSize, minChunkSizeChars, minChunkLengthToEmbed, maxNumChunks, keepSeparator
        // 如果版本支持，可以使用更精细的重叠控制：
    }
    // 假设 VectorStore 和 EmbeddingModel bean 已配置
    // 例如，SimpleVectorStore, PineconeVectorStore, PgVectorStore


    public ApplicationRunner loadYouTubeData(
            YouTubeDocumentReader reader,
            DocumentTransformer splitter,
            VectorStore vectorStore) {
        return args -> {
            log.info("开始加载 YouTube 数据到向量存储...");
            List<Document> rawDocuments = reader.read();
            List<Document> splitDocuments = splitter.transform(rawDocuments);
            vectorStore.accept(splitDocuments); // 或者 vectorStore.write(splitDocuments);
            log.info("已从 YouTube 加载 " + splitDocuments.size() + " 个文档片段。");
        };
    }
}