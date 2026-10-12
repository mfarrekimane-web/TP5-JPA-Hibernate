package com.example.service;

import com.example.model.Salle;
import com.example.repository.SalleRepository;
import com.example.util.PaginationResult;

import java.time.LocalDateTime;
import java.util.List;

public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;

    // Injection par constructeur (pas de conteneur EJB/CDI en Java SE)
    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Override
    public List<Salle> getAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Le début et la fin du créneau sont obligatoires");
        }
        verifierCreneau(debut, fin);
        return salleRepository.findAvailableRooms(debut, fin);
    }

    @Override
    public List<Salle> searchRooms(String nom, Integer capaciteMin,
                                   LocalDateTime debut, LocalDateTime fin,
                                   int page, int size) {
        verifierCriteres(capaciteMin, debut, fin);
        if (page < 1) {
            throw new IllegalArgumentException("La page doit être >= 1");
        }
        if (size < 1) {
            throw new IllegalArgumentException("La taille de page doit être >= 1");
        }
        return salleRepository.searchRooms(nom, capaciteMin, debut, fin, page, size);
    }

    @Override
    public long countRooms(String nom, Integer capaciteMin,
                           LocalDateTime debut, LocalDateTime fin) {
        verifierCriteres(capaciteMin, debut, fin);
        return salleRepository.countRooms(nom, capaciteMin, debut, fin);
    }

    // ---------- Règles métier ----------

    private void verifierCriteres(Integer capaciteMin, LocalDateTime debut, LocalDateTime fin) {
        if (capaciteMin != null && capaciteMin < 0) {
            throw new IllegalArgumentException("La capacité minimale ne peut pas être négative");
        }
        if ((debut == null) != (fin == null)) {
            throw new IllegalArgumentException("Le début et la fin doivent être fournis ensemble");
        }
        if (debut != null) {
            verifierCreneau(debut, fin);
        }
    }

    private void verifierCreneau(LocalDateTime debut, LocalDateTime fin) {
        if (!debut.isBefore(fin)) {
            throw new IllegalArgumentException("Le début doit être avant la fin");
        }
    }
    @Override
    public PaginationResult<Salle> searchRoomsPaginated(String nom, Integer capaciteMin,
                                                        LocalDateTime debut, LocalDateTime fin,
                                                        int page, int size) {
        // searchRooms et countRooms valident déjà les paramètres
        List<Salle> items = searchRooms(nom, capaciteMin, debut, fin, page, size);
        long total = countRooms(nom, capaciteMin, debut, fin);
        return new PaginationResult<>(items, page, size, total);
    }
}