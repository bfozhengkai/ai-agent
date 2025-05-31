package com.kaisiaiagent.demo.demo.invoke.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class ConfigUtils {

    public static String loadApiKeyFromConfig() {
        try {
            File file = new File("src/main/resources/application-local.yml");
            if (!file.exists()) {
                throw new RuntimeException("配置文件不存在: " + file.getAbsolutePath());
            }

            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(file)) {
                Map<String, Object> yamlMap = yaml.load(inputStream);
                System.out.println("加载的配置: " + yamlMap);

                // 根据嵌套结构获取 API Key
                Map<String, Object> spring = (Map<String, Object>) yamlMap.get("spring");
                if (spring != null) {
                    Map<String, Object> ai = (Map<String, Object>) spring.get("ai");
                    if (ai != null) {
                        Map<String, Object> dashscope = (Map<String, Object>) ai.get("dashscope");
                        if (dashscope != null && dashscope.containsKey("api-key")) {
                            String apiKey = (String) dashscope.get("api-key");
                            System.out.println("成功加载 API Key");
                            return apiKey;
                        } else {
                            throw new RuntimeException("配置文件中没有找到 api-key");
                        }
                    } else {
                        throw new RuntimeException("配置文件中没有找到 ai 配置项");
                    }
                } else {
                    throw new RuntimeException("配置文件中没有找到 spring 配置项");
                }
            }
        } catch (Exception e) {
            System.err.println("加载配置文件出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
