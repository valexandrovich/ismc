package ua.com.valexa.importer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "upload_le", schema = "red", indexes = {
//        @Index(name = "upload_pp__series__index", columnList = "series"),
})
@Getter
@Setter
public class UploadLe {

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

    private String shortName;
    private String fullName;
    private String opf;

    private LocalDate foundationDate;

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

                shortName +
                        fullName +
                        opf +
                        foundationDate +
                        edrpou +
                        citizenship +
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
