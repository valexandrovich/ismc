package ua.com.valexa.afscommon.dto.uploader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UploaderLeRowDto {


    private String shortName;
    private String fullName;
    private String opf;
    private String foundationDate;
    private String edrpou;
    private String citizenship;
    private String phone;
    private String email;
    private String addressSimple;
    private String addressZip;
    private String addressCountry;
    private String addressRegion;
    private String addressCounty;
    private String addressCityType;
    private String addressCityName;
    private String addressStreetType;
    private String addressStreetName;
    private String addressBuildingNo;
    private String addressBuildingPart;
    private String addressBuildingLetter;
    private String addressApartment;
    private String comment;
    private String markId;
    private String markEventDate;
    private String markStartDate;
    private String markEndDate;
    private String markTextValue;
    private String markNumberValue;
    private String markComment;
    private String source;


    private UUID id;







}
