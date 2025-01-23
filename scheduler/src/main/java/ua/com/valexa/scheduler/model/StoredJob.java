package ua.com.valexa.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Table(schema ="scheduler", name = "stored_job")
@Getter
@Setter
@ToString
public class StoredJob {

    @Id
    private Long id;
    private String alias;
    private String name;
    private String source;
    private Boolean isEnabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private String createdBy;

    private LocalDateTime changedAt = LocalDateTime.now();
    private String changedBy;

    @Transient
    private List<StoredStep> steps;

}