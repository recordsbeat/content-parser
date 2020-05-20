package com.recordsbeat.contentparser.service;

import com.recordsbeat.contentparser.enums.ParsingType;
import com.recordsbeat.contentparser.web.ResultDto;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
    void parsingContent() {
        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.TEXT;
        String result = contentsParserService.parsingContent(url, parsingType);

        System.out.println("result : " + result);

        //assertAll();
    }

    @Test
    void extractAlphabets() {
        String pattern = "^[a-zA-Z]*$";

        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.HTML;
        String result = contentsParserService.parsingContent(url, parsingType);
        String extractedStr = contentsParserService.extractAlphabets(result);
        System.out.println("extractedStr : " + extractedStr);

        assertTrue(extractedStr.matches(pattern));
    }

    @Test
    void extractDigits() {
        String pattern = "^[0-9]*$";

        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.TEXT;
        String result = contentsParserService.parsingContent(url, parsingType);

        String extractedStr = contentsParserService.extractDigits(result);
        System.out.println("extractedStr : " + extractedStr);
        assertTrue(extractedStr.matches(pattern));
    }

    @Test
    void sortDigits() {
        String digits = "2035160349672037";
        String result = contentsParserService.sortDigits(digits);

        System.out.println("sortDigits : " + result);
    }

    @Test
    void sortCapitalPrior(){
        String alphabets = "SadoSDGJvANCNEaosdW";
        String result = contentsParserService.sortCapitalPrior(alphabets);
        System.out.println("result : " + result);

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
    }

    @Test
    void mergeString() {
        String alphabets = contentsParserService.sortCapitalPrior("aAAaddDbB");
        String digits = contentsParserService.sortDigits("2035160349672037");

        String result = contentsParserService.mergeStrings(alphabets,digits);
        System.out.println("result : " + result);
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
                .filter(x-> x.length()==3)
                .forEach(x-> System.out.println("elm : " + x));
    }

    @Test
    void splitInChunks() {
        String str = "12312312312312312312312312";
        int chunkSize = 3;
        contentsParserService.splitInChunks(str,chunkSize)
                .stream()
                .filter(x-> x.length()==chunkSize)
                .peek(x -> assertSame(x.length(),chunkSize))
                .forEach(x-> System.out.println("elm : " + x));
    }

    @Test
    void calResult() {
        String str = "12312312312312312312312312";
        int chunkSize = 3;
        List<String> res = contentsParserService.splitInChunks(str,chunkSize);

        ResultDto result = contentsParserService.calResult(res,chunkSize);
        System.out.println("result : " + result);
        assertEquals(result.getShare(), "123123123123123123123123");
        assertEquals(result.getRest(), "12");

    }
}