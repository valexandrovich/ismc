package ua.com.valexa.scheduler.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ua.com.valexa.scheduler.model.StoredJob;

public interface StoredJobRepository extends R2dbcRepository<StoredJob, Long> {
}
