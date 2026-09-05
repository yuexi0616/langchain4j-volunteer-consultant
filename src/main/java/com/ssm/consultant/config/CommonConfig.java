package com.ssm.consultant.config;

import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel model;
    @Autowired
    private ChatMemoryStore redisChatMemoryStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;

    //    @Bean
//    public ConsultantServices consultantServices(){
//        ConsultantServices consultantServices = AiServices.builder(ConsultantServices.class)
//                .chatModel(model)
//                .build();
//        return consultantServices;
//    }
    // 构建会话记忆对象
    @Bean
    public ChatMemory chatMemory(){
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return memory;
    }

    // 构建chatMemoryProvider对象
    @Bean
    public ChatMemoryProvider  chatMemoryProvider(){
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {

            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    // 构建向量数据库操作对象
    // @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 1.加载文档进内存
        // 类路径加载
        // List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
        // 直接传入解析器
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content", new ApachePdfBoxDocumentParser());
        // 本地磁盘绝对路径加载
        // List<Document> documents = FileSystemDocumentLoader.loadDocuments("D:\\IDEA\\LangChain4j-study\\consultant\\src\\main\\resources\\content");
        // 2。构建向量数据库操作对象 操作的是内存版本的向量数据库
        // InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        // 构建文档分割器对象
        DocumentSplitter ds = DocumentSplitters.recursive(500, 100);
        // 3.构建一个EmeddingStoreIngestor对象，完成文本数据切割、向量化、存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                //.embeddingStore(store)
                // 使用redis向量数据库操作对象
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(ds)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(documents);

        return redisEmbeddingStore;
    }

    // 构建向量数据库检索对象
    @Bean
    public ContentRetriever  contentRetriever(/*EmbeddingStore<TextSegment> store*/){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .embeddingModel(embeddingModel) // 确保入库和查询使用同一 EmbeddingModel，避免向量维度不匹配
                .minScore(0.5)
                .maxResults(3)
                .build();
    }
}
