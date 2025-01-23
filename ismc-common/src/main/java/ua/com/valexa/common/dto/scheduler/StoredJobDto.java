package ua.com.valexa.common.dto.scheduler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredJobDto {

    private Long id;
    private String alias;
    private String name;
    private String source;
    private Boolean isEnabled = true;

}
