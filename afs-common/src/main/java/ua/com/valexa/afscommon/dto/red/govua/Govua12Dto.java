package ua.com.valexa.afscommon.dto.red.govua;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Govua12Dto {
    @JsonProperty("ID")
    private String id;
    @JsonProperty("OVD")
    private String ovd;
    @JsonProperty("CATEGORY")
    private String category;
    @JsonProperty("D_SERIES")
    private String series;
    @JsonProperty("D_NUMBER")
    private String number;
    @JsonProperty("D_TYPE")
    private String type;
    @JsonProperty("D_STATUS")
    private String status;
    @JsonProperty("THEFT_DATA")
    private String theftDate;
    @JsonProperty("INSERT_DATE")
    private String insertDate;
}
