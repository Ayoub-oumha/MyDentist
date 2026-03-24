📘 Cahier des Charges
Application MyDentist

Prise de rendez-vous chez le dentiste (Carte – Paiement – Documents)

1. Présentation du Projet
   1.1 Contexte

La digitalisation du secteur médical est devenue essentielle pour améliorer l’expérience des patients et optimiser la gestion des cabinets dentaires.
Ce projet vise à développer une application web moderne permettant aux patients de :

Prendre des rendez-vous en ligne
Localiser un cabinet dentaire
Effectuer des paiements en ligne
Échanger des documents médicaux de manière sécurisée
1.2 Objectifs
Faciliter la prise de rendez-vous
Réduire la gestion manuelle
Permettre le paiement sécurisé
Centraliser les documents médicaux
Offrir un tableau de bord pour les dentistes
✅ Mettre en place une architecture monolithique (un seul service) simple et efficace
2. Spécifications Techniques
   2.1 Architecture Technique
   Architecture Monolithique (Single Service)
   Backend centralisé (une seule application)
   Communication interne via services/modules
   API REST pour le frontend

👉 Contrairement aux microservices, tout est dans un seul projet Spring Boot

2.2 Backend
Java – Spring Boot
Spring Security + JWT
Spring Data JPA / Hibernate
Base de données MySQL

👉 Modules internes (dans le même projet) :

Gestion des utilisateurs & authentification
Gestion des rendez-vous
Gestion des paiements
Gestion des documents médicaux
2.3 Frontend
Angular
Angular Material
RxJS
NgRx (optionnel)
Responsive Design
2.4 Autres Technologies
Carte & Géolocalisation : Google Maps API
Paiement en ligne : Stripe
Stockage des documents : AWS S3
Notifications : Email (SMTP)
2.5 DevOps & Déploiement
Docker
CI/CD : GitHub Actions (ou Jenkins)
Hébergement AWS
Certificat SSL/TLS
Monitoring & Logs
3. Fonctionnalités
   3.1 Interface Patient
   Authentification
   Inscription (nom, email, mot de passe)
   Connexion sécurisée
   Réinitialisation du mot de passe
   JWT
   Prise de Rendez-vous
   Consultation des dentistes
   Choix date et heure
   Calendrier interactif
   Confirmation
   Carte & Localisation
   Affichage du cabinet
   Itinéraire via Google Maps
   Informations de contact
   Paiement en ligne
   Paiement sécurisé
   Historique
   Facture téléchargeable
   Documents Médicaux
   Upload (radio, analyses…)
   Téléchargement sécurisé
   Historique
   Accès patient ↔ dentiste
   Profil Patient
   Historique des rendez-vous
   Modification des infos
   Notifications email
   3.2 Interface Dentiste (Admin)
   Gestion des Rendez-vous
   Tableau de bord
   Acceptation / Annulation
   Gestion des disponibilités
   Gestion des Patients
   Consultation des dossiers
   Accès aux documents
   Ajout de notes
   Documents
   Upload sécurisé
   Classement par patient
   Statistiques
   Nombre de rendez-vous
   Revenus
   Activité
   Export PDF
4. Sécurité
   Authentification JWT
   Mots de passe chiffrés (bcrypt)
   Rôles : PATIENT / DENTISTE
   Protection CSRF & XSS
   Chiffrement des documents
   Sauvegardes AWS
5. Tests
   Tests unitaires (Spring Boot, Angular)
   Tests fonctionnels
   Tests d’intégration (dans le monolithe)
   Tests de sécurité
   Tests de performance
6. Livrables
   Cahier des charges
   Maquettes UI (Figma)
   Architecture Monolithique
   Diagrammes UML :
   Classes
   ERD
   Cas d’utilisation
   Code source
   Pipeline CI/CD
   Documentation technique
   Présentation finale
   ✅ Résumé important

👉 Avant : architecture microservices (complexe)
👉 Maintenant : un seul service (monolithique)

✔ Plus simple à développer
✔ Plus rapide pour un projet étudiant
✔ Plus facile à déployer

Si tu veux, je peux aussi :

te faire diagramme UML (classe + ERD)
ou te donner structure Spring Boot (packages prêts)
ou même commencer le code backend avec toi 🚀