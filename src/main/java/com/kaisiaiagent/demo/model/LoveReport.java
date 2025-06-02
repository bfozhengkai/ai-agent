package com.kaisiaiagent.demo.model;

import java.util.List;

/**
 * 恋爱报告实体类
 * 包含报告标题和建议列表
 */
public record LoveReport(String title, List<String> suggestions) {}
