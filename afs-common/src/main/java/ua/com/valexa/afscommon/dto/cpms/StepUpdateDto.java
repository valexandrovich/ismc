package ua.com.valexa.afscommon.dto.cpms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class StepUpdateDto {

    private Long stepId;
    private TaskStatus status;
    private Double progress;
    private String comment;
    private Map<String, String> results = new HashMap<>();

    @Override
    public String toString() {
        return "StepUpdateDto{" +
                "stepId=" + stepId +
                ", status=" + status +
                ", progress=" + progress +
                ", comment='" + comment + '\'' +
                ", results=" + results +
                '}';
    }
}
