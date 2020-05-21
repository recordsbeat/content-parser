package com.recordsbeat.contentparser.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.recordsbeat.contentparser.domain.Content;
import com.recordsbeat.contentparser.enums.ParsingType;
import com.recordsbeat.contentparser.web.dto.RequestDto;
import com.recordsbeat.contentparser.web.dto.ResultDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;


@Service
public class ContentParserService {

    public ResultDto parseContent(RequestDto requestDto) throws Exception {
        //crawling
        String crawlResult = getContent(requestDto.getUrl(), requestDto.getParsingType());

        Content contents = new Content(crawlResult, requestDto.getChunkSize());
        return contents.generateResult();
    }


    public String getContent(String url, ParsingType parsingType) throws Exception {
        //dynamic content crawling
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(10 * 1000);
        HtmlPage page = webClient.getPage(url);
        webClient.waitForBackgroundJavaScript(3 * 1000); // maximum 3sec

        String pageAsXml = page.asXml();

        Document document = Jsoup.parse(pageAsXml, url);

        /*
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("http://www.google.com")
                .get();
        */

        return parsingType.getContent(document);
    }
}
