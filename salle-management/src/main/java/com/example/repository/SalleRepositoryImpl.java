package com.example.repository;

import com.example.model.Reservation;
import com.example.model.Salle;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalleRepositoryImpl implements SalleRepository {

    private final EntityManager entityManager;

    public SalleRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /** JPQL : salles sans réservation chevauchant [debut, fin]. */
    @Override
    public List<Salle> findAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        String jpql = "SELECT s FROM Salle s WHERE s.id NOT IN (" +
                "SELECT r.salle.id FROM Reservation r WHERE (:debut < r.fin AND :fin > r.debut))";
        return entityManager.createQuery(jpql, Salle.class)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getResultList();
    }

    /** Criteria : recherche multi-critères paginée (page à partir de 1). */
    @Override
    public List<Salle> searchRooms(String nom, Integer capaciteMin,
                                   LocalDateTime debut, LocalDateTime fin,
                                   int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Salle> cq = cb.createQuery(Salle.class);
        Root<Salle> salle = cq.from(Salle.class);

        cq.select(salle)
                .where(cb.and(construirePredicats(cb, cq, salle, nom, capaciteMin, debut, fin)
                        .toArray(new Predicate[0])))
                .orderBy(cb.asc(salle.get("id")));

        TypedQuery<Salle> query = entityManager.createQuery(cq);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    /** Criteria : nombre total de salles correspondant aux critères. */
    @Override
    public long countRooms(String nom, Integer capaciteMin,
                           LocalDateTime debut, LocalDateTime fin) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Salle> salle = cq.from(Salle.class);

        cq.select(cb.count(salle))
                .where(cb.and(construirePredicats(cb, cq, salle, nom, capaciteMin, debut, fin)
                        .toArray(new Predicate[0])));

        return entityManager.createQuery(cq).getSingleResult();
    }

    /** Filtres communs à searchRooms et countRooms. */
    private List<Predicate> construirePredicats(CriteriaBuilder cb, CriteriaQuery<?> cq,
                                                Root<Salle> salle, String nom,
                                                Integer capaciteMin,
                                                LocalDateTime debut, LocalDateTime fin) {
        List<Predicate> predicates = new ArrayList<>();

        if (nom != null && !nom.isEmpty()) {
            predicates.add(cb.like(cb.lower(salle.<String>get("nom")),
                    "%" + nom.toLowerCase() + "%"));
        }
        if (capaciteMin != null) {
            predicates.add(cb.ge(salle.<Integer>get("capacite"), capaciteMin));
        }
        if (debut != null && fin != null) {
            Subquery<Long> sub = cq.subquery(Long.class);
            Root<Reservation> reservation = sub.from(Reservation.class);
            sub.select(reservation.get("salle").<Long>get("id"));
            sub.where(cb.and(
                    cb.lessThan(reservation.<LocalDateTime>get("debut"), fin),
                    cb.greaterThan(reservation.<LocalDateTime>get("fin"), debut)));
            predicates.add(cb.not(salle.<Long>get("id").in(sub)));
        }
        return predicates;
    }
}