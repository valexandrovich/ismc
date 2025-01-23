package ua.com.valexa.importer.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.red.govua.Govua13Dto;
import ua.com.valexa.afscommon.dto.uploader.UploaderPpRowDto;
import ua.com.valexa.importer.model.Govua13;
import ua.com.valexa.importer.model.UploadPp;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

@Mapper(componentModel = "spring")
@Component
public interface UploadPpMapper {
//    @Mapping(expression = "java(parseDateFromString(dto.getTheftDate()))", target = "theftDate")
//    @Mapping(expression = "java(parseDateFromString(dto.getInsertDate()))", target = "insertDate")
@Mapping(expression = "java(parseDateFromString(dto.getBirthday()))", target = "birthday"  )
@Mapping(expression = "java(parseDateFromString(dto.getLocalPassIssueDate()))", target = "localPassIssueDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getIdPassIssueDate()))", target = "idPassIssueDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getIntPassIssueDate()))", target = "intPassIssueDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getOthPassIssueDate()))", target = "othPassIssueDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getOthPassExpiredDate()))", target = "othPassExpiredDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getMarkEventDate()))", target = "markEventDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getMarkStartDate()))", target = "markStartDate"  )
@Mapping(expression = "java(parseDateFromString(dto.getMarkEndDate()))", target = "markEndDate"  )

    UploadPp mapToEntity(UploaderPpRowDto dto);


    @AfterMapping
    default void afterMapping(UploaderPpRowDto dto, @MappingTarget UploadPp entity) {

        System.out.println("AFTER MAPPIGN");
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


