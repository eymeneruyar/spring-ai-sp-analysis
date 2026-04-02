package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CursorExtractor {

    private static final Pattern CURSOR_PATTERN = Pattern.compile(
            "\\bcursor\\s+([a-zA-Z0-9_]+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    public List<String> extractCursorNames(String content) {
        List<String> cursorNames = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return cursorNames;
        }

        Matcher matcher = CURSOR_PATTERN.matcher(content);

        while (matcher.find()) {
            String cursorName = matcher.group(1);
            if (cursorName != null && !cursorName.isBlank()) {
                cursorNames.add(cursorName.trim());
            }
        }

        return cursorNames.stream().distinct().toList();
    }
}
