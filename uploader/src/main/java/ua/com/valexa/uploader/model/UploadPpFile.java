package ua.com.valexa.uploader.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "uploader", name = "upload_file_pp")
@Getter
@Setter
@NoArgsConstructor
public class UploadPpFile {
    @Id
    private UUID id;
    private String fileName;
    private String filePath;
    private String author;
    private Boolean isNew = true;
    private LocalDateTime createDate;
    private Long rowsCount;

}
