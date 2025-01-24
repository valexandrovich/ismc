package ua.com.valexa.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(schema ="scheduler", name = "stored_step_parameter")
@Getter
@Setter
public class StoredStepParameter {
        @Id
        private Long id;
        private Long storedStepId;
        private String key;
        private String value;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoredStepParameter that = (StoredStepParameter) o;
        return Objects.equals(storedStepId, that.storedStepId) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storedStepId, key);
    }
}
