package ua.com.valexa.importer.service.red_search;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua07;
import ua.com.valexa.importer.repository.Govua07Repository;

import java.util.List;

@Service
public class Govua07Service {

    @Autowired
    Govua07Repository govua07Repository;

    @Autowired
    EntityManager entityManager;

//    public List<Govua07> findByCodeLike(String code) {
//        String jpql = "SELECT r FROM Govua07 r " +
//                "WHERE UPPER(r.debtorCode) LIKE :code";
//        return entityManager.createQuery(jpql, Govua07.class)
//                .setParameter("code", '%' + code + '%')
//                .getResultList();
//    }

    public List<Govua07> findByCodeEqual(String code) {
        String jpql = "SELECT r FROM Govua07 r " +
                "WHERE r.debtorCode = :code";
        return entityManager.createQuery(jpql, Govua07.class)
                .setParameter("code", code)
                .getResultList();
    }

//    public List<Govua07> findByNameLike(String name) {
//        String jpql = "SELECT r FROM Govua07 r " +
//                "WHERE UPPER(r.debtorName) LIKE :name";
//        return entityManager.createQuery(jpql, Govua07.class)
//                .setParameter("name", '%' + name.toUpperCase().trim() + '%')
//                .getResultList();
//    }

    public List<Govua07> findByNameEqual(String name) {
        String jpql = "SELECT r FROM Govua07 r " +
                "WHERE r.debtorName = :name";
        return entityManager.createQuery(jpql, Govua07.class)
                .setParameter("name", name.toUpperCase().trim())
                .getResultList();
    }


}