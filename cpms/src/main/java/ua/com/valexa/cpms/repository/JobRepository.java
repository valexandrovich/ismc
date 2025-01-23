package ua.com.valexa.cpms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.cpms.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}
