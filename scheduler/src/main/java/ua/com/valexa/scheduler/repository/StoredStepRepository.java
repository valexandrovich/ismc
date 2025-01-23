package ua.com.valexa.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.scheduler.model.StoredStep;

@Repository
public interface StoredStepRepository extends JpaRepository<StoredStep, Long> {
}
