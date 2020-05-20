package com.recordsbeat.contentparser.web;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResultDto {
    private String share;
    private String rest;

    @Builder
    public ResultDto(String share, String rest) {
        this.share = share;
        this.rest = rest;
    }
}
