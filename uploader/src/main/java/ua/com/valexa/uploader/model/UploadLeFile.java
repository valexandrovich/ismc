package ua.com.valexa.uploader.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "uploader", name = "upload_file_le")
@Getter
@Setter
@NoArgsConstructor
public class UploadLeFile {
    @Id
    private UUID id;
    private String fileName;
    private String filePath;
    private String author;
    private Boolean isNew = true;
    private LocalDateTime createDate;
    private Long rowsCount;

}
