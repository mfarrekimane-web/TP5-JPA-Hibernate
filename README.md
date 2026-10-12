# TP5 : Gestion des salles avec JPA / Hibernate

## Description
Application Java SE (JPA, Hibernate 5.4, H2 en mémoire, Maven) pour rechercher des salles
selon plusieurs critères : disponibilité sur un créneau, nom, capacité minimale, avec pagination.

## Architecture en couches
App -> SalleService -> SalleRepository -> EntityManager -> H2

| Package | Contenu |
|---|---|
| `com.example.model` | Entités `Salle`, `Reservation` |
| `com.example.repository` | `SalleRepository`, `SalleRepositoryImpl` (JPQL et Criteria) |
| `com.example.service` | `SalleService`, `SalleServiceImpl` (règles métier) |
| `com.example.util` | `PaginationResult` (résultats paginés) |

## Structure du dépôt
- [salle-management](./salle-management) : code source du projet Maven
- [lab5](./lab5) : captures d'écran de l'exécution

## Étapes réalisées
- Étape 5 : requête JPQL des salles disponibles sur un créneau
- Étape 6 : recherche multi-critères dynamique (API Criteria)
- Étape 7 : pagination (`setFirstResult`, `setMaxResults`, count)
- Étape 8 : Repository JPA
- Étape 9 : service métier avec validations
- Étape 10 : classe générique `PaginationResult`
- Étape 11 : classe principale de test

## Exécution
Lancer la classe `com.example.App`.

## Preuves d'exécution

### Capture 1
![Capture 1](./lab5/IM1.png)

### Capture 2
![Capture 2](./lab5/IM2.png)

### Capture 3
![Capture 3](./lab5/IM3.png)

### Capture 4
![Capture 4](./lab5/IM4.png)

### Capture 5
![Capture 5](./lab5/IM5.png)

### Capture 6
![Capture 6](./lab5/IM6.png)

### Capture 7
![Capture 7](./lab5/IM7.png)

### Capture 8
![Capture 8](./lab5/IM8.png)

### Capture 9
![Capture 9](./lab5/IM9.png)

### Capture 10
![Capture 10](./lab5/IM10.png)

### Capture 11
![Capture 11](./lab5/IM11.png)

### Capture 12
![Capture 12](./lab5/IM12.png)

### Capture 13
![Capture 13](./lab5/IM13.png)

### Capture 14
![Capture 14](./lab5/IM14.png)

### Capture 15
![Capture 15](./lab5/IM15.png)

### Capture 16
![Capture 16](./lab5/IM16.png)

### Capture 17
![Capture 17](./lab5/IM17.png)

### Capture 18
![Capture 18](./lab5/IM18.png)

### Capture 19
![Capture 19](./lab5/IM19.png)

### Capture 20
![Capture 20](./lab5/IM20.png)

### Capture 21
![Capture 21](./lab5/IM21.png)

### Capture 22
![Capture 22](./lab5/IM22.png)

### Capture 23
![Capture 23](./lab5/IM23.png)

### Capture 24
![Capture 24](./lab5/IM24.png)

### Capture 25
![Capture 25](./lab5/IM25.png)

### Capture 26
![Capture 26](./lab5/IM26.png)
