package ua.com.valexa.importer.service.red_search;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua01;
import ua.com.valexa.importer.model.UploadPp;

import java.util.List;

@Service
public class UploadPpService {


    @Autowired
    EntityManager entityManager;

    public List<UploadPp> findByLocalPassport(String serial, String number) {
        String jpql = "SELECT r FROM UploadPp r " +
                " WHERE (r.localPassSerial = :serial OR r.localPassSerial = '') and r.localPassNum = :number"
                ;
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("serial", serial.toUpperCase().trim())
                .setParameter("number", number.toUpperCase().trim())
                .getResultList();
    }

    public List<UploadPp> findByIdPassport(String number) {
        String jpql = "SELECT r FROM UploadPp r " +
                " WHERE (r.idPassNumber = :number)"
                ;
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("number", number.toUpperCase().trim())
                .getResultList();
    }

    public List<UploadPp> findByNameUa(String lastName, String firstName, String patronymicName) {
        String jpql = "SELECT r FROM UploadPp r " +
                " WHERE (r.lastNameUa = :lastName AND r.firstNameUa = :firstName AND r.patronymicNameUa = :patronymicName)"
                ;
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("lastName", lastName.toUpperCase().trim())
                .setParameter("firstName", firstName.toUpperCase().trim())
                .setParameter("patronymicName", patronymicName.toUpperCase().trim())
                .getResultList();
    }

    public List<UploadPp> findByNameRu(String lastName, String firstName, String patronymicName) {
        String jpql = "SELECT r FROM UploadPp r " +
                " WHERE (r.lastNameRu = :lastName AND r.firstNameRu = :firstName AND r.patronymicNameRu = :patronymicName)"
                ;
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("lastName", lastName.toUpperCase().trim())
                .setParameter("firstName", firstName.toUpperCase().trim())
                .setParameter("patronymicName", patronymicName.toUpperCase().trim())
                .getResultList();
    }

    public List<UploadPp> findByNameEn(String lastName, String firstName, String patronymicName) {
        String jpql = "SELECT r FROM UploadPp r " +
                " WHERE (r.lastNameEn = :lastName AND r.firstNameEn = :firstName AND r.patronymicNameEn = :patronymicName)"
                ;
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("lastName", lastName.toUpperCase().trim())
                .setParameter("firstName", firstName.toUpperCase().trim())
                .setParameter("patronymicName", patronymicName.toUpperCase().trim())
                .getResultList();
    }

    public List<UploadPp> findByInnEqual(String inn) {
        String jpql = "SELECT r FROM UploadPp r " +
                "WHERE r.inn = :inn";
        return entityManager.createQuery(jpql, UploadPp.class)
                .setParameter("inn", inn.toUpperCase().trim())
                .getResultList();
    }




}
