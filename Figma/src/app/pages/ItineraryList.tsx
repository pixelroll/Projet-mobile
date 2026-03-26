import { useNavigate } from "react-router";
import { ArrowLeft, Heart, MapPin, Clock, TrendingUp, Euro } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { useItinerary } from "../contexts/ItineraryContext";

interface Itinerary {
  id: number;
  name: string;
  budget: number;
  duration: string;
  effort: "low" | "medium" | "high";
  stops: number;
  description: string;
}

const itineraries: Itinerary[] = [
  {
    id: 1,
    name: "Économique",
    budget: 45,
    duration: "6h30",
    effort: "medium",
    stops: 8,
    description: "Découverte des essentiels avec un budget limité",
  },
  {
    id: 2,
    name: "Équilibré",
    budget: 95,
    duration: "8h",
    effort: "medium",
    stops: 10,
    description: "Un mélange parfait de culture, gastronomie et détente",
  },
  {
    id: 3,
    name: "Confort",
    budget: 180,
    duration: "7h30",
    effort: "low",
    stops: 7,
    description: "Expérience premium avec des pauses régulières",
  },
];

const effortColors = {
  low: "bg-green-100 text-green-700",
  medium: "bg-yellow-100 text-yellow-700",
  high: "bg-red-100 text-red-700",
};

const effortLabels = {
  low: "Facile",
  medium: "Modéré",
  high: "Intense",
};

export function ItineraryList() {
  const navigate = useNavigate();
  const { toggleLikeItinerary, isItineraryLiked } = useItinerary();

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4">
        <div className="flex items-center gap-3 mb-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-semibold">Parcours générés</h2>
        </div>
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <MapPin className="w-4 h-4" />
          <span>Paris, France • 17 Mars 2026</span>
        </div>
      </div>

      <div className="p-4 space-y-4">
        {/* Info banner */}
        <div className="bg-primary/5 border border-primary/20 rounded-lg p-4">
          <p className="text-sm text-foreground">
            ✨ Nous avons généré <span className="font-semibold">3 parcours</span> adaptés
            à vos préférences et votre budget.
          </p>
        </div>

        {/* Itinerary cards */}
        {itineraries.map((itinerary) => (
          <div
            key={itinerary.id}
            className="bg-card rounded-xl overflow-hidden shadow-sm border border-border"
          >
            <div className="p-4 space-y-3">
              {/* Header */}
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="font-bold text-lg">{itinerary.name}</h3>
                  <p className="text-sm text-muted-foreground mt-1">
                    {itinerary.description}
                  </p>
                </div>
                <button
                  className="p-2 hover:bg-muted rounded-full"
                  onClick={() => toggleLikeItinerary(itinerary.id)}
                >
                  <Heart
                    className={`w-5 h-5 ${
                      isItineraryLiked(itinerary.id) ? "text-red-500" : "text-muted-foreground"
                    }`}
                  />
                </button>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-2 gap-3">
                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <Euro className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Budget estimé</p>
                    <p className="font-semibold">{itinerary.budget}€</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <Clock className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Durée totale</p>
                    <p className="font-semibold">{itinerary.duration}</p>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <TrendingUp className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Niveau d'effort</p>
                    <Badge
                      variant="secondary"
                      className={`${effortColors[itinerary.effort]} text-xs`}
                    >
                      {effortLabels[itinerary.effort]}
                    </Badge>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <MapPin className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <p className="text-xs text-muted-foreground">Nombre d'étapes</p>
                    <p className="font-semibold">{itinerary.stops} arrêts</p>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex gap-2 pt-2">
                <Button
                  onClick={() => navigate(`/travelpath/itinerary/${itinerary.id}`)}
                  className="flex-1 bg-primary hover:bg-primary/90"
                >
                  Voir le détail
                </Button>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() => toggleLikeItinerary(itinerary.id)}
                >
                  <Heart
                    className={`w-5 h-5 ${
                      isItineraryLiked(itinerary.id)
                        ? "text-red-500 fill-red-500"
                        : "text-muted-foreground"
                    }`}
                  />
                </Button>
              </div>
            </div>
          </div>
        ))}

        {/* Bottom actions */}
        <div className="flex gap-3 pt-2">
          <Button
            variant="outline"
            onClick={() => navigate("/travelpath/preferences")}
            className="flex-1"
          >
            Modifier les préférences
          </Button>
          <Button
            variant="outline"
            className="flex-1"
          >
            Regénérer
          </Button>
        </div>
      </div>

      <BottomNav />
    </MobileContainer>
  );
}