package com.recordsbeat.contentparser.domain;

import com.recordsbeat.contentparser.web.dto.ResultDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Content {
    private final static String digitPattern = "^[0-9]*$";
    private final static String alphabetPattern = "^[a-zA-Z]*$";

    private final static String digitExtractionPattern = "[^0-9]+";
    private final static String alphabetExtractionPattern = "[^a-zA-Z]+";

    private final String contentText;
    private final int chunkSize;


    public Content(String contentText, int chunkSize) {
        this.contentText = contentText;
        this.chunkSize = chunkSize;
    }

    public ResultDto generateResult(){
        //extraction, sorting, merge
        String mergedString = mergeStrings(sortCapitalPrior(),sortDigits());

        //split string in chunks
        List<String> split = splitInChunks(mergedString);

        return calResult(split);
    }

    private ResultDto calResult(List<String> stringList){
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

    private List<String> splitInChunks(String str){
        if(this.chunkSize<1)
            throw new IllegalArgumentException("chunk size must be bigger than 0");

        final AtomicInteger count =new AtomicInteger(0);
        Collection<String> splitStrList = convertStrToList(str).stream()
                .collect(Collectors.groupingBy(e-> count.getAndIncrement()/this.chunkSize
                        , Collectors.joining()))
                .values();

        return new ArrayList<>(splitStrList);
    }

    private String mergeStrings(String str1, String str2) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < str1.length() || i < str2.length(); i++) {
            if (i < str1.length())
                result.append(str1.charAt(i));

            if (i < str2.length())
                result.append(str2.charAt(i));
        }

        return result.toString();
    }

    private String sortCapitalPrior() {
        String alphabets = extractAlphabets();
        if(!alphabets.matches(alphabetPattern))
            throw new IllegalArgumentException("parameter ["+alphabets+"] does not contain only alphabets");

        List<String> sortedAlphabets = convertStrToList(alphabets);
        sortedAlphabets.sort((str1, str2) -> {
            if (str1.toUpperCase().equals(str2.toUpperCase()))
                return str1.compareTo(str2);
            return str1.toUpperCase().compareTo(str2.toUpperCase());
        });

        return String.join("", sortedAlphabets);
    }

    private String sortDigits() {
        String digits = extractDigits();
        if(!digits.matches(digitPattern))
            throw new IllegalArgumentException("parameter ["+digits+"] does not contain only digits");

        char[] sortedDigits = digits.toCharArray();
        Arrays.sort(sortedDigits);
        return Stream.of(sortedDigits)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public String extractAlphabets() {
        return contentText.replaceAll(alphabetExtractionPattern, "");
    }

    public String extractDigits() {
        return contentText.replaceAll(digitExtractionPattern, "");
    }
    private List<String> convertStrToList(String str){
        return Arrays.asList(str.split(""));
    }
}
