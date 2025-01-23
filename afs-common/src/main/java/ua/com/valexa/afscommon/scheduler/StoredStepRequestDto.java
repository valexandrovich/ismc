package ua.com.valexa.afscommon.scheduler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class StoredStepRequestDto {
    private Integer stepOrder;
    private String service;
    private String worker;
    private Boolean isSkipable;
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public String toString() {
        return "StoredStepRequestDto{" +
                "stepOrder=" + stepOrder +
                ", service='" + service + '\'' +
                ", worker='" + worker + '\'' +
                ", isSkipable=" + isSkipable +
                ", parameters=" + parameters +
                '}';
    }
}
