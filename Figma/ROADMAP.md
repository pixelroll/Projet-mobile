# 🗺️ ROADMAP - TRAVELING APP
## Carte de route pour atteindre 100% du cahier des charges

**Version**: 1.0  
**Date**: 18 Mars 2026  
**Objectif**: Implémenter toutes les fonctionnalités manquantes et finaliser les fonctionnalités partielles

---

## 📊 Vue d'ensemble

| Phase | Objectif | Fonctionnalités | Durée estimée |
|-------|----------|----------------|---------------|
| **Phase 1** | Fonctionnalités critiques | Vue carte, Groupes, Actions manquantes | 3-4 jours |
| **Phase 2** | Fonctionnalités importantes | Sauvegarde, Partage, Export PDF | 2-3 jours |
| **Phase 3** | Intégrations & IA | Tags IA, Google Maps, Calculs dynamiques | 3-4 jours |
| **Phase 4** | Fonctionnalités avancées | Hors-ligne, Créneaux, Optimisations | 2-3 jours |
| **Phase 5** | Tests & Polish | Tests complets, Bugs, UX refinement | 2 jours |

**Durée totale estimée**: 12-16 jours de développement

---

## 🚀 PHASE 1 : Fonctionnalités Critiques (HAUTE PRIORITÉ)
**Durée**: 3-4 jours  
**Objectif**: Implémenter les fonctionnalités essentielles du cahier des charges

### 1.1 Vue Carte avec Pins pour les Photos 🗺️
**Priorité**: 🔴 CRITIQUE  
**Complexité**: ⭐⭐⭐ Moyenne  
**Durée estimée**: 1.5 jours

#### Description
Créer une vue carte interactive affichant les photos sous forme de pins géolocalisés.

#### Tâches
- [ ] Installer une librairie de carte (react-leaflet ou Google Maps React)
- [ ] Créer le composant `MapView.tsx`
- [ ] Ajouter un toggle "Carte/Grille" dans la page Home
- [ ] Implémenter les pins avec preview de photo au survol
- [ ] Ajouter le clustering pour les groupes de pins proches
- [ ] Gérer le zoom et le centrage sur une région
- [ ] Permettre le clic sur un pin pour ouvrir PhotoDetail

#### Fichiers à créer/modifier
```
/src/app/components/MapView.tsx (nouveau)
/src/app/pages/Home.tsx (modifier)
/src/app/pages/AdvancedSearch.tsx (modifier - ajouter vue carte)
```

#### Dépendances
- Package: `react-leaflet` ou `@vis.gl/react-google-maps`
- Mock data avec coordonnées GPS pour chaque photo

---

### 1.2 Système de Groupes Complet 👥
**Priorité**: 🔴 CRITIQUE  
**Complexité**: ⭐⭐⭐ Moyenne  
**Durée estimée**: 1 jour

#### Description
Finaliser la création et la gestion des groupes d'utilisateurs.

