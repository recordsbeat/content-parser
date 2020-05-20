package com.recordsbeat.contentparser.enums;

import lombok.ToString;
import org.jsoup.nodes.Document;

import java.util.function.Function;

@ToString
public enum ParsingType {
    TEXT(document -> document.text()),
    HTML(document -> document.html());

    private Function<Document, String> expression;

    ParsingType(Function<Document, String> expression){this.expression = expression;}

    public String getContent(Document document){
        return expression.apply(document);
    }
}
