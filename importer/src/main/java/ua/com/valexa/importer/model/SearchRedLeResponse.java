package ua.com.valexa.importer.model;

import lombok.Getter;
import lombok.Setter;
import ua.com.valexa.afscommon.dto.red.govua.LegalEntitySearchDto;

@Getter
@Setter
public class SearchRedLeResponse {
    LegalEntitySearchDto legalEntitySearchDto;
    SearchResults searchResults = new SearchResults();
}