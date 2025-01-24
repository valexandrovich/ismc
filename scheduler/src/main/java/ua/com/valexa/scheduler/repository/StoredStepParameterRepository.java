package ua.com.valexa.scheduler.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ua.com.valexa.scheduler.model.StoredStepParameter;

public interface StoredStepParameterRepository extends R2dbcRepository<StoredStepParameter, Long> {
    Flux<StoredStepParameter> findByStoredStepId(Long storedStepId);
}
