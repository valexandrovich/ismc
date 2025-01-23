package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua_08", schema = "red", indexes = {
        @Index(name = "govua_08__last_name_ua__index", columnList = "last_name_ua"),
        @Index(name = "govua_08__first_name_ua__index", columnList = "first_name_ua"),
        @Index(name = "govua_08__patronymic_name_ua__index", columnList = "patronymic_name_ua"),
        @Index(name = "govua_08__last_name_ru__index", columnList = "last_name_ru"),
        @Index(name = "govua_08__first_name_ru__index", columnList = "first_name_ru"),
        @Index(name = "govua_08__patronymic_name_ru__index", columnList = "patronymic_name_ru"),
        @Index(name = "govua_08__last_name_en__index", columnList = "last_name_en"),
        @Index(name = "govua_08__first_name_en__index", columnList = "first_name_en"),
        @Index(name = "govua_08__patronymic_name_en__index", columnList = "patronymic_name_en"),
        @Index(name = "govua_08__birthday__index", columnList = "birthday"),
})
@Getter
@Setter
public class Govua08 {
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

    @Column(name = "id")
    private String id;
    @Column(name = "ovd")
    private String ovd;
    @Column(name = "category")
    private String category;

    @Column(name = "last_name_ua")
    private String lastNameUa;
    @Column(name = "first_name_ua")
    private String firstNameUa;
    @Column(name = "patronymic_name_ua")
    private String patronymicNameUa;

    @Column(name = "last_name_ru")
    private String lastNameRu;
    @Column(name = "first_name_ru")
    private String firstNameRu;
    @Column(name = "patronymic_name_ru")
    private String patronymicNameRu;

    @Column(name = "last_name_en")
    private String lastNameEn;
    @Column(name = "first_name_en")
    private String firstNameEn;
    @Column(name = "patronymic_name_en")
    private String patronymicNameEn;

    @Column(name = "birthday")
    private LocalDate birthday;
    @Column(name = "sex")
    private String sex;
    @Column(name = "lost_date")
    private LocalDate lostDate;
    @Column(name = "lost_place")
    private String lostPlace;
    @Column(name = "article_crim")
    private String articleCrim;
    @Column(name = "restraint")
    private String restraint;
    @Column(name = "contact")
    private String contact;
    @Column(name = "photoid")
    private String photoid;

    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((
                id +
                        ovd +
                        category +
                        firstNameUa +
                        lastNameUa +
                        patronymicNameUa +

                        firstNameRu +
                        lastNameRu +
                        patronymicNameRu +

                        firstNameEn +
                        lastNameEn +
                        patronymicNameEn +


                        birthday +
                        sex +
                        lostDate +
                        lostPlace +
                        articleCrim +
                        restraint +
                        contact +
                        photoid
        ).getBytes());
    }

}
