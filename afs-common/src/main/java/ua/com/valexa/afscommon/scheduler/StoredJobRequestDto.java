package ua.com.valexa.afscommon.scheduler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StoredJobRequestDto {
    private String shortName;
    private String name;
    private String source;
    private String initiatorName;
    private List<StoredStepRequestDto> steps = new ArrayList<>();

    @Override
    public String toString() {
        return "StoredJobRequestDto{" +
                "shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", initiatorName='" + initiatorName + '\'' +
                ", steps=" + steps +
                '}';
    }
}
