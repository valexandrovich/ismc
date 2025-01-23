package ua.com.valexa.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.red.govua.LegalEntitySearchDto;
import ua.com.valexa.afscommon.dto.red.govua.PersonSearchDto;
import ua.com.valexa.importer.model.SearchRedLeResponse;
import ua.com.valexa.importer.model.SearchRedPpResponse;
import ua.com.valexa.importer.service.red_search.*;

@Service
public class SearchRedService {

    @Autowired
    Govua01Service govua01Service;

    @Autowired
    Govua06Service govua06Service;

    @Autowired
    Govua07Service govua07Service;

    @Autowired
    Govua08Service govua08Service;

    @Autowired
    Govua09Service govua09Service;

    @Autowired
    Govua10Service govua10Service;

    @Autowired
    Govua11Service govua11Service;

    @Autowired
    Govua12Service govua12Service;

    @Autowired
    Govua13Service govua13Service;

    @Autowired
    UploadPpService uploadPpService;

    @Autowired
    UploadLeService uploadLeService;


    public SearchRedPpResponse searchPp(PersonSearchDto dto) {

        SearchRedPpResponse result = new SearchRedPpResponse();
        result.setPersonSearchDto(dto);

        if (dto.getInn() != null && !dto.getInn().isBlank()) {
            result.getSearchResults().getGovua01List().addAll(govua01Service.findByCodeEqual(dto.getInn()));
            result.getSearchResults().getGovua06List().addAll(govua06Service.findByCodeEqual(dto.getInn()));
            result.getSearchResults().getGovua07List().addAll(govua07Service.findByCodeEqual(dto.getInn()));

            result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByInnEqual(dto.getInn()));
        }

        if (dto.getParams().isSimpleName() && dto.getSimpleName() != null && !dto.getSimpleName().isBlank()) {
            String sName = dto.getSimpleName();
            if (sName.split(" ").length == 3) {
                String lastName = sName.split(" ")[0];
                String firstName = sName.split(" ")[1];
                String patronymicName = sName.split(" ")[2];
                result.getSearchResults().getGovua08List().addAll(govua08Service.findByNameEqual(lastName, firstName, patronymicName));
                result.getSearchResults().getGovua09List().addAll(govua09Service.findByNameEquals(lastName, firstName, patronymicName));

                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameUa(lastName, firstName, patronymicName));
                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameRu(lastName, firstName, patronymicName));
                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameEn(lastName, firstName, patronymicName));

            }
            result.getSearchResults().getGovua01List().addAll(govua01Service.findByNameEqual(sName));
            result.getSearchResults().getGovua06List().addAll(govua06Service.findByNameEqual(sName));
            result.getSearchResults().getGovua07List().addAll(govua07Service.findByNameEqual(sName));

        } else {
            if (
                    (dto.getLastName() != null && !dto.getLastName().isEmpty()) ||
                            (dto.getFirstName() != null && !dto.getFirstName().isEmpty()) ||
                            (dto.getPatronymicName() != null && !dto.getPatronymicName().isEmpty())

            ) {
                result.getSearchResults().getGovua08List().addAll(govua08Service.findByNameEqual(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));
                result.getSearchResults().getGovua09List().addAll(govua09Service.findByNameEquals(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));

                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameUa(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));
                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameRu(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));
                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByNameEn(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));


                String sName = dto.getLastName() + " " + dto.getFirstName() + " " + dto.getPatronymicName();
                result.getSearchResults().getGovua01List().addAll(govua01Service.findByNameEqual(sName));
                result.getSearchResults().getGovua06List().addAll(govua06Service.findByNameEqual(sName));
                result.getSearchResults().getGovua07List().addAll(govua07Service.findByNameEqual(sName));
            }

        }

        if (dto.getParams().getPassportType().equals("1")) {

            if (
                    dto.getLocalPassportSerial() != null &&
                            !dto.getLocalPassportSerial().isEmpty() &&
                            dto.getLocalPassportNumber() != null &&
                            !dto.getLocalPassportNumber().isEmpty()
            ) {
                result.getSearchResults().getGovua10List().addAll(govua10Service.findByPassportNumber(dto.getLocalPassportSerial(), dto.getLocalPassportNumber()));
                result.getSearchResults().getGovua12List().addAll(govua12Service.findByPassportNumber(dto.getLocalPassportSerial(), dto.getLocalPassportNumber()));

                result.getSearchResults().getUploadPpList().addAll(uploadPpService.findByLocalPassport(dto.getLocalPassportSerial(), dto.getLocalPassportNumber()));


            }

        }

        if (dto.getParams().getPassportType().equals("3")) {

            if (
                    dto.getIntPassportSerial() != null &&
                            !dto.getIntPassportSerial().isEmpty() &&
                            dto.getIntPassportNumber() != null &&
                            !dto.getIntPassportNumber().isEmpty()
            ) {
                result.getSearchResults().getGovua11List().addAll(govua11Service.findByPassportNumber(dto.getIntPassportSerial(), dto.getIntPassportNumber()));
                result.getSearchResults().getGovua13List().addAll(govua13Service.findByPassportNumber(dto.getIntPassportSerial(), dto.getIntPassportNumber()));

            }

        }

        //TODO Search for ID cards
