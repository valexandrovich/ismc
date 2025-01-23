package ua.com.valexa.importer.service;

import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;

public interface Importable {
    StepUpdateDto handleStepRequest(StepRequestDto stepRequestDto);
}
