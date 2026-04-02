package com.bank.spmodernization.application.service;

import com.bank.spmodernization.domain.model.ProcedureSegment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ProcedureSegmentationService {

    public List<ProcedureSegment> segment(String content) {
        List<ProcedureSegment> segments = new ArrayList<>();
        String lower = content.toLowerCase(Locale.ROOT);

        if (lower.contains("exception")) {
            int exceptionIndex = lower.indexOf("exception");

            String mainPart = content.substring(0, exceptionIndex);
            String exceptionPart = content.substring(exceptionIndex);

            segments.add(ProcedureSegment.builder()
                    .id("SEG-1")
                    .type("MAIN")
                    .content(mainPart)
                    .build());

            segments.add(ProcedureSegment.builder()
                    .id("SEG-2")
                    .type("EXCEPTION")
                    .content(exceptionPart)
                    .build());

            return segments;
        }

        segments.add(ProcedureSegment.builder()
                .id("SEG-1")
                .type("RAW")
                .content(content)
                .build());

        return segments;
    }

}