//
//
//        if (dto.getInn() != null && !dto.getInn().isBlank()) {
//            result.getSearchResults().getGovua01List().addAll(govua01Service.findByCodeLike(dto.getInn()));
//
//            result.getSearchResults().getGovua06List().addAll(govua06Service.findByCodeLike(dto.getInn()));
//
//            result.getSearchResults().getGovua07List().addAll(govua07Service.findByCodeLike(dto.getInn()));
//
//        }
//
//        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
//            StringBuilder fName = new StringBuilder();
//            fName.append(dto.getLastName() == null ? "" : dto.getLastName());
//            fName.append(dto.getFirstName() == null ? "" : " " + dto.getFirstName());
//            fName.append(dto.getPatronymicName() == null ? "" : " " + dto.getPatronymicName());
//
//            result.getSearchResults().getGovua01List().addAll(govua01Service.findByNameLike(fName.toString()));
//
//            result.getSearchResults().getGovua06List().addAll(govua06Service.findByNameLike(fName.toString()));
//
//            result.getSearchResults().getGovua07List().addAll(govua07Service.findByNameLike(fName.toString()));
//
//            result.getSearchResults().getGovua08List().addAll(govua08Service.findByNameLike(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));
//            result.getSearchResults().getGovua09List().addAll(govua09Service.findByNameLike(dto.getLastName(), dto.getFirstName(), dto.getPatronymicName()));
//        }
//
//        if (dto.getLocalPassportNumber() != null && !dto.getLocalPassportNumber().isBlank()) {
//            result.getSearchResults().getGovua10List().addAll(govua10Service.findByPassportNumber(dto.getLocalPassportSerial(), dto.getLocalPassportNumber()));
//            result.getSearchResults().getGovua12List().addAll(govua12Service.findByPassportNumber(dto.getLocalPassportSerial(), dto.getLocalPassportNumber()));
//
//        }
//
//        if (dto.getIdPassportNumber() != null && !dto.getIdPassportNumber().isBlank()) {
//            result.getSearchResults().getGovua10List().addAll(govua10Service.findByPassportNumber("", dto.getIdPassportNumber()));
//            result.getSearchResults().getGovua12List().addAll(govua12Service.findByPassportNumber("", dto.getIdPassportNumber()));
//        }
//
//        if (dto.getIntPassportNumber() != null && !dto.getIntPassportNumber().isBlank()) {
//            result.getSearchResults().getGovua11List().addAll(govua11Service.findByPassportNumber(dto.getIntPassportSerial(), dto.getIntPassportNumber()));
//            result.getSearchResults().getGovua13List().addAll(govua13Service.findByPassportNumber(dto.getIntPassportSerial(), dto.getIntPassportNumber()));
//        }


        return result;


    }


    public SearchRedLeResponse searchLe(LegalEntitySearchDto dto) {
        SearchRedLeResponse result = new SearchRedLeResponse();
        result.setLegalEntitySearchDto(dto);

        if (dto.getEdrpou() != null && !dto.getEdrpou().isBlank()) {
            result.getSearchResults().getGovua01List().addAll(govua01Service.findByCodeEqual(dto.getEdrpou()));
            result.getSearchResults().getGovua06List().addAll(govua06Service.findByCodeEqual(dto.getEdrpou()));
            result.getSearchResults().getGovua07List().addAll(govua07Service.findByCodeEqual(dto.getEdrpou()));

            result.getSearchResults().getUploadLeList().addAll(uploadLeService.findByEdrpouEqual(dto.getEdrpou()));
        }

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            result.getSearchResults().getGovua01List().addAll(govua01Service.findByNameEqual(dto.getName()));
            result.getSearchResults().getGovua06List().addAll(govua06Service.findByNameEqual(dto.getName()));
            result.getSearchResults().getGovua07List().addAll(govua07Service.findByNameEqual(dto.getName()));

            result.getSearchResults().getUploadLeList().addAll(uploadLeService.findByName(dto.getName()));

        }

        return result;
    }


}
