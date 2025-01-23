package ua.com.valexa.cpms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(schema = "cpms", name = "step")
@Getter
@Setter
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", foreignKey = @ForeignKey(name = "step__job__fk"))
    @JsonBackReference
    private Job job;

    @Column(name = "step_order")
    private Integer stepOrder;

    private String service;

    private String worker;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    private Double progress;

    @Enumerated(EnumType.STRING)
    TaskStatus status;

    @Column(columnDefinition = "text")
    private String comment;

    @Column(name = "is_skippable")
    private Boolean isSkippable;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "cpms", name = "step_parameters",
            joinColumns = @JoinColumn(
                    name = "step_id",
                    foreignKey = @ForeignKey(name = "fk__step_parameters__step_id")
            )
    )
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(id, step.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", status=" + status +
                ", progress=" + progress +
                ", comment='" + comment + '\'' +
                ", stepOrder=" + stepOrder +
                ", service='" + service + '\'' +
                ", worker='" + worker + '\'' +
                ", isSkippable=" + isSkippable +
                ", parameters=" + parameters +
                '}';
    }
}
