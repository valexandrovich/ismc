package ua.com.valexa.enricher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.enricher.model.TagType;

@Repository
public interface TagTypeRepository extends JpaRepository<TagType, Long> {
}
