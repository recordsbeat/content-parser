package com.recordsbeat.contentparser.domain;

import com.recordsbeat.contentparser.web.dto.ResultDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentTest {

    Content content;
    int chunkSize;
    @BeforeEach
    void setUp() {
        String contentsText="20jfASg9xAOFF@Nnvos%%%OdsnA@20502";
        chunkSize = 7;
        content = new Content(contentsText,chunkSize);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void generateResult() {
        ResultDto resultDto = content.generateResult();
        int strLength = content.extractAlphabets().length() + content.extractDigits().length();
        int resultLength = resultDto.getFullResult().length();

        assertEquals(resultDto.getShare().length()%chunkSize, 0);
        assertTrue(resultDto.getRest().length()<chunkSize);
        assertEquals(strLength, resultLength);
        assertTrue(resultDto.getFullResult().matches("^[a-zA-Z0-9]*$"));
    }

}