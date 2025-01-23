package ua.com.valexa.afscommon.dto.red.govua;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Govua01Dto {
    private String number;
    private String date;
    private String type;
    private String firm_edrpou;
    private String firm_name;
    private String case_number;
    private String start_date_auc;
    private String end_date_auc;
    private String court_name;
    private String end_registration_date;
}
