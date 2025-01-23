package ua.com.valexa.afscommon.dto.uploader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UploaderPpRowDto {


    private String lastNameUa;
    private String firstNameUa;
    private String patronymicNameUa;

    private String lastNameRu;
    private String firstNameRu;
    private String patronymicNameRu;

    private String lastNameEn;
    private String firstNameEn;
    private String patronymicNameEn;

    private String birthday;

    private String inn;

    private String localPassSerial;
    private String localPassNum;
    private String localPassIssueDate;
    private String localPassIssuer;
    private String idPassNumber;
    private String idPassRecord;
    private String idPassIssueDate;
    private String idPassIssuerCode;
    private String intPassSerial;
    private String intPassNumber;
    private String intPassIssueDate;
    private String intPassIssuerCode;
    private String othPassName;
    private String othPassNumber;
    private String othPassIssueDate;
    private String othPassExpiredDate;
    private String othPassIssuerName;
    private String citizenship;
    private String birthplace;
    private String sex;
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
