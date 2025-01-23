package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "upload_pp", schema = "red", indexes = {
//        @Index(name = "upload_pp__series__index", columnList = "series"),
})
@Getter
@Setter
public class UploadPp {

    @Id
    @Column(name = "hash")
    private UUID hash;

    @Column(name = "create_revision_id")
    private Long createRevisionId;
    @Column(name = "update_revision_id")
    private Long updateRevisionId;
    @Column(name = "disable_revision_id")
    private Long disableRevisionId;

    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Column(name = "update_date")
    private LocalDateTime updateDate;
    @Column(name = "disable_date")
    private LocalDateTime disableDate;

    private String lastNameUa;
    private String firstNameUa;
    private String patronymicNameUa;

    private String lastNameRu;
    private String firstNameRu;
    private String patronymicNameRu;

    private String lastNameEn;
    private String firstNameEn;
    private String patronymicNameEn;

    private LocalDate birthday;

    private String inn;

    private String localPassSerial;
    private String localPassNum;
    private LocalDate localPassIssueDate;
    private String localPassIssuer;
    private String idPassNumber;
    private String idPassRecord;
    private LocalDate idPassIssueDate;
    private String idPassIssuerCode;
    private String intPassSerial;
    private String intPassNumber;
    private LocalDate intPassIssueDate;
    private String intPassIssuerCode;
    private String othPassName;
    private String othPassNumber;
    private LocalDate othPassIssueDate;
    private LocalDate othPassExpiredDate;
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
    private LocalDate markEventDate;
    private LocalDate markStartDate;
    private LocalDate markEndDate;
    private String markTextValue;
    private String markNumberValue;
    private String markComment;
    private String source;


    private UUID id;

    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((
                lastNameUa +
                        firstNameUa +
                        patronymicNameUa +
                        lastNameRu +
                        firstNameRu +
                        patronymicNameRu +
                        lastNameEn +
                        firstNameEn +
                        patronymicNameEn +
                        birthday +
                        inn +
                        localPassSerial +
                        localPassNum +
                        localPassIssueDate +
                        localPassIssuer +
                        idPassNumber +
                        idPassRecord +
                        idPassIssueDate +
                        idPassIssuerCode +
                        intPassSerial +
                        intPassNumber +
                        intPassIssueDate +
                        intPassIssuerCode +
                        othPassName +
                        othPassNumber +
                        othPassIssueDate +
                        othPassExpiredDate +
                        othPassIssuerName +
                        citizenship +
                        birthplace +
                        sex +
                        phone +
                        email +
                        addressSimple +
                        addressZip +
                        addressCountry +
                        addressRegion +
                        addressCounty +
                        addressCityType +
                        addressCityName +
                        addressStreetType +
                        addressStreetName +
                        addressBuildingNo +
                        addressBuildingPart +
                        addressBuildingLetter +
                        addressApartment +
                        comment +
                        markId +
                        markEventDate +
                        markStartDate +
                        markEndDate +
                        markTextValue +
                        markNumberValue +
                        markComment +
                        source
        ).getBytes());
    }

}
