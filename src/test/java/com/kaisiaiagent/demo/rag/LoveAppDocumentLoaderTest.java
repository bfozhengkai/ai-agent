package com.kaisiaiagent.demo.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LoveAppDocumentLoaderTest {

    /**
     * Tests for the `loadMarkdowns` method in the `LoveAppDocumentLoader` class.
     * The method retrieves and processes Markdown documents from a specified directory
     * using the configured `ResourcePatternResolver`.
     */

    private final ResourcePatternResolver resourcePatternResolver = mock(ResourcePatternResolver.class);
    private final LoveAppDocumentLoader loveAppDocumentLoader = new LoveAppDocumentLoader(resourcePatternResolver);

    @Test
    void testLoadMarkdownsWhenNoMarkdownFilesPresent() throws IOException {
        // Arrange
        when(resourcePatternResolver.getResources("classpath:documents/*.md"))
                .thenReturn(new Resource[]{});

        // Act
        List<Document> result = loveAppDocumentLoader.loadMarkdowns();

        // Assert
        assertEquals(0, result.size());
        verify(resourcePatternResolver, times(1)).getResources("classpath:documents/*.md");
    }

    @Test
    void testLoadMarkdownsWhenIOExceptionOccurs() throws IOException {
        // Arrange
        when(resourcePatternResolver.getResources("classpath:documents/*.md"))
                .thenThrow(new IOException("File not found"));

        // Act
        List<Document> result = loveAppDocumentLoader.loadMarkdowns();

        // Assert
        assertEquals(0, result.size());
        verify(resourcePatternResolver, times(1)).getResources("classpath:documents/*.md");
    }
}