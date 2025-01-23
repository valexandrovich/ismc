package ua.com.valexa.scheduler.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;


@Entity
@Table(schema ="scheduler", name = "stored_step")
@Getter
@Setter
@NoArgsConstructor
public class StoredStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service")
    private String service;

    @Column(name = "worker")
    private String worker;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "is_skippable")
    private Boolean isSkippable = false;

    @Column(name = "step_order")
    private Integer stepOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stored_job_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private StoredJob storedJob;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "scheduler", name = "stored_step_parameter", joinColumns = @JoinColumn(name = "stored_step_id"), foreignKey = @ForeignKey(name = "stored_step_parameter__stored_step__fk"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> parameters = new HashMap<>();

}

