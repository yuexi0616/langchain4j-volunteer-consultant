package com.ssm.consultant.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 联网搜索工具：通过调用 Tavily Search API 实现联网检索，
 * 让顾问「熊大」能够获取今年最新高考政策、录取分数线等实时信息。
 * 1.17.2-beta27 版本无现成 Tavily 适配器，故用 JDK 内置 HttpClient 自行封装。
 */
@Component
public class WebSearchTool {

    private static final String TAVILY_SEARCH_URL = "https://api.tavily.com/search";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tavily.api-key:}")
    private String tavilyApiKey;

    @Value("${tavily.max-results:5}")
    private int maxResults;

    @Tool("联网搜索最新信息，用于回答需要实时数据的问题，例如今年高考政策、最新录取分数线、最新招生动态等")
    public String webSearch(@P("搜索关键词") String query) {
        // 未配置 API Key 时给出明确提示，避免调用直接失败
        if (tavilyApiKey == null || tavilyApiKey.isBlank()) {
            return "联网搜索功能未启用：尚未配置 Tavily API Key，请在环境变量中设置 TAVILY_API_KEY。";
        }
        try {
            // 构造 Tavily 搜索请求体
            ObjectNode body = objectMapper.createObjectNode();
            body.put("query", query);
            body.put("max_results", maxResults);
            body.put("search_depth", "basic");
            body.put("include_answer", true);
            body.put("language", "zh");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TAVILY_SEARCH_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + tavilyApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = objectMapper.readTree(response.body());

            // 非 200 响应：解析错误信息
            if (response.statusCode() != 200) {
                String error = root.path("detail").path("error").asText("HTTP " + response.statusCode());
                return "联网搜索失败：" + error;
            }

            StringBuilder sb = new StringBuilder();
            // Tavily 返回的 AI 摘要（include_answer=true 时存在）
            JsonNode answer = root.path("answer");
            if (!answer.isMissingNode() && !answer.asText().isBlank()) {
                sb.append("【AI 摘要】").append(answer.asText()).append("\n\n");
            }
            // 搜索结果列表
            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {
                int i = 1;
                for (JsonNode result : results) {
                    sb.append(i++).append(". ").append(result.path("title").asText())
                            .append("\n   链接：").append(result.path("url").asText())
                            .append("\n   内容：").append(result.path("content").asText())
                            .append("\n\n");
                }
            }
            return sb.length() > 0 ? sb.toString().trim() : "未搜索到相关结果。";
        } catch (Exception e) {
            return "联网搜索异常：" + e.getMessage();
        }
    }
}
