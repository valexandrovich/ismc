package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua_12", schema = "red", indexes = {
        @Index(name = "govua_12__series__index", columnList = "series"),
        @Index(name = "govua_12__number__index", columnList = "number")
})
@Getter
@Setter
public class Govua12 {

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
    @Column(name = "series")
    private String series;
    @Column(name = "number")
    private String number;
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private String status;
    @Column(name = "theft_date")
    private LocalDate theftDate;
    @Column(name = "insert_date")
    private LocalDate insertDate;

    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((
                id +
                        ovd +
                        series +
                        number +
                        type +
                        status +
                        theftDate +
                        insertDate
        ).getBytes());
    }

}
