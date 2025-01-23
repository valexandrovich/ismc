package ua.com.valexa.enricher.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(schema="data", name = "tag_type")
@Getter
@Setter
@ToString
public class TagType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level_1_code;
    private String level_2_code;
    private String level_3_code;
    private String level_4_code;
    private String level_5_code;

    private String code;

    private String level_1_description;
    private String level_2_description;
    private String level_3_description;
    private String level_4_description;
    private String level_5_description;

    private String description;

}
