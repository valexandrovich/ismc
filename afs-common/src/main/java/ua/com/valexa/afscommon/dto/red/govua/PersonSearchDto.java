package ua.com.valexa.afscommon.dto.red.govua;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PersonSearchDto {
    private Params params = new Params();

    private String simpleName;
    private String lastName;
    private String firstName;
    private String patronymicName;
    private LocalDate birthday;
    private String inn;

    private String localPassportSerial;
    private String localPassportNumber;

    private String intPassportSerial;
    private String intPassportNumber;

    private String idPassportRecordNumber;
    private String idPassportNumber;

    private String address;
    private String phone;

    @Override
    public String toString() {
        return "PersonSearchDto{" +
                "params=" + params +
                ", simpleName='" + simpleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", birthday=" + birthday +
                ", inn='" + inn + '\'' +
                ", localPassportSerial='" + localPassportSerial + '\'' +
                ", localPassportNumber='" + localPassportNumber + '\'' +
                ", intPassportSerial='" + intPassportSerial + '\'' +
                ", intPassportNumber='" + intPassportNumber + '\'' +
                ", idPassportRecordNumber='" + idPassportRecordNumber + '\'' +
                ", idPassportNumber='" + idPassportNumber + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Getter
    @Setter
    public static class Params {
        private String passportType;
        @JsonProperty("isSimpleName")
        private boolean isSimpleName;

        @Override
        public String toString() {
            return "Params{" +
                    "passportType='" + passportType + '\'' +
                    ", isSimpleName=" + isSimpleName +
                    '}';
        }
    }
}