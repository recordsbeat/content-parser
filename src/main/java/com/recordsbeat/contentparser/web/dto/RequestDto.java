package com.recordsbeat.contentparser.web.dto;

import com.recordsbeat.contentparser.enums.ParsingType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class RequestDto {
    private String url;
    private ParsingType parsingType;
    private int chunkSize;

    @Builder
    public RequestDto(String url, ParsingType parsingType, int chunkSize) {
        this.url = url;
        this.parsingType = parsingType;
        this.chunkSize = chunkSize;
    }
}
