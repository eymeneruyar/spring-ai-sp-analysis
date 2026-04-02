package com.bank.spmodernization.parser.metadata;

import com.bank.spmodernization.common.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    private String fileName;
    private Long fileSize;
    private FileType fileType;
    private String content;
}
