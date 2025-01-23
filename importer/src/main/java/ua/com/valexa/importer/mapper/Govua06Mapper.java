package ua.com.valexa.importer.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ua.com.valexa.afscommon.dto.red.govua.Govua01Dto;
import ua.com.valexa.afscommon.dto.red.govua.Govua06Dto;
import ua.com.valexa.importer.model.Govua01;
import ua.com.valexa.importer.model.Govua06;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
@Component
public interface Govua06Mapper {
    @Mapping(source="debtor_name", target="debtorName")

    @Mapping(expression = "java(parseDateFromString(dto.getDebtor_birthdate()))", target = "debtorBirthdate"  )
//    @Mapping(source="debtor_birthdate", target="debtorBirthdate")
    @Mapping(source="debtor_code", target="debtorCode")
    @Mapping(source="creditor_name", target="creditorName")
    @Mapping(source="creditor_code", target="creditorCode")
    @Mapping(source="vp_ordernum", target="vpOrdernum")

    @Mapping(expression = "java(parseDateFromString(dto.getVp_begindate()))", target = "vpBegindate"  )
//    @Mapping(source="vp_begindate", target="vpBegindate")
    @Mapping(source="vp_state", target="vpState")
    @Mapping(source="dvs_code", target="dvsCode")
    @Mapping(source="org_name", target="orgName")
    @Mapping(source="phone_num", target="phoneNum")
    @Mapping(source="email_addr", target="emailAddr")
    @Mapping(source="bank_account", target="bankAccount")
    Govua06 mapToEntity(Govua06Dto dto);


    @AfterMapping
    default void afterNapping(Govua06Dto dto, @MappingTarget Govua06 entity){

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.M.yyyy HH:mm:ss");
        try {
            return   LocalDate.ofInstant(simpleDateFormat.parse(dateString).toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            return null;
        }
    }
}
