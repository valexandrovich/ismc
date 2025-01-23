package ua.com.valexa.importer.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.red.govua.Govua06Dto;
import ua.com.valexa.afscommon.dto.red.govua.Govua07Dto;
import ua.com.valexa.importer.model.Govua06;
import ua.com.valexa.importer.model.Govua07;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
@Component
public interface Govua07Mapper {
    @Mapping(source = "debtorName", target = "debtorName")
    @Mapping(expression = "java(parseDateFromString(dto.getDebtorBirthdate()))", target = "debtorBirthdate")
    @Mapping(source = "debtorCode", target = "debtorCode")
    @Mapping(source = "publisher", target = "publisher")
    @Mapping(source = "orgName", target = "orgName")
    @Mapping(source = "orgPhoneNum", target = "orgPhoneNum")
    @Mapping(source = "empFullFio", target = "empFullFio")
    @Mapping(source = "empPhoneNum", target = "empPhoneNum")
    @Mapping(source = "emailAddr", target = "emailAddr")
    @Mapping(source = "vpOrdernum", target = "vpOrdernum")
    @Mapping(source = "vdCat", target = "vdCat")
    Govua07 mapToEntity(Govua07Dto dto);

    @AfterMapping
    default void afterNapping(Govua07Dto dto, @MappingTarget Govua07 entity) {
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy");
        try {
            return LocalDate.ofInstant(simpleDateFormat.parse(dateString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            return null;
        }
    }
}
