package com.recordsbeat.contentparser.service;

import com.recordsbeat.contentparser.domain.Content;
import com.recordsbeat.contentparser.enums.ParsingType;
import com.recordsbeat.contentparser.web.dto.RequestDto;
import com.recordsbeat.contentparser.web.dto.ResultDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ContentsParserServiceTest {

    @Autowired
    ContentsParserService contentsParserService;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getContent() {
        String url = "https://github.com/recordsbeat/content-parser";
        ParsingType parsingType = ParsingType.TEXT;
        String result = contentsParserService.getContent(url, parsingType);

        System.out.println("result : " + result);

        assertNotNull(result);
    }

    @Test
    void extractAlphabets() {
        String pattern = "^[a-zA-Z]*$";

        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.HTML;
        String result = contentsParserService.getContent(url, parsingType);
        String extractedStr = result.replaceAll("[^a-zA-Z]+", "");
        System.out.println("extractedStr : " + extractedStr);

        assertTrue(extractedStr.matches(pattern));
    }

    @Test
    void extractDigits() {
        String pattern = "^[0-9]*$";

        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.TEXT;
        String result = contentsParserService.getContent(url, parsingType);

        String extractedStr = result.replaceAll("[^0-9]+", "");
        System.out.println("extractedStr : " + extractedStr);
        assertTrue(extractedStr.matches(pattern));
    }


    @Test
    void sortCapitalPriorTest(){
        String alphabets = "aAAaddDbB";

        List<String> stringList = Arrays.asList(alphabets.split(""));
        stringList.sort((str1, str2) -> {
            if (str1.toUpperCase().equals(str2.toUpperCase()))
                return str1.compareTo(str2);
            return str1.toUpperCase().compareTo(str2.toUpperCase());
        });

        String result = stringList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        System.out.println("result : " + result);
        assertEquals(result,"AAaaBbDdd");
    }


    @Test
    void splitInChunksTest(){
        String str = "12312312312312312312312312";
        int chunkSize = 3;
        final AtomicInteger count =new AtomicInteger(0);
        List<String> strList = Arrays.asList(str.split(""));
        Collection<String> strings = strList.stream()
                .collect(
                        Collectors.groupingByConcurrent(e-> count.getAndIncrement()/chunkSize
                        ,Collectors.joining()))
                .values();
        strList = new ArrayList<>(strings);


        strList.stream()
                .filter(x-> x.length()==chunkSize)
                .peek(x -> assertSame(x.length(),chunkSize))
                .forEach(x-> System.out.println("elm : " + x));
    }

    @Test
    void parseContent() {
        RequestDto requestDto = RequestDto.builder()
                .url("https://blog.naver.com/PostView.nhn?blogId=acornedu&logNo=221519913222&from=search&redirect=Log&widgetTypeCall=true&directAccess=false")
                .parsingType(ParsingType.TEXT)
                .chunkSize(10)
                .build();
        ResultDto resultDto = contentsParserService.parseContent(requestDto);

        System.out.println("result : " + resultDto);

        assertEquals(resultDto.getShare().length()%requestDto.getChunkSize(), 0);
        assertTrue(resultDto.getRest().length()<requestDto.getChunkSize());
    }

}