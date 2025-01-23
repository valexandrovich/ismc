package ua.com.valexa.importer.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.uploader.UploaderLeRowDto;
import ua.com.valexa.afscommon.dto.uploader.UploaderPpRowDto;
import ua.com.valexa.importer.model.UploadLe;
import ua.com.valexa.importer.model.UploadPp;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
@Component
public interface UploadLeMapper {

    @Mapping(expression = "java(parseDateFromString(dto.getFoundationDate()))", target = "foundationDate"  )
    @Mapping(expression = "java(parseDateFromString(dto.getMarkEventDate()))", target = "markEventDate"  )
    @Mapping(expression = "java(parseDateFromString(dto.getMarkStartDate()))", target = "markStartDate"  )
    @Mapping(expression = "java(parseDateFromString(dto.getMarkEndDate()))", target = "markEndDate"  )
    UploadLe mapToEntity(UploaderLeRowDto dto);


    @AfterMapping
    default void afterMapping(UploaderLeRowDto dto, @MappingTarget UploadLe entity) {

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


