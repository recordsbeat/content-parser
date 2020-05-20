package com.recordsbeat.contentparser.service;

import com.recordsbeat.contentparser.enums.ParsingType;
import com.recordsbeat.contentparser.web.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ContentsParserService {

    private final static String digitPattern = "^[0-9]*$";
    private final static String alphabetPattern = "^[a-zA-Z]*$";

    private final static String digitExtractionPattern = "[^0-9]+";
    private final static String alphabetExtractionPattern = "[^a-zA-Z]+";

    public String parsingContent(String url, ParsingType parsingType) {
        String result = "";
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .get();

            result = parsingType.getContent(document);

        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public String extractAlphabets(String contents) {
        return contents.replaceAll(alphabetExtractionPattern, "");
    }

    public String extractDigits(String contents) {
        return contents.replaceAll(digitExtractionPattern, "");
    }

    public String sortCapitalPrior(String alphabets) {
        if(!alphabets.matches(alphabetPattern))
            throw new IllegalArgumentException("parameter ["+alphabets+"] does not contain only alphabets");

        List<String> sortedAlphabets = strToList(alphabets);
        sortedAlphabets.sort((str1, str2) -> {
            if (str1.toUpperCase().equals(str2.toUpperCase()))
                return str1.compareTo(str2);
            return str1.toUpperCase().compareTo(str2.toUpperCase());
        });

        return String.join("", sortedAlphabets);
    }

    public String sortDigits(String digits) {
        if(!digits.matches(digitPattern))
            throw new IllegalArgumentException("parameter ["+digits+"] does not contain only digits");

        char[] sortedDigits = digits.toCharArray();
        Arrays.sort(sortedDigits);
        return Stream.of(sortedDigits)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public String mergeStrings(String s1, String s2) {
        // To store the final string
        StringBuilder result = new StringBuilder();

        // For every index in the strings
        for (int i = 0; i < s1.length() || i < s2.length(); i++) {

            // First choose the ith character of the
            // first string if it exists
            if (i < s1.length())
                result.append(s1.charAt(i));

            // Then choose the ith character of the
            // second string if it exists
            if (i < s2.length())
                result.append(s2.charAt(i));
        }

        return result.toString();
    }

    public List<String> splitInChunks(String str, int chunkSize){
        final AtomicInteger count =new AtomicInteger(0);
        Collection<String> splitStrList = strToList(str).stream()
                .collect(Collectors.groupingBy(e-> count.getAndIncrement()/chunkSize
                        , Collectors.joining()))
                .values();

        return new ArrayList<>(splitStrList);
    }
    public ResultDto calResult(List<String> stringList, int chunkSize){
        String share;
        String rest = null;
        if(stringList.get(stringList.size()-1).length() < chunkSize)
            rest = stringList.get(stringList.size()-1);

        share = stringList.stream()
                .filter(x -> x.length()==chunkSize)
                .collect(Collectors.joining());

        return ResultDto.builder()
                .share(share)
                .rest(StringUtils.isEmpty(rest) ?"":rest)
                .build();
    }


    private List<String> strToList(String str){
        return Arrays.asList(str.split(""));
    }
}
