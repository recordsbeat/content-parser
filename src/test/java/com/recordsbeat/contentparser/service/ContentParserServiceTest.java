package com.recordsbeat.contentparser.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.recordsbeat.contentparser.enums.ParsingType;
import com.recordsbeat.contentparser.web.dto.RequestDto;
import com.recordsbeat.contentparser.web.dto.ResultDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
class ContentParserServiceTest {

    @Autowired
    ContentParserService contentsParserService;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getContent() throws Exception{

        String url = "https://en.dict.naver.com/#/main";

        ParsingType parsingType = ParsingType.TEXT;
        String result = contentsParserService.getContent(url, parsingType);

        System.out.println("result : " + result);

        assertNotNull(result);
    }

    @Test
    void getDynamicContent()throws Exception{

        String url = "https://en.dict.naver.com/#/main";

        WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10 * 1000);
        HtmlPage page = webClient.getPage(url);
        webClient.waitForBackgroundJavaScript(2 * 1000); // maximum 2sec

        String pageAsXml = page.asXml();

        Document doc = Jsoup.parse(pageAsXml, url);

        String result = ParsingType.TEXT.getContent(doc);

        System.out.println("result : " + result);

        assertNotNull(result);
    }

    @Test
    void extractAlphabets() throws Exception{
        String pattern = "^[a-zA-Z]*$";

        String url = "https://stackoverflow.com/questions/39568461/jsoup-statuscode-wont-print-404-error";
        ParsingType parsingType = ParsingType.HTML;
        String result = contentsParserService.getContent(url, parsingType);
        String extractedStr = result.replaceAll("[^a-zA-Z]+", "");
        System.out.println("extractedStr : " + extractedStr);

        assertTrue(extractedStr.matches(pattern));
    }

    @Test
    void extractDigits() throws Exception{
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


        String result = strList.stream()
                .filter(x-> x.length()==chunkSize)
                .peek(x-> System.out.println("elm : " + x))
                .collect(Collectors.joining());

        assertEquals(result, "123123123123123123123123");
    }

    @Test
    void parseContent() throws Exception{
        RequestDto requestDto = RequestDto.builder()
                .url("https://front.wemakeprice.com/main")
                .parsingType(ParsingType.TEXT)
                .chunkSize(10)
                .build();
        ResultDto resultDto = contentsParserService.parseContent(requestDto);

        System.out.println("result : " + resultDto);

        assertEquals(resultDto.getShare().length()%requestDto.getChunkSize(), 0);
        assertTrue(resultDto.getRest().length()<requestDto.getChunkSize());
    }

}