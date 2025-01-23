package ua.com.valexa.importer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.importer.model.UploadPp;

import java.util.UUID;

@Repository
public interface UploadPpRepository extends JpaRepository<UploadPp, UUID> {
}
