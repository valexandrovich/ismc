package ua.com.valexa.afscommon.dto.cpms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class StepRequestDto {

    private Long stepId;
    private String worker;
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public String toString() {
        return "StepRequestDto{" +
                "stepId=" + stepId +
                ", worker='" + worker + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
