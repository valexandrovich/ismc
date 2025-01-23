package ua.com.valexa.importer.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.red.govua.Govua01Dto;
import ua.com.valexa.importer.model.Govua01;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
@Component
public interface Govua01Mapper {
    @Mapping(source="number", target="number")
    @Mapping(expression = "java(parseDateFromString(dto.getDate()))", target = "date"  )
    @Mapping(source="type", target="type")
    @Mapping(source="firm_edrpou", target="firmEdrpou")
    @Mapping(source="firm_name", target="firmName")
    @Mapping(source="case_number", target="caseNumber")
    @Mapping(source="start_date_auc", target="startDateAuc")
    @Mapping(source="end_date_auc", target="endDateAuc")
    @Mapping(source="court_name", target="courtName")
    @Mapping(source="end_registration_date", target="endRegistrationDate")
    Govua01 mapToEntity(Govua01Dto dto);


    @AfterMapping
    default void afterNapping(Govua01Dto dto, @MappingTarget Govua01 entity){
        // Convert all string fields to uppercase
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    String value = (String) field.get(entity);
                    if (value != null) {
                        field.set(entity, value.toUpperCase());
                    }
                } catch (IllegalAccessException e) {
                    // Handle the exception
                }
            }
        }
        entity.generateHash();
        entity.setCreateDate(LocalDateTime.now());
        entity.setUpdateDate(LocalDateTime.now());
    }

    default LocalDate parseDateFromString(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy");
        try {
            return   LocalDate.ofInstant(simpleDateFormat.parse(dateString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            return null;
        }
    }
}
