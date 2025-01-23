package ua.com.valexa.importer.model;

import lombok.Getter;
import lombok.Setter;
import ua.com.valexa.afscommon.dto.red.govua.PersonSearchDto;

@Getter
@Setter
public class SearchRedPpResponse {
    PersonSearchDto personSearchDto;
    SearchResults searchResults = new SearchResults();

}
