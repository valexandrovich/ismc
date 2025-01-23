package ua.com.valexa.cpms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(schema = "cpms", name = "job",
        indexes = {
                @Index(name = "job__started_at__index", columnList = "started_at")
        }
)
@Getter
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_name")
    private String shortName;

    private String name;

    private String source;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "initiator_name")
    private String initiatorName;

    @OneToMany(fetch = FetchType.EAGER,  orphanRemoval = true)
    @JoinColumn(name = "job_id")
    @JsonManagedReference
    @OrderBy("stepOrder ASC")
    private List<Step> steps = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema = "cpms", name = "job_results",
            joinColumns = @JoinColumn(
                    name = "job_id",
                    foreignKey =  @ForeignKey(name = "fk__job_results__job_id")
            )
    )
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> results = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", initiatorName='" + initiatorName + '\'' +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", steps=" + steps +
                ", results=" + results +
                '}';
    }
}