#### Tâches
- [ ] Créer la page `CreateGroup.tsx`
- [ ] Créer la page `GroupList.tsx` (mes groupes)
- [ ] Créer la page `GroupDetail.tsx` (membres, photos du groupe)
- [ ] Implémenter le dialog de création de groupe dans PublishPhoto
- [ ] Ajouter la fonctionnalité "Joindre un groupe" (code d'invitation)
- [ ] Gérer les permissions (admin, membre)
- [ ] Ajouter une section "Groupes" dans le profil

#### Fichiers à créer/modifier
```
/src/app/pages/CreateGroup.tsx (nouveau)
/src/app/pages/GroupList.tsx (nouveau)
/src/app/pages/GroupDetail.tsx (nouveau)
/src/app/pages/PublishPhoto.tsx (modifier - finaliser dialog)
/src/app/pages/Profile.tsx (modifier - ajouter tab Groupes)
/src/app/data/groups.ts (nouveau - mock data)
```

#### Mock data
```typescript
interface Group {
  id: string;
  name: string;
  description: string;
  coverImage: string;
  members: User[];
  admins: string[]; // user IDs
  inviteCode: string;
  createdAt: Date;
  photosCount: number;
}
```

---

### 1.3 Actions Manquantes (Signalement, Like Parcours) ⚡
**Priorité**: 🔴 HAUTE  
**Complexité**: ⭐ Facile  
**Durée estimée**: 0.5 jour

#### Description
Implémenter les actions pour les boutons déjà présents dans l'UI.

#### Tâches
- [ ] Implémenter l'action "Signaler une photo"
  - Dialog de signalement avec raisons (contenu inapproprié, spam, etc.)
  - Confirmation après signalement
- [ ] Implémenter le like/unlike de parcours
  - Ajouter état dans Context ou localStorage
  - Sauvegarder les parcours likés
  - Afficher les parcours likés dans le profil
- [ ] Implémenter le partage de profil
  - Dialog avec options (copier lien, partage natif)
- [ ] Implémenter le bouton "Modifier le profil"
  - Créer page EditProfile.tsx

#### Fichiers à créer/modifier
```
/src/app/components/ReportDialog.tsx (nouveau)
/src/app/pages/PhotoDetail.tsx (modifier)
/src/app/pages/ItineraryDetail.tsx (modifier)
/src/app/pages/ItineraryList.tsx (modifier)
/src/app/pages/Profile.tsx (modifier)
/src/app/pages/EditProfile.tsx (nouveau)
/src/app/contexts/ItineraryContext.tsx (nouveau - pour gérer likes)
```

---

## 🎯 PHASE 2 : Fonctionnalités Importantes (MOYENNE PRIORITÉ)
**Durée**: 2-3 jours  
**Objectif**: Compléter les fonctionnalités de sauvegarde et partage

### 2.1 Sauvegarde des Parcours 💾
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐ Facile-Moyenne  
**Durée estimée**: 0.5 jour

#### Description
Permettre de sauvegarder et gérer les parcours favoris.

#### Tâches
- [ ] Créer un Context `SavedItinerariesContext.tsx`
- [ ] Utiliser localStorage pour persistance
- [ ] Ajouter bouton "Sauvegarder" dans ItineraryDetail
- [ ] Créer section "Parcours sauvegardés" dans Profile
- [ ] Permettre de supprimer un parcours sauvegardé
- [ ] Badge "Sauvegardé" sur les parcours déjà sauvegardés

#### Fichiers à créer/modifier
```
/src/app/contexts/SavedItinerariesContext.tsx (nouveau)
/src/app/pages/ItineraryDetail.tsx (modifier)
/src/app/pages/Profile.tsx (modifier)
/src/app/pages/SavedItineraries.tsx (nouveau)
```

---

### 2.2 Partage de Profils et Parcours 📤
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐ Facile-Moyenne  
**Durée estimée**: 0.5 jour

#### Description
Implémenter le partage natif pour profils et parcours.

#### Tâches
- [ ] Créer composant `ShareDialog.tsx` réutilisable
- [ ] Implémenter partage profil
  - Générer URL de profil
  - Partage natif (Web Share API)
  - Copier le lien
- [ ] Implémenter partage parcours
  - Générer URL de parcours
  - Partage natif avec preview
  - Options : copier lien, image du parcours
- [ ] Gérer les routes publiques pour les liens partagés

#### Fichiers à créer/modifier
```
/src/app/components/ShareDialog.tsx (nouveau)
/src/app/pages/Profile.tsx (modifier)
/src/app/pages/ItineraryDetail.tsx (modifier)
/src/app/utils/share.ts (nouveau)
```

---

### 2.3 Export PDF des Parcours 📄
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐⭐ Moyenne  
**Durée estimée**: 1 jour

#### Description
Générer un PDF téléchargeable du parcours avec toutes les informations.

#### Tâches
- [ ] Installer librairie PDF (jsPDF ou react-pdf)
- [ ] Créer template PDF professionnel
  - En-tête avec titre et date
  - Carte du parcours
  - Liste des étapes avec photos
  - Informations pratiques (budget, durée, météo)
  - QR code vers le parcours en ligne
- [ ] Implémenter la génération et téléchargement
- [ ] Ajouter loading state pendant génération
- [ ] Optimiser les images pour le PDF

#### Fichiers à créer/modifier
```
/src/app/utils/pdfGenerator.ts (nouveau)
/src/app/pages/ItineraryDetail.tsx (modifier)
```

#### Dépendances
- Package: `jspdf` + `html2canvas` OU `@react-pdf/renderer`

---

### 2.4 Regénération de Parcours avec Ajustements 🔄
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐ Facile-Moyenne  
**Durée estimée**: 0.5 jour

#### Description
Permettre de regénérer des parcours en ajustant certains paramètres.

#### Tâches
- [ ] Créer dialog `RegenerateDialog.tsx`
- [ ] Options d'ajustement rapide :
  - Augmenter/diminuer le budget
  - Augmenter/diminuer la durée
  - Changer le niveau d'effort
  - Ajouter/retirer des types d'activités
- [ ] Conserver les préférences de base
- [ ] Générer de nouveaux parcours avec ajustements
- [ ] Animation de chargement

#### Fichiers à créer/modifier
```
/src/app/components/RegenerateDialog.tsx (nouveau)
/src/app/pages/ItineraryList.tsx (modifier)
```

---

## 🤖 PHASE 3 : Intégrations & Intelligence (MOYENNE-HAUTE PRIORITÉ)
**Durée**: 3-4 jours  
**Objectif**: Ajouter les intégrations intelligentes et APIs

### 3.1 Tags Automatiques par IA 🏷️
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐⭐⭐ Moyenne-Haute  
**Durée estimée**: 1.5 jours

#### Description
Générer automatiquement des tags pour les photos uploadées.

#### Tâches
- [ ] Choisir API de vision (TensorFlow.js, Google Vision API, Clarifai)
- [ ] Option 1 : TensorFlow.js (local, gratuit)
  - Charger modèle MobileNet ou COCO-SSD
  - Analyser l'image côté client
  - Extraire labels/objets détectés
- [ ] Option 2 : Google Vision API (cloud, précis)
  - Intégrer API Google Cloud Vision
  - Envoyer image pour analyse
  - Parser les labels retournés
- [ ] Créer composant `AITagSuggestions.tsx`
- [ ] Afficher suggestions de tags
- [ ] Permettre d'accepter/refuser chaque tag
- [ ] Loading state pendant analyse

#### Fichiers à créer/modifier
```
/src/app/components/AITagSuggestions.tsx (nouveau)
/src/app/utils/imageAnalysis.ts (nouveau)
/src/app/pages/PublishPhoto.tsx (modifier)
```

#### Dépendances
- Option 1: `@tensorflow/tfjs` + `@tensorflow-models/mobilenet`
- Option 2: `@google-cloud/vision` (nécessite backend)

---

### 3.2 Intégration Google Maps (Intents & Carte) 🗺️
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐ Facile-Moyenne  
**Durée estimée**: 1 jour

#### Description
Ouvrir Google Maps pour les itinéraires et afficher cartes interactives.

#### Tâches
- [ ] Implémenter ouverture Google Maps
  - Mobile: Intent Android/iOS
  - Web: URL google.com/maps
  - Passer coordonnées ou adresse
- [ ] Implémenter carte dans ItineraryDetail
  - Afficher tous les points du parcours
  - Tracer l'itinéraire entre les points
  - Pins numérotés pour chaque étape
  - Zoom adaptatif pour voir tout le parcours
- [ ] Ajouter bouton "Voir dans Google Maps" sur chaque étape
- [ ] Calculer distance réelle entre points (Google Distance Matrix)

#### Fichiers à créer/modifier
```
/src/app/utils/maps.ts (nouveau)
/src/app/components/ItineraryMap.tsx (nouveau)
/src/app/pages/ItineraryDetail.tsx (modifier)
/src/app/pages/PhotoDetail.tsx (modifier)
```

#### Dépendances
- Package: `@vis.gl/react-google-maps` ou `@react-google-maps/api`
- Google Maps API Key

---

### 3.3 Calculs Dynamiques de Parcours 🧮
**Priorité**: 🟡 MOYENNE  
**Complexité**: ⭐⭐⭐⭐⭐ Haute  
**Durée estimée**: 1.5 jours

#### Description
Remplacer les parcours mock par un algorithme de génération réel.

#### Tâches
- [ ] Créer base de données de lieux (POI - Points of Interest)
  - Restaurants, musées, parcs, monuments
  - Coordonnées GPS, prix, durée visite, horaires
  - Type d'activité, tags
- [ ] Implémenter algorithme de génération
  - Filtrer POI selon préférences utilisateur
  - Algorithme TSP (Traveling Salesman Problem) simplifié
  - Ou algorithme glouton avec optimisation
  - Prendre en compte : distance, budget, temps, effort
- [ ] Générer 3 parcours avec stratégies différentes
  - Économique : POI gratuits/pas chers, marche à pied
  - Équilibré : mix prix/qualité, transports en commun
  - Confort : POI premium, taxis/Uber, moins de marche
- [ ] Calculer métriques réelles
  - Budget total (entrées + repas + transport)
  - Temps de visite + temps de trajet
  - Effort (distance de marche en km)

#### Fichiers à créer/modifier
```
/src/app/data/pois.ts (nouveau - base POI Paris)
/src/app/utils/itineraryGenerator.ts (nouveau)
/src/app/utils/tspSolver.ts (nouveau)
/src/app/pages/ItineraryList.tsx (modifier)
```

#### Algorithme suggéré
```typescript
interface POI {
  id: string;
  name: string;
  lat: number;
  lng: number;
  type: 'restaurant' | 'museum' | 'park' | 'monument';
  cost: number;
  duration: number; // minutes
  openingHours: { start: string; end: string }[];
  effort: 'low' | 'medium' | 'high';
  tags: string[];
}

// Algorithme glouton
1. Filtrer POI selon activités sélectionnées
2. Sélectionner POI de départ (proche position utilisateur)
3. Pour chaque étape suivante:
   - Calculer score = qualité - (distance * poids) - (prix * poids)
   - Choisir POI avec meilleur score
   - Vérifier contraintes budget/temps
4. Optimiser ordre avec 2-opt algorithm
```

---

## 🌐 PHASE 4 : Fonctionnalités Avancées (BASSE PRIORITÉ)
**Durée**: 2-3 jours  
**Objectif**: Fonctionnalités bonus pour expérience premium

### 4.1 Mode Hors-ligne avec Cache 📡
**Priorité**: 🟢 BASSE  
**Complexité**: ⭐⭐⭐⭐ Moyenne-Haute  
**Durée estimée**: 1.5 jours

#### Description
Permettre l'utilisation de l'app sans connexion internet.

#### Tâches
- [ ] Configurer Service Worker pour PWA
- [ ] Implémenter stratégie de cache
  - Cache-first pour images statiques
  - Network-first pour données dynamiques
  - Fallback pour offline
- [ ] Créer page `OfflineItineraries.tsx`
- [ ] Permettre téléchargement parcours pour offline
  - Compresser images
  - Sauvegarder dans IndexedDB
  - Badge "Disponible hors-ligne"
- [ ] Indicateur de statut connexion
- [ ] Synchronisation lors de retour online

#### Fichiers à créer/modifier
```
/public/service-worker.js (nouveau)
/src/app/utils/offlineManager.ts (nouveau)
/src/app/pages/OfflineItineraries.tsx (nouveau)
/src/app/components/OfflineIndicator.tsx (nouveau)
```

#### Dépendances
- Package: `workbox-webpack-plugin` ou `vite-plugin-pwa`
- IndexedDB pour stockage local

---

### 4.2 Prise en Compte Créneaux d'Ouverture ⏰
**Priorité**: 🟢 BASSE  
**Complexité**: ⭐⭐⭐ Moyenne  
**Durée estimée**: 0.5 jour

#### Description
Vérifier les horaires d'ouverture dans la génération de parcours.

#### Tâches
- [ ] Ajouter horaires d'ouverture aux POI
  - Jours de la semaine
  - Heures d'ouverture/fermeture
  - Jours fériés / fermetures exceptionnelles
- [ ] Modifier algorithme de génération
  - Vérifier disponibilité au moment prévu
  - Réorganiser si lieu fermé
  - Alerter l'utilisateur en cas de problème
- [ ] Afficher horaires dans ItineraryDetail
- [ ] Badge "Fermé" si hors horaires

#### Fichiers à créer/modifier
```
/src/app/data/pois.ts (modifier - ajouter openingHours)
/src/app/utils/itineraryGenerator.ts (modifier)
/src/app/components/OpeningHoursIndicator.tsx (nouveau)
```

---

### 4.3 Intégration Météo en Temps Réel 🌤️
**Priorité**: 🟢 BASSE  
**Complexité**: ⭐⭐ Facile-Moyenne  
**Durée estimée**: 0.5 jour

#### Description
Afficher météo du jour et adapter suggestions.

#### Tâches
- [ ] Intégrer API météo (OpenWeatherMap, WeatherAPI)
- [ ] Afficher météo dans ItineraryDetail
  - Température
  - Conditions (soleil, pluie, nuages)
  - Icône météo
- [ ] Suggestions adaptées à la météo
  - Pluie → privilégier musées, lieux couverts
  - Beau temps → privilégier parcs, activités extérieures
- [ ] Alertes météo
  - Pluie prévue → avertissement
  - Chaleur/froid extrême → ajuster parcours

#### Fichiers à créer/modifier
```
/src/app/utils/weather.ts (nouveau)
/src/app/components/WeatherWidget.tsx (nouveau)
/src/app/pages/ItineraryDetail.tsx (modifier)
/src/app/utils/itineraryGenerator.ts (modifier)
```

#### Dépendances
- API: OpenWeatherMap (gratuit jusqu'à 1000 calls/jour)

---

### 4.4 Notifications Push Réelles 🔔
**Priorité**: 🟢 BASSE  
**Complexité**: ⭐⭐⭐⭐ Moyenne-Haute  
**Durée estimée**: 1 jour

#### Description
Implémenter vraies notifications push.

#### Tâches
- [ ] Configurer Firebase Cloud Messaging (FCM)
- [ ] Demander permission notifications
- [ ] Implémenter envoi notifications
  - Nouvelle photo d'une personne suivie
  - Nouvelle photo dans lieu suivi
  - Invitation à un groupe
  - Commentaire sur une photo
- [ ] Gérer notifications en foreground/background
- [ ] Ouvrir page appropriée au clic

#### Fichiers à créer/modifier
```
/src/app/utils/notifications.ts (nouveau)
/src/firebase-config.ts (nouveau)
/public/firebase-messaging-sw.js (nouveau)
```

#### Dépendances
- Package: `firebase`
- Configuration Firebase project

---

## 🧪 PHASE 5 : Tests, Optimisations & Polish
**Durée**: 2 jours  
**Objectif**: Application production-ready

### 5.1 Tests Complets 🧪
**Durée estimée**: 1 jour

#### Tâches
- [ ] Tests unitaires composants clés
- [ ] Tests d'intégration parcours critiques
- [ ] Tests des Context (UserMode, Itineraries)
- [ ] Tests responsive (mobile, tablette)
- [ ] Tests cross-browser (Chrome, Safari, Firefox)
- [ ] Tests performance (Lighthouse)

---

### 5.2 Optimisations Performance ⚡
**Durée estimée**: 0.5 jour

#### Tâches
- [ ] Lazy loading des images
- [ ] Code splitting des routes
- [ ] Optimisation taille bundle
- [ ] Compression images
- [ ] Memoization composants lourds
- [ ] Virtual scrolling pour listes longues

---

### 5.3 Accessibilité & UX Polish ♿
**Durée estimée**: 0.5 jour

#### Tâches
- [ ] Ajouter labels ARIA
- [ ] Support navigation clavier
- [ ] Contraste couleurs (WCAG AA)
- [ ] Textes alternatifs images
- [ ] Focus states visibles
- [ ] Animations smooth
- [ ] Loading states partout
- [ ] Messages d'erreur clairs

---

## 📋 Checklist Finale

### Avant de commencer chaque phase
- [ ] Lire les spécifications complètes
- [ ] Préparer mock data si nécessaire
- [ ] Installer dépendances requises
- [ ] Créer branche Git pour la phase

### Après chaque fonctionnalité
- [ ] Tester manuellement
- [ ] Vérifier responsive
- [ ] Vérifier cohérence design
- [ ] Commit avec message descriptif
- [ ] Mettre à jour cette roadmap

### Critères de "Terminé"
- ✅ Fonctionnalité implémentée
- ✅ Testée sur mobile et desktop
- ✅ Pas de bugs bloquants
- ✅ Design cohérent avec l'existant
- ✅ Code commenté si complexe
- ✅ Commit et push

---

## 🎯 Métriques de Progression

| Phase | Fonctionnalités | Statut | Complétion |
|-------|----------------|--------|------------|
| Phase 1 | 3 | ⏳ À faire | 0% |
| Phase 2 | 4 | ⏳ À faire | 0% |
| Phase 3 | 3 | ⏳ À faire | 0% |
| Phase 4 | 4 | ⏳ À faire | 0% |
| Phase 5 | 3 | ⏳ À faire | 0% |
| **TOTAL** | **17** | **⏳ À faire** | **0%** |

---

## 💡 Conseils d'Implémentation

### Ordre Recommandé
1. **Commencer par Phase 1** - Ce sont les fonctionnalités les plus visibles et critiques
2. **Phase 2 peut être parallèle** - Sauvegarde et partage sont indépendants
3. **Phase 3 nécessite setup** - Prévoir temps pour config APIs
4. **Phase 4 est optionnelle** - À faire si temps disponible
5. **Phase 5 est continue** - Tester au fur et à mesure

### Pièges à Éviter
- ⚠️ Ne pas sous-estimer la complexité des cartes
- ⚠️ Prévoir temps pour apprendre les APIs (Google Maps, Vision)
- ⚠️ Tester régulièrement sur mobile réel
- ⚠️ Garder le code clean et maintenable
- ⚠️ Commit souvent, petits commits atomiques

### Ressources Utiles
- **Cartes**: [React Leaflet Docs](https://react-leaflet.js.org/)
- **PDF**: [jsPDF Examples](https://parall.ax/products/jspdf)
- **IA Vision**: [TensorFlow.js Models](https://www.tensorflow.org/js/models)
- **Météo**: [OpenWeatherMap API](https://openweathermap.org/api)
- **TSP Algorithm**: [Google OR-Tools](https://developers.google.com/optimization)

---

## 🏁 Objectif Final

À la fin de cette roadmap, l'application **Traveling** sera :
- ✅ **100% conforme** au cahier des charges
- ✅ **Production-ready** avec toutes les fonctionnalités
- ✅ **Optimisée** pour performance et UX
- ✅ **Testée** et stable
- ✅ **Extensible** pour futures évolutions

**Bon courage pour l'implémentation ! 🚀**
