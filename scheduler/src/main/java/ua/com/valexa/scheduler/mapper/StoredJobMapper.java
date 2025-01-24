package ua.com.valexa.scheduler.mapper;

import org.mapstruct.Mapper;
import ua.com.valexa.common.dto.scheduler.StoredJobDto;
import ua.com.valexa.scheduler.model.StoredJob;

@Mapper(componentModel = "spring")
public interface StoredJobMapper {

    StoredJobDto toDto(StoredJob storedJob);


}
