package ua.com.valexa.importer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.importer.model.Govua01;

import java.util.UUID;

@Repository
public interface Govua01Repository extends JpaRepository<Govua01, UUID> {
}
