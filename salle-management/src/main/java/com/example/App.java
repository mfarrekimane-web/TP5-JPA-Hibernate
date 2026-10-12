package com.example;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.repository.SalleRepositoryImpl;
import com.example.service.SalleService;
import com.example.service.SalleServiceImpl;
import com.example.util.PaginationResult;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {

    /* ===================== ÉTAPE 5 (JPQL) : mise en commentaire =====================

    public static List<Salle> sallesDisponibles(EntityManager em,
                                                LocalDateTime debut,
                                                LocalDateTime fin) {
        String jpql = "SELECT s FROM Salle s WHERE s.id NOT IN (" +
                "SELECT r.salle.id FROM Reservation r WHERE " +
                "(:debut < r.fin AND :fin > r.debut))";

        TypedQuery<Salle> query = em.createQuery(jpql, Salle.class);
        query.setParameter("debut", debut);
        query.setParameter("fin", fin);
        return query.getResultList();
    }

    ================================================================================ */

    /* ===================== ÉTAPE 6 (Criteria) : mise en commentaire =====================

    public static List<Salle> rechercherSalles(EntityManager em,
                                               String nomRecherche,
                                               Integer capaciteMin,
                                               LocalDateTime debutRecherche,
                                               LocalDateTime finRecherche) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Salle> cq = cb.createQuery(Salle.class);
        Root<Salle> salle = cq.from(Salle.class);
        List<Predicate> predicates = new ArrayList<>();

        if (nomRecherche != null && !nomRecherche.isEmpty()) {
            predicates.add(cb.like(cb.lower(salle.<String>get("nom")),
                    "%" + nomRecherche.toLowerCase() + "%"));
        }
        if (capaciteMin != null) {
            predicates.add(cb.ge(salle.<Integer>get("capacite"), capaciteMin));
        }
        if (debutRecherche != null && finRecherche != null) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<Reservation> reservation = subquery.from(Reservation.class);
            subquery.select(reservation.get("salle").<Long>get("id"));
            subquery.where(cb.and(
                    cb.lessThan(reservation.<LocalDateTime>get("debut"), finRecherche),
                    cb.greaterThan(reservation.<LocalDateTime>get("fin"), debutRecherche)));
            predicates.add(cb.not(salle.<Long>get("id").in(subquery)));
        }
        cq.select(salle).where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(cq).getResultList();
    }

    ================================================================================ */

    // ===================== ÉTAPE 7 (Pagination) : mise en commentaire =====================
    // Le code de pagination (PageResultat, construirePredicats, rechercherSallesPage)
    // est maintenant dans SalleRepositoryImpl : searchRooms() et countRooms().
    //
    // Principe conservé :
    //   query.setFirstResult((page - 1) * size);
    //   query.setMaxResults(size);
    //   totalPages = (int) Math.ceil((double) total / size);
    // ======================================================================================

    // ===================== ÉTAPE 8 (Repository) : mise en commentaire =====================
    //   SalleRepository repo = new SalleRepositoryImpl(em);
    //   afficher(repo.findAvailableRooms(debut, fin));
    //   System.out.println("Total : " + repo.countRooms("test", 30, null, null));
    //   afficher(repo.searchRooms("test", 30, null, null, 1, 3));
    //   afficher(repo.searchRooms("test", 30, null, null, 2, 3));
    // ======================================================================================

    // ===================== ÉTAPE 9 (Service) : mise en commentaire =====================
    //   SalleService service = new SalleServiceImpl(repo);
    //   afficher(service.getAvailableRooms(debut, fin));
    //   System.out.println("Total : " + service.countRooms("test", 30, null, null));
    //   afficher(service.searchRooms("test", 30, null, null, 1, 3));
    //   // Règles : créneau inversé, page 0, capacité négative -> IllegalArgumentException
    // ===================================================================================

    // ===================== ÉTAPE 10 (PaginationResult) : mise en commentaire =====================
    //   System.out.println("===== TOUTES LES SALLES : pages de 3 =====");
    //   for (int p = 1; p <= 4; p++) {
    //       afficherPage(service.searchRoomsPaginated(null, null, null, null, p, 3));
    //       System.out.println();
    //   }
    //   afficherPage(service.searchRoomsPaginated("test", 30, null, null, 1, 3));
    //   afficherPage(service.searchRoomsPaginated("test", 30, null, null, 2, 3));
    // =============================================================================================

    // ===================== ÉTAPE 11 : classe de test =====================

    private static Salle creerSalle(EntityManager em, String nom, int capacite) {
        Salle s = new Salle();
        s.setNom(nom);
        s.setCapacite(capacite);
        em.persist(s);
        return s;
    }

    private static void creerReservation(EntityManager em, Salle salle,
                                         LocalDateTime debut, LocalDateTime fin) {
        Reservation r = new Reservation();
        r.setSalle(salle);
        r.setDebut(debut);
        r.setFin(fin);
        em.persist(r);
    }

    private static void afficher(List<Salle> salles) {
        for (Salle s : salles) {
            System.out.println("- " + s.getNom() + " (Capacité: " + s.getCapacite() + ")");
        }
    }

    private static void afficherPage(PaginationResult<Salle> r) {
        System.out.println("Page " + r.getCurrentPage() + "/" + r.getTotalPages()
                + " | total " + r.getTotalItems()
                + " | précédente : " + r.hasPrevious()
                + " | suivante : " + r.hasNext());
        afficher(r.getItems());
    }

    public static void main(String[] args) {
        // 1. Initialisation JPA
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SallePU");
        EntityManager em = emf.createEntityManager();

        try {
            // 2. Données de test
            em.getTransaction().begin();
            Salle a = creerSalle(em, "Salle A", 20);
            Salle b = creerSalle(em, "Salle B", 30);
            creerSalle(em, "Salle C", 50);
            for (int i = 1; i <= 7; i++) {
                creerSalle(em, "Salle Test " + i, 10 * i);
            }
            creerReservation(em, a,
                    LocalDateTime.of(2026, 10, 12, 9, 0),
                    LocalDateTime.of(2026, 10, 12, 12, 0));
            creerReservation(em, b,
                    LocalDateTime.of(2026, 10, 12, 14, 0),
                    LocalDateTime.of(2026, 10, 12, 16, 0));
            em.getTransaction().commit();

            // 3. Montage des couches : App -> Service -> Repository -> EntityManager
            SalleRepository repo = new SalleRepositoryImpl(em);
            SalleService service = new SalleServiceImpl(repo);

            LocalDateTime debut = LocalDateTime.of(2026, 10, 12, 10, 0);
            LocalDateTime fin = LocalDateTime.of(2026, 10, 12, 11, 0);

            // 4. Test : disponibilité (JPQL)
            System.out.println("===== TEST 1 : salles disponibles entre " + debut + " et " + fin + " =====");
            afficher(service.getAvailableRooms(debut, fin));

            // 5. Test : recherche multi-critères (Criteria) + pagination
            System.out.println("\n===== TEST 2 : nom 'test', capacité >= 30, page 1 (taille 3) =====");
            afficherPage(service.searchRoomsPaginated("test", 30, null, null, 1, 3));

            // 6. Test : tous les critères combinés
            System.out.println("\n===== TEST 3 : capacité >= 40 ET disponible 10h-11h =====");
            afficherPage(service.searchRoomsPaginated(null, 40, debut, fin, 1, 5));

            // 7. Test : règle métier (créneau inversé)
            System.out.println("\n===== TEST 4 : règle métier, créneau inversé =====");
            try {
                service.getAvailableRooms(fin, debut);
            } catch (IllegalArgumentException e) {
                System.out.println("Refusé : " + e.getMessage());
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            // 8. Libération des ressources
            em.close();
            emf.close();
        }
    }
}