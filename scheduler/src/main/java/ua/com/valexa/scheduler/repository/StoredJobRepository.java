package ua.com.valexa.scheduler.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ua.com.valexa.scheduler.model.StoredJob;

public interface StoredJobRepository extends ReactiveCrudRepository<StoredJob, Long> {
}
