package ua.com.valexa.importer.service.red_search;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.importer.model.Govua09;
import ua.com.valexa.importer.repository.Govua09Repository;

import java.util.List;

@Service
public class Govua09Service {

    @Autowired
    Govua09Repository govua09Repository;

    @Autowired
    EntityManager entityManager;

//    public List<Govua09> findByNameLike(String lastName, String firstName, String patronymicName) {
//        String jpql = "SELECT r FROM Govua09 r " +
//                " WHERE (UPPER(r.lastNameUa) = :lastName OR UPPER(r.lastNameEn) = :lastName OR UPPER(r.lastNameRu) = :lastName ) " +
//                " AND (UPPER(r.firstNameUa) = :firstName OR UPPER(r.firstNameEn) = :firstName  OR UPPER(r.firstNameRu) = :firstName ) " +
//                " AND (UPPER(r.patronymicNameUa) = :patronymicName OR UPPER(r.patronymicNameEn) = :patronymicName OR UPPER(r.patronymicNameRu) = :patronymicName ) "
//                ;
//        return entityManager.createQuery(jpql, Govua09.class)
//                .setParameter("lastName", lastName.toUpperCase())
//                .setParameter("firstName", firstName.toUpperCase())
//                .setParameter("patronymicName", patronymicName.toUpperCase())
//                .getResultList();
//    }

//    public List<Govua09> findByNameLike(String lastName, String firstName, String patronymicName) {
//        StringBuilder jpql = new StringBuilder("SELECT r FROM Govua09 r WHERE 1=1");
//
//        if (lastName != null && !lastName.trim().isEmpty()) {
//            jpql.append(" AND (UPPER(r.lastNameUa) LIKE :lastName OR UPPER(r.lastNameEn) LIKE :lastName OR UPPER(r.lastNameRu) LIKE :lastName)");
//        }
//        if (firstName != null && !firstName.trim().isEmpty()) {
//            jpql.append(" AND (UPPER(r.firstNameUa) LIKE :firstName OR UPPER(r.firstNameEn) LIKE :firstName OR UPPER(r.firstNameRu) LIKE :firstName)");
//        }
//        if (patronymicName != null && !patronymicName.trim().isEmpty()) {
//            jpql.append(" AND (UPPER(r.patronymicNameUa) LIKE :patronymicName OR UPPER(r.patronymicNameEn) LIKE :patronymicName OR UPPER(r.patronymicNameRu) LIKE :patronymicName)");
//        }
//
//        TypedQuery<Govua09> query = entityManager.createQuery(jpql.toString(), Govua09.class);
//
//        if (lastName != null && !lastName.trim().isEmpty()) {
//            query.setParameter("lastName", "%" + lastName.toUpperCase() + "%");
//        }
//        if (firstName != null && !firstName.trim().isEmpty()) {
//            query.setParameter("firstName", "%" + firstName.toUpperCase() + "%");
//        }
//        if (patronymicName != null && !patronymicName.trim().isEmpty()) {
//            query.setParameter("patronymicName", "%" + patronymicName.toUpperCase() + "%");
//        }
//
//        return query.getResultList();
//    }

    public List<Govua09> findByNameEquals(String lastName, String firstName, String patronymicName) {
        StringBuilder jpql = new StringBuilder("SELECT r FROM Govua09 r WHERE 1=1");

        if (lastName != null && !lastName.trim().isEmpty()) {
            jpql.append(" AND (r.lastNameUa = :lastName OR r.lastNameEn = :lastName OR r.lastNameRu = :lastName)");
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            jpql.append(" AND (r.firstNameUa = :firstName OR r.firstNameEn LIKE :firstName OR r.firstNameRu = :firstName)");
        }
        if (patronymicName != null && !patronymicName.trim().isEmpty()) {
            jpql.append(" AND (r.patronymicNameUa = :patronymicName OR r.patronymicNameEn = :patronymicName OR r.patronymicNameRu = :patronymicName)");
        }

        TypedQuery<Govua09> query = entityManager.createQuery(jpql.toString(), Govua09.class);

        if (lastName != null && !lastName.trim().isEmpty()) {
            query.setParameter("lastName", lastName.toUpperCase());
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            query.setParameter("firstName", firstName.toUpperCase() );
        }
        if (patronymicName != null && !patronymicName.trim().isEmpty()) {
            query.setParameter("patronymicName",  patronymicName.toUpperCase());
        }

        return query.getResultList();
    }




}
