package ua.com.valexa.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;

@Table(schema ="scheduler", name = "stored_step")
@Getter
@Setter
public class StoredStep {

    @Id
    private Long id;
    private Long storedJobId;
    private Integer stepOrder;
    private String service;
    private String worker;
    private Boolean isEnabled = true;
    private Boolean isSkippable = false;

    @Transient
    private HashSet<StoredStepParameter> parameters = new HashSet<>();
    

}