import { useState } from "react";
import { useNavigate } from "react-router";
import { ArrowLeft, UtensilsCrossed, Gamepad2, Compass, Landmark, Plus, Sparkles } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Label } from "../components/ui/label";
import { Checkbox } from "../components/ui/checkbox";
import { Slider } from "../components/ui/slider";
import { Badge } from "../components/ui/badge";

export function TravelPathPreferences() {
  const navigate = useNavigate();
  const [budget, setBudget] = useState([100]);
  const [duration, setDuration] = useState("1day");
  const [activities, setActivities] = useState({
    restauration: true,
    loisirs: false,
    decouverte: true,
    culture: true,
  });
  const [profile, setProfile] = useState<string[]>(["famille"]);
  const [weather, setWeather] = useState<string[]>([]);
  const [favoritePlaces] = useState([
    "Tour Eiffel",
    "Musée du Louvre",
    "Notre-Dame",
  ]);

  const toggleActivity = (key: string) => {
    setActivities((prev) => ({ ...prev, [key]: !prev[key as keyof typeof prev] }));
  };

  const toggleProfile = (value: string) => {
    setProfile((prev) =>
      prev.includes(value) ? prev.filter((p) => p !== value) : [...prev, value]
    );
  };

  const toggleWeather = (value: string) => {
    setWeather((prev) =>
      prev.includes(value) ? prev.filter((w) => w !== value) : [...prev, value]
    );
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="font-semibold">Créer un parcours</h2>
      </div>

      <div className="p-4 space-y-6">
        {/* Info banner with link to Passerelle */}
        <div className="bg-gradient-to-r from-primary/10 to-accent/10 border border-primary/20 rounded-lg p-4">
          <div className="flex gap-3">
            <Sparkles className="w-6 h-6 text-primary flex-shrink-0" />
            <div className="flex-1">
              <h3 className="font-semibold mb-1">Générer depuis vos photos</h3>
              <p className="text-sm text-muted-foreground mb-3">
                Créez un parcours basé sur vos photos favorites
              </p>
              <Button
                variant="outline"
                size="sm"
                onClick={() => navigate("/passerelle")}
                className="text-primary border-primary hover:bg-primary/5"
              >
                Essayer maintenant
              </Button>
            </div>
          </div>
        </div>

        {/* Activities */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <h3 className="font-semibold mb-4">Type d'activités</h3>
          <div className="space-y-3">
            <div className="flex items-center gap-3">
              <Checkbox
                id="restauration"
                checked={activities.restauration}
                onCheckedChange={() => toggleActivity("restauration")}
              />
              <Label htmlFor="restauration" className="flex items-center gap-2 cursor-pointer">
                <UtensilsCrossed className="w-5 h-5 text-primary" />
                Restauration
              </Label>
            </div>
            <div className="flex items-center gap-3">
              <Checkbox
                id="loisirs"
                checked={activities.loisirs}
                onCheckedChange={() => toggleActivity("loisirs")}
              />
              <Label htmlFor="loisirs" className="flex items-center gap-2 cursor-pointer">
                <Gamepad2 className="w-5 h-5 text-primary" />
                Loisirs
              </Label>
            </div>
            <div className="flex items-center gap-3">
              <Checkbox
                id="decouverte"
                checked={activities.decouverte}
                onCheckedChange={() => toggleActivity("decouverte")}
              />
              <Label htmlFor="decouverte" className="flex items-center gap-2 cursor-pointer">
                <Compass className="w-5 h-5 text-primary" />
                Découverte
              </Label>
            </div>
            <div className="flex items-center gap-3">
              <Checkbox
                id="culture"
                checked={activities.culture}
                onCheckedChange={() => toggleActivity("culture")}
              />
              <Label htmlFor="culture" className="flex items-center gap-2 cursor-pointer">
                <Landmark className="w-5 h-5 text-primary" />
                Culture
              </Label>
            </div>
          </div>
        </div>

        {/* Favorite places */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <h3 className="font-semibold mb-3">Lieux favoris / obligatoires</h3>
          <div className="flex flex-wrap gap-2">
            {favoritePlaces.map((place) => (
              <Badge key={place} variant="secondary" className="text-sm">
                {place}
              </Badge>
            ))}
            <Badge
              variant="outline"
              className="cursor-pointer hover:bg-muted text-sm gap-1"
            >
              <Plus className="w-3 h-3" />
              Ajouter
            </Badge>
          </div>
        </div>

        {/* Budget */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold">Budget maximum</h3>
            <span className="font-bold text-primary">{budget[0]}€</span>
          </div>
          <Slider
            value={budget}
            onValueChange={setBudget}
            max={500}
            min={0}
            step={10}
            className="mb-2"
          />
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>0€</span>
            <span>500€</span>
          </div>
        </div>

        {/* Duration */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <h3 className="font-semibold mb-3">Durée</h3>
          <div className="grid grid-cols-3 gap-2">
            <button
              onClick={() => setDuration("halfday")}
              className={`p-3 rounded-lg border-2 text-sm font-medium transition-colors ${
                duration === "halfday"
                  ? "border-primary bg-primary/5 text-primary"
                  : "border-border hover:border-primary/50"
              }`}
            >
              ½ journée
            </button>
            <button
              onClick={() => setDuration("1day")}
              className={`p-3 rounded-lg border-2 text-sm font-medium transition-colors ${
                duration === "1day"
                  ? "border-primary bg-primary/5 text-primary"
                  : "border-border hover:border-primary/50"
              }`}
            >
              1 jour
            </button>
            <button
              onClick={() => setDuration("2days+")}
              className={`p-3 rounded-lg border-2 text-sm font-medium transition-colors ${
                duration === "2days+"
                  ? "border-primary bg-primary/5 text-primary"
                  : "border-border hover:border-primary/50"
              }`}
            >
              2 jours+
            </button>
          </div>
        </div>

        {/* Profile */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <h3 className="font-semibold mb-3">Profil / Effort</h3>
          <div className="flex flex-wrap gap-2">
            {["Famille", "Seniors", "Sportif", "Aventurier"].map((item) => (
              <button
                key={item}
                onClick={() => toggleProfile(item.toLowerCase())}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  profile.includes(item.toLowerCase())
                    ? "bg-primary text-white"
                    : "bg-muted text-foreground hover:bg-muted/80"
                }`}
              >
                {item}
              </button>
            ))}
          </div>
        </div>

        {/* Weather */}
        <div className="bg-card rounded-xl p-4 border border-border">
          <h3 className="font-semibold mb-3">Préférences météo</h3>
          <div className="flex flex-wrap gap-2">
            {["Froid", "Chaleur", "Humidité", "Ensoleillé"].map((item) => (
              <button
                key={item}
                onClick={() => toggleWeather(item.toLowerCase())}
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  weather.includes(item.toLowerCase())
                    ? "bg-primary text-white"
                    : "bg-muted text-foreground hover:bg-muted/80"
                }`}
              >
                {item}
              </button>
            ))}
          </div>
        </div>

        {/* Generate button */}
        <Button
          onClick={() => navigate("/travelpath/itineraries")}
          className="w-full bg-primary hover:bg-primary/90 py-6"
        >
          Générer les parcours
        </Button>
      </div>

      <BottomNav />
    </MobileContainer>
  );
}