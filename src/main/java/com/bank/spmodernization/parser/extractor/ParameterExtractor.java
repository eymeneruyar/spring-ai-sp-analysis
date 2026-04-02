package com.bank.spmodernization.parser.extractor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParameterExtractor {

    public List<String> extractParameters(String spText) {
        List<String> params = new ArrayList<>();

        int start = spText.indexOf("(");
        int end = spText.indexOf(")");

        if (start >= 0 && end > start) {
            String inside = spText.substring(start + 1, end);
            String[] split = inside.split(",");

            for (String s : split) {
                String value = s.trim();
                if (!value.isBlank()) {
                    params.add(value);
                }
            }
        }

        return params;
    }
}
