package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua_07", schema = "red", indexes = {
        @Index(name = "govua_07__debtor_name__index", columnList = "debtor_name"),
        @Index(name = "govua_07__debtor_code__index", columnList = "debtor_code"),
        @Index(name = "govua_07__debtor_birthday__index", columnList = "debtor_birthday")
})
@Getter
@Setter
public class Govua07 {

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

    @Column(name = "debtor_name")
    private String debtorName;
    @Column(name = "debtor_birthdate")
    private LocalDate debtorBirthdate;
    @Column(name = "debtor_code")
    private String debtorCode;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "org_name")
    private String orgName;
    @Column(name = "org_phone_num")
    private String orgPhoneNum;
    @Column(name = "emp_full_fio")
    private String empFullFio;
    @Column(name = "emp_phone_num")
    private String empPhoneNum;
    @Column(name = "email_addr")
    private String emailAddr;
    @Column(name = "vp_ordernum")
    private String vpOrdernum;
    @Column(name = "vd_cat")
    private String vdCat;


    public boolean isPp() {
        if (
                debtorBirthdate != null
        ) {
            return true;
        }
        return false;
    }

    public boolean isLe() {
        if (debtorCode != null
                && !debtorCode.isBlank()
                && debtorCode.length() <= 8
                && debtorCode.length() >= 5
                && debtorCode.matches("\\d+")
        ) {
            return true;
        }
        return false;
    }


    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((
                debtorName
                        + debtorBirthdate
                        + debtorCode
                        + publisher
                        + orgName
                        + orgPhoneNum
                        + empFullFio
                        + empPhoneNum
                        + emailAddr
                        + vpOrdernum
                        + vdCat
        ).getBytes());
    }
}
