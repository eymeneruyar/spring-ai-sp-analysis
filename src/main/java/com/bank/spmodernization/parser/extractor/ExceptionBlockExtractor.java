package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExceptionBlockExtractor {

    private static final Pattern EXCEPTION_HANDLER_PATTERN = Pattern.compile(
            "\\bwhen\\s+([a-zA-Z0-9_\\s]+?)\\s+then\\b",
            Pattern.CASE_INSENSITIVE
    );

    public List<String> extractExceptionHandlers(String content) {
        List<String> handlers = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return handlers;
        }

        Matcher matcher = EXCEPTION_HANDLER_PATTERN.matcher(content);

        while (matcher.find()) {
            String handler = matcher.group(1);
            if (handler != null && !handler.isBlank()) {
                handlers.add(handler.trim().replaceAll("\\s+", " "));
            }
        }

        return handlers.stream().distinct().toList();
    }

    public boolean hasExceptionBlock(String content) {
        return content != null && content.toLowerCase().contains("exception");
    }
}
