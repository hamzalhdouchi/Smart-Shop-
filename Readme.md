# SmartShop – Backend REST API

SmartShop est une application web de gestion commerciale pour MicroTech Maroc (B2B – matériel informatique). Le backend est développé en Java Spring Boot, exposant des API REST sécurisées.

---

## Sommaire

- Présentation
- Fonctionnalités principales
- Architecture technique
- Installation & Lancement
- Modèle de données
- Instructions API & Tests
- Livraison & Évaluation
- Technologies utilisées
- Contributeurs
- Structure des dossiers

---

## Présentation

SmartShop vise à digitaliser la gestion commerciale de MicroTech Maroc : gestion des clients, commandes multi-produits, système de fidélité automatisé, paiements multi-moyens, et traçabilité financière complète. Il s'adresse à un environnement B2B sans interface graphique, uniquement des endpoints REST testés via Postman ou Swagger[attached_file:1].

---

## Fonctionnalités principales

- Gestion CRUD des clients et produits
- Historique complet des commandes & statistiques
- Système de fidélité automatique (BASIC, SILVER, GOLD, PLATINUM)
- Gestion et suivi commandes multi-produits
- Application automatique de la TVA et des remises
- Paiements fractionnés multi-moyens (espèces, chèque, virement)
- Statuts riches pour commandes et paiements
- Gestion centralisée d'erreurs et réponses JSON cohérentes

---

## Architecture Technique

- Backend uniquement : API REST (JSON)
- Java 8+, Spring Boot, Spring Data JPA
- PostgreSQL/MySQL – ORM Hibernate
- Organisation en couches : Controller, Service, Repository, Entity, DTO, Mapper
- Tests : JUnit, Mockito
- Authentification par HTTP Session (pas de JWT/Spring Security)
- Mapping : Lombok, Builder Pattern, MapStruct

---

## Installation & Lancement

1. **Pré-requis :**
    - JDK 8+
    - Maven ou Gradle
    - PostgreSQL ou MySQL

2. **Clonage du dépôt :**

- git clone https://github.com/votre-utilisateur/smartshop-backend.git
- cd smartshop-backend

3. **Configuration base de données :**
   Modifiez `application.properties` ou `application.yml` pour vos accès DB.

4. **Initialisation de la base :**  
   Ajoutez au moins 5 enregistrements dans chaque table (Client, Produit…).

5. **Lancement du projet :**

*mvn spring-boot:run*


---

## Modèle de données

Les entités clés du projet sont :
- Utilisateur (ADMIN, CLIENT)
- Client (niveau de fidélité)
- Produit (stock disponible)
- Commande (statut, multi-produits, total calculé)
- Paiement (multi-moyens, statuts, dates d'encaissement)
- OrderItem (détail d’articles par commande)[attached_file:1]

---

## Instructions API & Tests

- Accédez à la documentation Swagger sur [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (si activée), sinon, utilisez la collection Postman fournie.
- Lancez les différentes étapes :
- Authentification via /login
- Opérations CRUD Clients, Produits, Commandes
- Gestion et vérification des paiements
- Validation et suivi commandes[attached_file:1]

---

## Livraison & Évaluation

Les livrables attendus :
- Code source sur GitHub : inclure le lien.
- UML (diagramme de classes – image)
- Projet Jira découpé en epics/user stories/tâches (exemple fourni)
- README clair et documenté
- Documentation API (Swagger/Postman)[attached_file:1]

---

## Technologies utilisées

- Java 8+ / Spring Boot / Spring Data JPA
- PostgreSQL ou MySQL
- Maven ou Gradle
- Lombok, MapStruct
- JUnit, Mockito
- Swagger ou Postman
- GitHub

---

## Auteur & Contributeurs

Projet réalisé par [Votre Nom/Équipe] dans le cadre d’une évaluation technique chez MicroTech Maroc.

---

## Remarques

- L’application démarre sans erreur
- Les validations, remises, TVA et stock sont gérés correctement
- Les erreurs sont envoyées sous format JSON standardisé
- L’architecture (Controller-Service-Repository-DTO-Mapper) est claire et propre


## Architecture du projet

### Structure des dossiers

```
smartshop-backend/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── microtech/
│ │ │ └── smartshop/
│ │ │ ├── config/ # Configuration Spring
│ │ │ ├── controller/ # API REST Endpoints
│ │ │ ├── service/ # Logique métier
│ │ │ │ └── impl/ # Implémentations des services
│ │ │ ├── repository/ # Accès données (JPA)
│ │ │ ├── entity/ # Entités JPA
│ │ │ ├── dto/ # Data Transfer Objects
│ │ │ ├── mapper/ # MapStruct mappers
│ │ │ ├── enums/ # Énumérations (OrderStatus, PaymentStatus...)
│ │ │ ├── exception/ # Exceptions personnalisées
│ │ │ └── util/ # Classes utilitaires
│ │ └── resources/
│ │ ├── application.properties # Configuration application
│ │ └── data.sql # Données initiales (optionnel)
│ └── test/
│ └── java/ # Tests unitaires et d'intégration
├── pom.xml # Dépendances Maven
├── README.md
└── UML-diagram.png # Diagramme de classes
```


### Architecture en couches

Le projet suit une architecture en couches pour assurer une séparation claire des responsabilités :

#### 1. Couche Controller (Présentation)
- Expose les endpoints REST
- Gère les requêtes HTTP (GET, POST, PUT, DELETE)
- Valide les données entrantes avec `@Valid`
- Retourne des DTO au lieu d'entités
- Exemple : `ClientController`, `OrderController`, `ProductController`

#### 2. Couche Service (Logique métier)
- Contient toute la logique métier et les règles de gestion
- Implémente les interfaces de service
- Gère les transactions avec `@Transactional`
- Applique les remises, calcule la TVA, met à jour les niveaux de fidélité
- Exemple : `ClientService`, `OrderService`, `PaymentService`

#### 3. Couche Repository (Accès aux données)
- Interface avec la base de données via Spring Data JPA
- Utilise des méthodes CRUD et des requêtes personnalisées
- Exemple : `ClientRepository`, `OrderRepository`, `ProductRepository`

#### 4. Couche Entity (Modèle de données)
- Représente les tables de la base de données
- Annotées avec `@Entity`, `@Table`, `@Column`
- Relations JPA : `@OneToMany`, `@ManyToOne`, etc.
- Exemple : `Client`, `Order`, `Product`, `Payment`

#### 5. Couche DTO (Data Transfer Objects)
- Objets pour transférer les données entre les couches
- Évite l'exposition directe des entités
- Validation avec annotations (`@NotNull`, `@Size`, etc.)

#### 6. Couche Mapper
- Conversion entre Entity ↔ DTO
- Utilisation de MapStruct pour automatiser le mapping
- Exemple : `ClientMapper`, `OrderMapper`

#### 7. Gestion des exceptions
- `@ControllerAdvice` pour centraliser la gestion des erreurs
- Codes HTTP cohérents (400, 401, 403, 404, 422, 500)
- Réponses JSON standardisées avec timestamp, message, et path

### Diagramme de flux

