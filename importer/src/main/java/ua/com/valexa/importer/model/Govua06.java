package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua_06", schema = "red", indexes = {
        @Index(name = "govua_06__debtor_name__index", columnList = "debtor_name"),
        @Index(name = "govua_06__debtor_code__index", columnList = "debtor_code"),
        @Index(name = "govua_06__debtor_birthday__index", columnList = "debtor_birthday")
})
@Getter
@Setter
public class Govua06 {
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

//    @Lob
    @Column(name = "debtor_name", columnDefinition = "TEXT")
    public String debtorName;
    @Column(name = "debtor_birthdate")
    public LocalDate debtorBirthdate;
    @Column(name = "debtor_code")
    public String debtorCode;
//    @Lob
    @Column(name = "creditor_name", columnDefinition = "TEXT")
    public String creditorName;
    @Column(name = "creditor_code")
    public String creditorCode;
    @Column(name = "vp_ordernum")
    public String vpOrdernum;
    @Column(name = "vp_state")
    public String vpState;
    @Column(name = "vp_begindate")
    public LocalDate vpBegindate;
//    @Lob
    @Column(name = "org_name", columnDefinition = "TEXT")
    public String orgName;
    @Column(name = "dvs_code")
    public String dvsCode;
    @Column(name = "phone_num")
    public String phoneNum;
    @Column(name = "email_addr")
    public String emailAddr;
//    @Lob
    @Column(name = "bank_account",  columnDefinition = "TEXT")
    public String bankAccount;

    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((debtorName
                + debtorBirthdate
                + debtorCode
                + creditorName
                + creditorCode
                + vpOrdernum
                + vpBegindate
                + vpState
                + orgName
                + dvsCode
                + phoneNum
                + emailAddr
                + bankAccount
        ).getBytes());
    }


//    public boolean isPp(){
//        if (
//                firmEdrpou != null
//                        && !firmEdrpou.isBlank()
//                        && firmEdrpou.length() == 10
//                        &&firmEdrpou.matches("\\d+")
//
//        ){
//            return true;
//        }
//        return false;
//    }
//
//    public boolean isLe(){
//        if (firmEdrpou != null
//                && !firmEdrpou.isBlank()
//                && firmEdrpou.length() <= 8
//                && firmEdrpou.length() >= 5
//                &&firmEdrpou.matches("\\d+")
//        ){
//            return true;
//        }
//        return false;
//
//    }


}
