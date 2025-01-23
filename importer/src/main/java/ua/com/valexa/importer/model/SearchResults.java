package ua.com.valexa.importer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchResults {

    List<Govua01> govua01List = new ArrayList<>();
    List<Govua06> govua06List = new ArrayList<>();
    List<Govua07> govua07List = new ArrayList<>();
    List<Govua08> govua08List = new ArrayList<>();
    List<Govua09> govua09List = new ArrayList<>();
    List<Govua10> govua10List = new ArrayList<>();
    List<Govua11> govua11List = new ArrayList<>();
    List<Govua12> govua12List = new ArrayList<>();
    List<Govua13> govua13List = new ArrayList<>();
    List<UploadPp> uploadPpList = new ArrayList<>();
    List<UploadLe> uploadLeList = new ArrayList<>();

}
