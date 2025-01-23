package ua.com.valexa.afscommon.dto.red.govua;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Govua06Dto {
    public String debtor_name;
    public String debtor_birthdate;
    public String debtor_code;
    public String creditor_name;
    public String creditor_code;
    public String vp_ordernum;
    public String vp_begindate;
    public String vp_state;
    public String org_name;
    public String dvs_code;
    public String phone_num;
    public String email_addr;
    public String bank_account;

}
