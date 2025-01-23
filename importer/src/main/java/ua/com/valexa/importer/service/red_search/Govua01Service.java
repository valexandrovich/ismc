package ua.com.valexa.importer.service.red_search;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua01;
import ua.com.valexa.importer.repository.Govua01Repository;

import java.util.List;


@Service
public class Govua01Service {

    @Autowired
    Govua01Repository govua01Repository;

    @Autowired
    EntityManager entityManager;

//    public List<Govua01> findByCodeLike(String code) {
//        String jpql = "SELECT r FROM Govua01 r " +
//                "WHERE UPPER(r.firmEdrpou) LIKE :firmEdrpou";
//        return entityManager.createQuery(jpql, Govua01.class)
//                .setParameter("firmEdrpou", '%' + code + '%')
//                .getResultList();
//    }

    public List<Govua01> findByCodeEqual(String code) {
        String jpql = "SELECT r FROM Govua01 r " +
                "WHERE r.firmEdrpou = :firmEdrpou";
        return entityManager.createQuery(jpql, Govua01.class)
                .setParameter("firmEdrpou", code)
                .getResultList();
    }

//    public List<Govua01> findByNameLike(String name) {
//        String jpql = "SELECT r FROM Govua01 r " +
//                "WHERE UPPER(r.firmName) LIKE :firmName";
//        return entityManager.createQuery(jpql, Govua01.class)
//                .setParameter("firmName", '%' + name.toUpperCase().trim() + '%')
//                .getResultList();
//    }

    public List<Govua01> findByNameEqual(String name) {
        String jpql = "SELECT r FROM Govua01 r " +
                "WHERE r.firmName = :firmName";
        return entityManager.createQuery(jpql, Govua01.class)
                .setParameter("firmName", name.toUpperCase().trim())
                .getResultList();
    }




}
