package ua.com.valexa.importer.service.red_search;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua06;
import ua.com.valexa.importer.model.Govua07;
import ua.com.valexa.importer.repository.Govua06Repository;
import ua.com.valexa.importer.repository.Govua07Repository;

import java.util.List;

@Service
public class Govua06Service {

//    @Autowired
//    Govua06Repository govua06Repository;

    @Autowired
    EntityManager entityManager;

//    public List<Govua06> findByCodeLike(String code) {
//        String jpql = "SELECT r FROM Govua06 r " +
//                "WHERE UPPER(r.debtorCode) LIKE :code";
//        return entityManager.createQuery(jpql, Govua06.class)
//                .setParameter("code", '%' + code + '%')
//                .getResultList();
//    }

    public List<Govua06> findByCodeEqual(String code) {
        String jpql = "SELECT r FROM Govua06 r " +
                "WHERE r.debtorCode = :code";
        return entityManager.createQuery(jpql, Govua06.class)
                .setParameter("code", code)
                .getResultList();
    }

//    public List<Govua06> findByNameLike(String name) {
//        String jpql = "SELECT r FROM Govua06 r " +
//                "WHERE UPPER(r.debtorName) LIKE :name";
//        return entityManager.createQuery(jpql, Govua06.class)
//                .setParameter("name", '%' + name.toUpperCase().trim() + '%')
//                .getResultList();
//    }

    public List<Govua06> findByNameEqual(String name) {
        String jpql = "SELECT r FROM Govua06 r " +
                "WHERE r.debtorName = :name";
        return entityManager.createQuery(jpql, Govua06.class)
                .setParameter("name",  name.toUpperCase().trim())
                .getResultList();
    }




}