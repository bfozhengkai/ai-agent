package com.kaisiaiagent.demo.documentreader;
// Spring AI BOM: org.springframework.ai:spring-ai-bom:${springAiVersion}
// Spring AI OpenAI/Ollama starter: org.springframework.ai:spring-ai-openai-spring-boot-starter
// Jackson: com.fasterxml.jackson.core:jackson-databind
// youtubedl-java: com.github.sapher:youtubedl-java:1.1 (通过 jitpack.io)

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class YouTubeDocumentReader implements DocumentReader {

    private final String youtubeUrl= "https://www.youtube.com/watch?v=XWfSuGKmqXY";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public YouTubeDocumentReader() {
        // 确保 youtube-dl (或 yt-dlp) 已安装并位于 PATH 中
        // 可以在此处添加检查或处理 YoutubeDLException
    }

    @Override
    public List<Document> get() {
        List<Document> documents = new ArrayList<>();
        Path tempDir = null;
        try {
            // 1. 创建临时目录用于下载文件
            tempDir = Files.createTempDirectory("youtube-transcript-");
            String outputTemplate = tempDir.resolve("%(id)s.%(ext)s").toString();
            String videoId = extractVideoId(youtubeUrl);

            // 2. 构建 YoutubeDLRequest 以提取字幕和 info JSON
            YoutubeDLRequest request = new YoutubeDLRequest(youtubeUrl, tempDir.toString());
            request.setOption("skip-download"); // 不下载视频
            request.setOption("all-subs");      // 下载所有可用字幕
            request.setOption("convert-subs", "srt"); // 转换为 SRT 格式
            request.setOption("write-info-json"); // 将视频元数据写入 JSON
            request.setOption("output", outputTemplate); // 定义输出文件名模板

            log.info("正在执行 yt-dlp，目标: " + youtubeUrl);
            YoutubeDL.setExecutablePath("C:\\Users\\Kaisi\\yt-dlp\\yt-dlp.exe");
            YoutubeDLResponse response = YoutubeDL.execute(request);
            String stdOut = response.getOut();
            String stdErr = response.getErr();

            System.out.println("\n--- yt-dlp Standard Output (from getOut()) ---");
            System.out.println(stdOut);
            System.out.println("----------------------------------------------\n");

            if (!stdErr.isEmpty()) {
                System.err.println("\n--- yt-dlp Error Output (from getErr()) ---");
                System.err.println(stdErr);
                System.err.println("-------------------------------------------\n");
            }
            File ytDlpLogFile = tempDir.resolve("yt-dlp-output.log").toFile();
            // 检查 ytDlpLogFile 是否存在并读取其内容（如果 response.getOut() 不完整）
            if (ytDlpLogFile.exists()) {
                System.out.println("\n--- yt-dlp Full Log from File (" + ytDlpLogFile.getName() + ") ---");
                Files.readAllLines(ytDlpLogFile.toPath()).forEach(System.out::println);
                System.out.println("--------------------------------------------------\n");
            }

            if (response.getExitCode()!= 0) {
                log.error("yt-dlp 命令失败: " + response.getErr());
                throw new IOException("未能提取 YouTube 数据。");
            }

            // 3. 定位下载的文件
            File srtFile = tempDir.resolve(videoId + ".srt").toFile();
            File infoJsonFile = tempDir.resolve(videoId + ".info.json").toFile();

            if (!srtFile.exists() ||!infoJsonFile.exists()) {
                log.error("所需文件未找到: " + srtFile.getName() + ", " + infoJsonFile.getName());
                throw new IOException("提取后未找到 SRT 或 Info JSON 文件。");
            }

            // 4. 从 info.json 解析视频元数据
            JsonNode infoJson = objectMapper.readTree(infoJsonFile);
            Map<String, Object> baseMetadata = new HashMap<>();
            baseMetadata.put("video_id", videoId);
            baseMetadata.put("title", infoJson.has("title")? infoJson.get("title").asText() : "N/A");
            baseMetadata.put("author", infoJson.has("uploader")? infoJson.get("uploader").asText() : "N/A");
            baseMetadata.put("upload_date", infoJson.has("upload_date")? infoJson.get("upload_date").asText() : "N/A"); //格式通常为YYYYMMDD
            baseMetadata.put("duration_seconds", infoJson.has("duration")? infoJson.get("duration").asLong() : 0L);
            baseMetadata.put("view_count", infoJson.has("view_count")? infoJson.get("view_count").asLong() : 0L);
            baseMetadata.put("categories", infoJson.has("categories")? infoJson.get("categories").toString() : ""); // 将数组转换为 String
            baseMetadata.put("tags", infoJson.has("tags")? infoJson.get("tags").toString() : ""); // 将数组转换为 String
            baseMetadata.put("description", infoJson.has("description")? infoJson.get("description").asText() : "N/A");
            baseMetadata.put("thumbnail_url", infoJson.has("thumbnail")? infoJson.get("thumbnail").asText() : "N/A");
            baseMetadata.put("source_url", youtubeUrl);
            baseMetadata.put("document_type", "youtube_transcript");

            // 5. 解析 SRT 文件 (使用内部解析逻辑)
            List<SubtitleEntry> subtitles = parseSrtFile(srtFile);

            // 6. 从字幕条目创建 Spring AI Document 对象
            for (int i = 0; i < subtitles.size(); i++) {
                SubtitleEntry sub = subtitles.get(i);
                Map<String, Object> docMetadata = new HashMap<>(baseMetadata);
                docMetadata.put("start_timestamp_ms", sub.getStartTimeMs());
                docMetadata.put("end_timestamp_ms", sub.getEndTimeMs());
                docMetadata.put("segment_index", i);
                docMetadata.put("original_srt_index", sub.getId()); // SRT 文件中的原始索引

                // 确保元数据值是简单类型
                // SubtitleEntry 的 getStartTimeMs/getEndTimeMs 返回 long (毫秒)，这符合要求。
                // 文本内容已经是 String。

                documents.add(Document.builder()
                        .id(videoId + "_chunk_" + i) // 每个片段的唯一 ID
                        .text(sub.getText())
                        .metadata(docMetadata)
                        .build());
            }

        } catch (Exception e) {
            log.error("读取 YouTube 视频时出错: " + e.getMessage());
            e.printStackTrace();
            // 可选择返回空列表或重新抛出自定义运行时异常
        } finally {
            // 清理临时文件
            if (tempDir!= null) {
                try {
                    Files.walk(tempDir)
                            .sorted(java.util.Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    log.info("已清理临时目录: " + tempDir);
                } catch (IOException e) {
                    log.error("未能删除临时目录: " + e.getMessage());
                }
            }
        }
        return documents;
    }

    private String extractVideoId(String youtubeUrl) {
        // 提取各种 YouTube URL 格式中的视频 ID 的基本正则表达式
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embedCE%2F|%2Fv%2F|eX%2F|watch\\?v=|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youtubeUrl);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // 或者针对无效 URL 抛出异常
    }

    // 内部类用于表示 SRT 字幕条目
    private static class SubtitleEntry {
        int id;
        long startTimeMs;
        long endTimeMs;
        String text;

        public SubtitleEntry(int id, long startTimeMs, long endTimeMs, String text) {
            this.id = id;
            this.startTimeMs = startTimeMs;
            this.endTimeMs = endTimeMs;
            this.text = text;
        }

        public int getId() { return id; }
        public long getStartTimeMs() { return startTimeMs; }
        public long getEndTimeMs() { return endTimeMs; }
        public String getText() { return text; }
    }

    // 内部方法用于解析 SRT 文件
    private List<SubtitleEntry> parseSrtFile(File srtFile) throws IOException {
        List<SubtitleEntry> entries = new ArrayList<>();
        List<String> lines = Files.readAllLines(srtFile.toPath());

        Pattern timestampPattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2}),(\\d{3}) --> (\\d{2}):(\\d{2}):(\\d{2}),(\\d{3})");

        int i = 0;
        while (i < lines.size()) {
            // 跳过空行或非数字行，直到找到索引
            if (lines.get(i).trim().isEmpty() ||!lines.get(i).trim().matches("\\d+")) {
                i++;
                continue;
            }

            int id = Integer.parseInt(lines.get(i).trim());
            i++;

            if (i >= lines.size()) break;

            String timestampLine = lines.get(i).trim();
            Matcher matcher = timestampPattern.matcher(timestampLine);
            if (!matcher.matches()) {
                log.error("格式错误的 SRT 时间戳行: " + timestampLine);
                i++;
                continue;
            }

            long startTimeMs = parseTimeToMs(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
            long endTimeMs = parseTimeToMs(matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(8));
            i++;

            StringBuilder textBuilder = new StringBuilder();
            while (i < lines.size() &&!lines.get(i).trim().isEmpty()) {
                textBuilder.append(lines.get(i)).append(" ");
                i++;
            }
            String text = textBuilder.toString().trim();

            entries.add(new SubtitleEntry(id, startTimeMs, endTimeMs, text));
            i++; // 跳过文本块后的空行
        }
        return entries;
    }

    // 内部方法用于将时间字符串转换为毫秒
    private long parseTimeToMs(String hours, String minutes, String seconds, String milliseconds) {
        return Long.parseLong(hours) * 3600 * 1000 +
                Long.parseLong(minutes) * 60 * 1000 +
                Long.parseLong(seconds) * 1000 +
                Long.parseLong(milliseconds);
    }
}