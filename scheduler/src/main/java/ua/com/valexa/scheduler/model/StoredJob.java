package ua.com.valexa.scheduler.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(schema ="scheduler", name = "stored_job")
@Getter
@Setter
@NoArgsConstructor
public class StoredJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_name")
    private String shortName;

    private String name;

    @Column(name = "source")
    private String source;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @OneToMany(mappedBy = "storedJob", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<StoredStep> steps;

}
