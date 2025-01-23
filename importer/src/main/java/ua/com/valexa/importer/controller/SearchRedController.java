package ua.com.valexa.importer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.valexa.afscommon.dto.red.govua.LegalEntitySearchDto;
import ua.com.valexa.afscommon.dto.red.govua.PersonSearchDto;
import ua.com.valexa.importer.model.SearchRedLeResponse;
import ua.com.valexa.importer.model.SearchRedPpResponse;
import ua.com.valexa.importer.service.SearchRedService;


@RestController
@RequestMapping("/search-red")
@Slf4j
public class SearchRedController {

    @Autowired
    SearchRedService searchRedService;

    @PostMapping("/pp")
    public SearchRedPpResponse searchPP(@RequestBody PersonSearchDto dto){
        log.info("PP Search: " + dto);
        return searchRedService.searchPp(dto);
    }

    @PostMapping("/le")
    public SearchRedLeResponse searchLe(@RequestBody LegalEntitySearchDto dto){
        log.info("LE Search: " + dto);
        return searchRedService.searchLe(dto);
    }

}
