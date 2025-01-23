package ua.com.valexa.scheduler.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ua.com.valexa.scheduler.model.StoredStep;

public interface StoredStepRepository extends R2dbcRepository<StoredStep, Long> {
    Flux<StoredStep> findByStoredJobId(Long storedJobId);
}
