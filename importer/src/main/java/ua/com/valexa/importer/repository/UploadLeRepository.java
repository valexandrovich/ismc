package ua.com.valexa.importer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.importer.model.UploadLe;
import ua.com.valexa.importer.model.UploadPp;

import java.util.UUID;

@Repository
public interface UploadLeRepository extends JpaRepository<UploadLe, UUID> {
}
