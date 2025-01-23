package ua.com.valexa.importer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.importer.model.Govua01;
import ua.com.valexa.importer.model.Govua06;

import java.util.UUID;

@Repository
public interface Govua06Repository extends JpaRepository<Govua06, UUID> {
}
