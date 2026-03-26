# PLAN - Application Traveling (Android)

## Avancement

- [x] Phase 0 : Audit de la maquette Figma et définition de la structure de l'application.
- [x] Phase 1 : Mise en place de l'architecture de base, Configuration du Thème (Material 3), Navigation (BottomNavigationView).
- [x] Phase 2 : Configuration Backend (dépendances Firestore, Firebase Auth, FCM, SDK Cloudinary, POJOs).
- [x] Phase 3 : Développement du sous-système **TravelShare** (Mode Anonyme/Connecté, Feed de photos, Recherche).
- [ ] Phase 4 : Développement du sous-système **TravelPath** (Saisie préférences, API Générateur Mock/Algorithme, Timeline Itinéraire).
- [ ] Phase 5 : Intégration de la **Passerelle**.

## Choix Techniques
* **Langage** : Java
* **UI** : `ConstraintLayout`, Composants Material Design 3, `RecyclerView` avancés.
* **Architecture** : MVVM (Model-View-ViewModel) + Navigation Component (Fragments et `NavGraph`).
* **Base de données** : Firebase Firestore (NoSQL temps réel).
* **Médias** : Cloudinary (stockage, transformation on-the-fly, et gestion des notes audio).
* **Authentification** : Firebase Auth.

## Prochaines Étapes
1. Créer la vue principale (`MainActivity`) avec la bottom navigation.
2. Saisir les styles et couleurs depuis Figma (`colors.xml`, `strings.xml`, `themes.xml`).
3. Créer les répertoires pour les Modèles (POJOs Firestore).
