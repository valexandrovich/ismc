package ua.com.valexa.importer.service.red_search;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.UploadLe;
import ua.com.valexa.importer.model.UploadPp;

import java.util.List;

@Service
public class UploadLeService {


    @Autowired
    EntityManager entityManager;





    public List<UploadLe> findByName(String name) {
        String jpql = "SELECT r FROM UploadLe r " +
                " WHERE (r.shortName = :name OR r.fullName = :name)"
                ;
        return entityManager.createQuery(jpql, UploadLe.class)
                .setParameter("name", name.toUpperCase().trim())
                .getResultList();
    }



    public List<UploadLe> findByEdrpouEqual(String edrpou) {
        String jpql = "SELECT r FROM UploadLe r " +
                "WHERE r.edrpou = :edrpou";
        return entityManager.createQuery(jpql, UploadLe.class)
                .setParameter("edrpou", edrpou.toUpperCase().trim())
                .getResultList();
    }




}
