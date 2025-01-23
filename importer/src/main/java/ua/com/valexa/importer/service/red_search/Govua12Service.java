package ua.com.valexa.importer.service.red_search;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua12;

import java.util.List;

@Service
public class Govua12Service {


    @Autowired
    EntityManager entityManager;

    public List<Govua12> findByPassportNumber(String serial, String number) {
        String jpql = "SELECT r FROM Govua12 r " +
                " WHERE (r.series = :serial OR r.series = '') and r.number = :number"
                ;
        return entityManager.createQuery(jpql, Govua12.class)
                .setParameter("serial", serial)
                .setParameter("number", number)
                .getResultList();
    }




}
