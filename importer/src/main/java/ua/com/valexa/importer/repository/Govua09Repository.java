package ua.com.valexa.importer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.importer.model.Govua01;
import ua.com.valexa.importer.model.Govua09;

import java.util.UUID;

@Repository
public interface Govua09Repository extends JpaRepository<Govua09, UUID> {
}
