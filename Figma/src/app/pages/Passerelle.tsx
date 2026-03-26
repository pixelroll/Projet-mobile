import { useState } from "react";
import { useNavigate } from "react-router";
import { ArrowLeft, Heart, MapPin, Sparkles } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";

interface LikedPhoto {
  id: number;
  image: string;
  place: string;
  city: string;
  selected: boolean;
}

interface GeneratedItinerary {
  id: number;
  name: string;
  places: string[];
  duration: string;
  budget: number;
}

export function Passerelle() {
  const navigate = useNavigate();
  const [likedPhotos, setLikedPhotos] = useState<LikedPhoto[]>([
    {
      id: 1,
      image: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
      place: "Tour Eiffel",
      city: "Paris",
      selected: true,
    },
    {
      id: 2,
      image: "https://images.unsplash.com/photo-1707952189186-8694f03529f7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
      place: "Musée du Louvre",
      city: "Paris",
      selected: true,
    },
    {
      id: 3,
      image: "https://images.unsplash.com/photo-1624671419120-f35b177bf37c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
      place: "Jardin du Luxembourg",
      city: "Paris",
      selected: false,
    },
    {
      id: 4,
      image: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
      place: "Colisée",
      city: "Rome",
      selected: false,
    },
    {
      id: 5,
      image: "https://images.unsplash.com/photo-1695306812834-7f7ccc0e1140?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
      place: "Montmartre",
      city: "Paris",
      selected: true,
    },
  ]);

  const generatedItineraries: GeneratedItinerary[] = [
    {
      id: 1,
      name: "Tour des monuments parisiens",
      places: ["Tour Eiffel", "Louvre", "Montmartre"],
      duration: "1 jour",
      budget: 85,
    },
    {
      id: 2,
      name: "Paris romantique",
      places: ["Tour Eiffel", "Montmartre", "Seine"],
      duration: "½ journée",
      budget: 45,
    },
  ];

  const togglePhotoSelection = (id: number) => {
    setLikedPhotos((prev) =>
      prev.map((photo) =>
        photo.id === id ? { ...photo, selected: !photo.selected } : photo
      )
    );
  };

  const selectedCount = likedPhotos.filter((p) => p.selected).length;

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4">
        <div className="flex items-center gap-3 mb-2">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div className="flex-1">
            <h2 className="font-semibold">Créer un parcours depuis vos photos</h2>
          </div>
        </div>
        <p className="text-sm text-muted-foreground">
          Sélectionnez les lieux que vous aimez pour générer un itinéraire personnalisé
        </p>
      </div>

      <div className="p-4 space-y-6">
        {/* Info banner */}
        <div className="bg-gradient-to-r from-primary/10 to-accent/10 border border-primary/20 rounded-lg p-4">
          <div className="flex gap-3">
            <Sparkles className="w-6 h-6 text-primary flex-shrink-0" />
            <div>
              <h3 className="font-semibold mb-1">Intelligence artificielle</h3>
              <p className="text-sm text-muted-foreground">
                Nous analysons vos photos aimées pour créer des parcours qui correspondent
                à vos goûts et vos envies de voyage.
              </p>
            </div>
          </div>
        </div>

        {/* Liked photos carousel */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold">Vos photos aimées</h3>
            <span className="text-sm text-muted-foreground">
              {selectedCount} sélectionnée{selectedCount > 1 ? "s" : ""}
            </span>
          </div>

          <div className="flex gap-3 overflow-x-auto pb-3">
            {likedPhotos.map((photo) => (
              <div key={photo.id} className="flex-shrink-0 w-36">
                <div
                  className={`relative rounded-lg overflow-hidden cursor-pointer transition-all ${
                    photo.selected ? "ring-4 ring-primary" : ""
                  }`}
                  onClick={() => togglePhotoSelection(photo.id)}
                >
                  <img
                    src={photo.image}
                    alt={photo.place}
                    className="w-full h-40 object-cover"
                  />
                  {photo.selected && (
                    <div className="absolute top-2 right-2 w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                      <Heart className="w-4 h-4 text-white fill-white" />
                    </div>
                  )}
                  <div className="absolute inset-x-0 bottom-0 bg-gradient-to-t from-black/70 to-transparent p-2">
                    <p className="text-white text-sm font-medium">{photo.place}</p>
                    <p className="text-white/80 text-xs">{photo.city}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Generate button */}
        {selectedCount > 0 && (
          <Button
            onClick={() => navigate("/travelpath/preferences")}
            className="w-full bg-primary hover:bg-primary/90 py-6"
          >
            <Sparkles className="w-5 h-5 mr-2" />
            Générer un parcours complet ({selectedCount} lieux)
          </Button>
        )}

        {/* Generated mini-itineraries */}
        {selectedCount > 0 && (
          <div>
            <h3 className="font-semibold mb-3">Idées de parcours</h3>
            <p className="text-sm text-muted-foreground mb-4">
              Basé sur vos {selectedCount} photos sélectionnées
            </p>

            <div className="space-y-3">
              {generatedItineraries.map((itinerary) => (
                <div
                  key={itinerary.id}
                  className="bg-card rounded-lg border border-border p-4 hover:border-primary transition-colors cursor-pointer"
                  onClick={() => navigate("/travelpath/itineraries")}
                >
                  <h4 className="font-semibold mb-2">{itinerary.name}</h4>
                  <div className="flex flex-wrap gap-1 mb-3">
                    {itinerary.places.map((place) => (
                      <Badge key={place} variant="secondary" className="text-xs">
                        <MapPin className="w-3 h-3 mr-1" />
                        {place}
                      </Badge>
                    ))}
                  </div>
                  <div className="flex items-center gap-4 text-sm text-muted-foreground">
                    <span>⏱️ {itinerary.duration}</span>
                    <span>💰 ~{itinerary.budget}€</span>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    className="w-full mt-3"
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate("/travelpath/preferences");
                    }}
                  >
                    Personnaliser ce parcours
                  </Button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Empty state */}
        {selectedCount === 0 && (
          <div className="text-center py-12">
            <Heart className="w-16 h-16 text-muted-foreground mx-auto mb-4 opacity-50" />
            <h3 className="font-semibold mb-2">Aucune photo sélectionnée</h3>
            <p className="text-sm text-muted-foreground mb-6">
              Sélectionnez au moins une photo pour générer un parcours
            </p>
            <Button variant="outline" onClick={() => navigate("/home")}>
              Retour à l'accueil
            </Button>
          </div>
        )}

        {/* How it works */}
        <div className="border-t border-border pt-6">
          <h3 className="font-semibold mb-3">Comment ça marche ?</h3>
          <div className="space-y-3">
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                <span className="text-sm font-bold text-primary">1</span>
              </div>
              <div>
                <p className="font-medium text-sm">Sélectionnez vos lieux préférés</p>
                <p className="text-sm text-muted-foreground">
                  Parcourez vos photos aimées et choisissez celles qui vous intéressent
                </p>
              </div>
            </div>
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                <span className="text-sm font-bold text-primary">2</span>
              </div>
              <div>
                <p className="font-medium text-sm">L'IA génère des parcours</p>
                <p className="text-sm text-muted-foreground">
                  Nous créons automatiquement des itinéraires optimisés
                </p>
              </div>
            </div>
            <div className="flex gap-3">
              <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                <span className="text-sm font-bold text-primary">3</span>
              </div>
              <div>
                <p className="font-medium text-sm">Personnalisez et partez</p>
                <p className="text-sm text-muted-foreground">
                  Ajustez selon vos préférences et commencez votre aventure
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <BottomNav />
    </MobileContainer>
  );
}
