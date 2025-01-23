package ua.com.valexa.scheduler.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import ua.com.valexa.common.dto.scheduler.StoredJobDto;
import ua.com.valexa.scheduler.model.StoredJob;

@Mapper(componentModel = "spring")
public interface StoredJobMapper {

    StoredJobDto toDto(StoredJob storedJob);

    @BeforeMapping
    default void fillSteps(StoredJob storedJob, StoredJobDto storedJobDto) {
        System.out.println("Before Mapping");
        System.out.println("Stored Job: " + storedJob);
        System.out.println("Stored Job DTO: " + storedJobDto);
    }

    @AfterMapping
    default void fillStepsAfter(StoredJob storedJob, StoredJobDto storedJobDto) {
        System.out.println("After Mapping");
        System.out.println("Stored Job: " + storedJob);
        System.out.println("Stored Job DTO: " + storedJobDto);
    }

}
