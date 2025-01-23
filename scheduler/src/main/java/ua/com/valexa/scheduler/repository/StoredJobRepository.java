package ua.com.valexa.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.scheduler.model.StoredJob;

@Repository
public interface StoredJobRepository extends JpaRepository<StoredJob, Long> {
}
