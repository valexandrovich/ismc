package ua.com.valexa.afscommon.dto.uploader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UploaderPpRequestDto {

    private String userName;
    private String fileName;
    private List<UploaderPpRowDto> rows = new ArrayList<>();

}
