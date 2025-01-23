package ua.com.valexa.uploader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.uploader.model.UploadLeFile;
import ua.com.valexa.uploader.model.UploadPpFile;

import java.util.UUID;

@Repository
public interface UploadLeFileRepository extends JpaRepository<UploadLeFile, UUID> {
}
