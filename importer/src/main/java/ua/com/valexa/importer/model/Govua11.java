package ua.com.valexa.importer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "govua_11", schema = "red", indexes = {
        @Index(name = "govua_11__series__index", columnList = "series"),
        @Index(name = "govua_11__number__index", columnList = "number")
})
@Getter
@Setter
public class Govua11 {

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

    @Column(name = "nn")
    private String nn;
    @Column(name = "status")
    private String status;
    @Column(name = "series")
    private String series;
    @Column(name = "number")
    private String number;
    @Column(name = "date_edit")
    private LocalDate dateEdit;

    public void generateHash() {
        this.hash = UUID.nameUUIDFromBytes((number.toString()
                + nn
                + status
                + series
                + number
                + dateEdit
        ).getBytes());
    }

}
