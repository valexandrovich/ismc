package ua.com.valexa.enricher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.valexa.enricher.model.TagType;
import ua.com.valexa.enricher.repository.TagTypeRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagTypeController {

    private final TagTypeRepository tagTypeRepository;

    @GetMapping("/tag-types")
    public List<TagType> getTagTypes() {
        return tagTypeRepository.findAll();
    }

}
