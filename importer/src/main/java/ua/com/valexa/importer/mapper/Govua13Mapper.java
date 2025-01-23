package ua.com.valexa.importer.mapper;


import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.red.govua.Govua13Dto;
import ua.com.valexa.importer.model.Govua13;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

@Mapper(componentModel = "spring")
@Component
public interface Govua13Mapper {
    @Mapping(expression = "java(parseDateFromString(dto.getTheftDate()))", target = "theftDate")
    @Mapping(expression = "java(parseDateFromString(dto.getInsertDate()))", target = "insertDate")
    Govua13 mapToEntity(Govua13Dto dto);


    @AfterMapping
    default void afterNapping(Govua13Dto dto, @MappingTarget Govua13 entity) {
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

    default LocalDate parseDateFromString(String dateString) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter();

        try {
            TemporalAccessor temporalAccessor = formatter.parseBest(dateString, LocalDateTime::from, LocalDate::from);
            if (temporalAccessor instanceof LocalDateTime) {
                return ((LocalDateTime) temporalAccessor).toLocalDate();
            } else if (temporalAccessor instanceof LocalDate) {
                return (LocalDate) temporalAccessor;
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}


