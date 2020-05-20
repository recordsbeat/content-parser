package com.recordsbeat.contentparser.web;

import com.recordsbeat.contentparser.service.ContentsParserService;
import com.recordsbeat.contentparser.web.dto.RequestDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
public class ContentsParserController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ContentsParserService contentsParserService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @ResponseBody
    @PostMapping("/request")
    public ResponseEntity<?> requestParsing(@RequestBody RequestDto requestDto){
        ResponseEntity<?> result;
        try {
            result = new ResponseEntity<>(contentsParserService.parseContent(requestDto), HttpStatus.OK);
        }
        catch(Exception e) {
            logger.debug(e.getMessage());
            result = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST) ;
        }
        return result;
    }
}
