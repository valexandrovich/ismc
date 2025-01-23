package ua.com.valexa.cpms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.com.valexa.cpms.model.Step;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
}
