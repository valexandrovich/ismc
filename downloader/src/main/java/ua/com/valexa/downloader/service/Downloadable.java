package ua.com.valexa.downloader.service;

import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;

public interface Downloadable {
    StepUpdateDto handleStepRequest(StepRequestDto stepRequestDto);
}
