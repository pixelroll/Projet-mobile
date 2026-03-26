import { useNavigate, useParams } from "react-router";
import {
  ArrowLeft,
  MapPin,
  Clock,
  Euro,
  Share2,
  Download,
  RotateCw,
  Heart,
  UtensilsCrossed,
  Coffee,
  Landmark,
  Trees,
  Navigation,
  Sun,
  Cloud,
  Thermometer,
  Wind,
  Droplets,
  WifiOff,
  Check,
  Image,
  Play,
  ChevronDown,
  ChevronUp,
  X,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { useItinerary } from "../contexts/ItineraryContext";
import { ShareDialog } from "../components/ShareDialog";
import { useState } from "react";
import { Dialog, DialogContent, DialogTitle, DialogDescription } from "../components/ui/dialog";

interface StepGalleryItem {
  url: string;
  type: "photo" | "video";
  caption?: string;
}

interface ItineraryStep {
  id: number;
  time: string;
  period: string;
  name: string;
  type: "restaurant" | "cafe" | "museum" | "park";
  image: string;
  duration: string;
  cost: number;
  description: string;
  openingHours: string;
  isOpenNow: boolean;
  gallery: StepGalleryItem[];
}

const steps: ItineraryStep[] = [
  {
    id: 1,
    time: "09:00",
    period: "Matin",
    name: "Petit-déjeuner au Café de Flore",
    type: "cafe",
    image: "https://images.unsplash.com/photo-1577056922988-2387f2cee50d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "1h",
    cost: 15,
    description: "Café historique du quartier Saint-Germain",
    openingHours: "07:30 – 01:30",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1577056922988-2387f2cee50d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Terrasse du Café de Flore" },
      { url: "https://images.unsplash.com/photo-1509042239860-f550ce710b93?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Café et croissant" },
      { url: "https://images.unsplash.com/photo-1554118811-1e0d58224f24?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "video", caption: "Ambiance matinale" },
    ],
  },
  {
    id: 2,
    time: "10:30",
    period: "Matin",
    name: "Musée du Louvre",
    type: "museum",
    image: "https://images.unsplash.com/photo-1707952189186-8694f03529f7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "2h30",
    cost: 17,
    description: "Visite des collections principales",
    openingHours: "09:00 – 18:00",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1707952189186-8694f03529f7?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Pyramide du Louvre" },
      { url: "https://images.unsplash.com/photo-1499426600726-ac36e0dbd6f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Galerie intérieure" },
      { url: "https://images.unsplash.com/photo-1574158622682-e40e69881006?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "video", caption: "Visite guidée de la Joconde" },
      { url: "https://images.unsplash.com/photo-1564399579883-451a5d44ec08?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Sculpture grecque" },
    ],
  },
  {
    id: 3,
    time: "13:00",
    period: "Après-midi",
    name: "Déjeuner à La Palette",
    type: "restaurant",
    image: "https://images.unsplash.com/photo-1695306812834-7f7ccc0e1140?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "1h30",
    cost: 28,
    description: "Restaurant traditionnel avec terrasse",
    openingHours: "12:00 – 23:00",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1695306812834-7f7ccc0e1140?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Plat du jour" },
      { url: "https://images.unsplash.com/photo-1414235077428-338989a2e8c0?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Terrasse" },
    ],
  },
  {
    id: 4,
    time: "14:30",
    period: "Après-midi",
    name: "Jardin du Luxembourg",
    type: "park",
    image: "https://images.unsplash.com/photo-1624671419120-f35b177bf37c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "1h",
    cost: 0,
    description: "Balade relaxante dans les jardins",
    openingHours: "07:30 – 21:30",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1624671419120-f35b177bf37c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Bassin central" },
      { url: "https://images.unsplash.com/photo-1587668178277-295251f900ce?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "video", caption: "Promenade dans les allées" },
      { url: "https://images.unsplash.com/photo-1555940280-66bf87aa823d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Fontaine Médicis" },
    ],
  },
  {
    id: 5,
    time: "16:00",
    period: "Après-midi",
    name: "Tour Eiffel",
    type: "museum",
    image: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "1h30",
    cost: 26,
    description: "Montée au 2ème étage",
    openingHours: "09:30 – 23:45",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Vue depuis le Trocadéro" },
      { url: "https://images.unsplash.com/photo-1543349689-9a4d426bee8e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Détail de la structure" },
      { url: "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "video", caption: "Panorama au 2ème étage" },
    ],
  },
  {
    id: 6,
    time: "18:00",
    period: "Soir",
    name: "Pause café - Carette",
    type: "cafe",
    image: "https://images.unsplash.com/photo-1577056922988-2387f2cee50d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    duration: "45min",
    cost: 9,
    description: "Pâtisserie renommée du Trocadéro",
    openingHours: "08:00 – 23:30",
    isOpenNow: true,
    gallery: [
      { url: "https://images.unsplash.com/photo-1577056922988-2387f2cee50d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Pâtisseries" },
      { url: "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", type: "photo", caption: "Vue sur la Tour Eiffel" },
    ],
  },
];

const getIcon = (type: string) => {
  switch (type) {
    case "restaurant":
      return UtensilsCrossed;
    case "cafe":
      return Coffee;
    case "museum":
      return Landmark;
    case "park":
      return Trees;
    default:
      return MapPin;
  }
};

export function ItineraryDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { toggleLikeItinerary, isItineraryLiked } = useItinerary();
  const [isShareDialogOpen, setIsShareDialogOpen] = useState(false);
  const [showPdfDialog, setShowPdfDialog] = useState(false);
  const [pdfGenerating, setPdfGenerating] = useState(false);
  const [pdfReady, setPdfReady] = useState(false);
  const [showSaveToast, setShowSaveToast] = useState(false);
  const [offlineDownloading, setOfflineDownloading] = useState(false);
  const [offlineReady, setOfflineReady] = useState(false);
  const [expandedGallery, setExpandedGallery] = useState<number | null>(null);
  const [lightboxImage, setLightboxImage] = useState<StepGalleryItem | null>(null);

  const totalCost = steps.reduce((sum, step) => sum + step.cost, 0);
  const totalDuration = "8h";
  const walkingTime = "45min";

  const itineraryId = id ? parseInt(id) : 2;
  const currentUrl = window.location.href;

  const handleSave = () => {
    toggleLikeItinerary(itineraryId);
    if (!isItineraryLiked(itineraryId)) {
      setShowSaveToast(true);
      setTimeout(() => setShowSaveToast(false), 3000);
    }
  };

  const handleExportPdf = () => {
    setShowPdfDialog(true);
    setPdfGenerating(true);
    setPdfReady(false);
    setTimeout(() => {
      setPdfGenerating(false);
      setPdfReady(true);
    }, 2000);
  };

  const handleOfflineDownload = () => {
    setOfflineDownloading(true);
    setTimeout(() => {
      setOfflineDownloading(false);
      setOfflineReady(true);
    }, 2500);
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4">
        <div className="flex items-center gap-3 mb-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div className="flex-1">
            <h2 className="font-semibold">Parcours Équilibré</h2>
            <p className="text-sm text-muted-foreground">Paris • 17 Mars 2026</p>
          </div>
          <button className="p-2 hover:bg-muted rounded-full" onClick={handleSave}>
            <Heart className={`w-5 h-5 ${isItineraryLiked(itineraryId) ? "text-primary" : "text-muted-foreground"}`} />
          </button>
        </div>
      </div>

      {/* Map placeholder */}
      <div className="relative h-48 bg-muted border-b border-border">
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="text-center">
            <MapPin className="w-12 h-12 text-primary mx-auto mb-2" />
            <p className="text-sm text-muted-foreground">Carte de l'itinéraire</p>
          </div>
        </div>
        <button className="absolute bottom-4 right-4 bg-white rounded-full p-3 shadow-lg">
          <Navigation className="w-5 h-5 text-primary" />
        </button>
      </div>

      {/* Météo du jour */}
      <div className="p-4 bg-gradient-to-r from-blue-50 to-cyan-50 border-b border-border">
        <div className="flex items-center gap-2 mb-2">
          <Sun className="w-5 h-5 text-amber-500" />
          <h3 className="font-semibold text-sm">Météo du jour — 17 Mars 2026</h3>
        </div>
        <div className="grid grid-cols-4 gap-3 text-center">
          <div>
            <Thermometer className="w-4 h-4 text-red-400 mx-auto mb-1" />
            <p className="font-bold text-sm">14°C</p>
            <p className="text-xs text-muted-foreground">Ressenti 12°</p>
          </div>
          <div>
            <Cloud className="w-4 h-4 text-gray-400 mx-auto mb-1" />
            <p className="font-bold text-sm">Nuageux</p>
            <p className="text-xs text-muted-foreground">Éclaircies PM</p>
          </div>
          <div>
            <Droplets className="w-4 h-4 text-blue-400 mx-auto mb-1" />
            <p className="font-bold text-sm">15%</p>
            <p className="text-xs text-muted-foreground">Pluie</p>
          </div>
          <div>
            <Wind className="w-4 h-4 text-teal-400 mx-auto mb-1" />
            <p className="font-bold text-sm">12 km/h</p>
            <p className="text-xs text-muted-foreground">Vent NO</p>
          </div>
        </div>
        <div className="mt-3 flex gap-2">
          <Badge variant="secondary" className="text-xs bg-amber-100 text-amber-700">
            ☀️ Matin ensoleillé
          </Badge>
          <Badge variant="secondary" className="text-xs bg-gray-100 text-gray-700">
            ☁️ Après-midi nuageux
          </Badge>
        </div>
      </div>

      {/* Summary */}
      <div className="p-4 bg-primary/5 border-b border-border">
        <div className="grid grid-cols-3 gap-4 text-center">
          <div>
            <div className="flex items-center justify-center gap-1 mb-1">
              <Euro className="w-4 h-4 text-primary" />
              <p className="font-bold">{totalCost}€</p>
            </div>
            <p className="text-xs text-muted-foreground">Budget total</p>
          </div>
          <div>
            <div className="flex items-center justify-center gap-1 mb-1">
              <Clock className="w-4 h-4 text-primary" />
              <p className="font-bold">{totalDuration}</p>
            </div>
            <p className="text-xs text-muted-foreground">Durée totale</p>
          </div>
          <div>
            <div className="flex items-center justify-center gap-1 mb-1">
              <MapPin className="w-4 h-4 text-primary" />
              <p className="font-bold">{steps.length}</p>
            </div>
            <p className="text-xs text-muted-foreground">Étapes</p>
          </div>
        </div>
        <div className="mt-3 text-center">
          <Badge variant="secondary" className="text-xs">
            🚶 {walkingTime} de marche
          </Badge>
        </div>
      </div>

      {/* Mode hors-ligne */}
      <div className="p-4 border-b border-border">
        {offlineReady ? (
          <div className="flex items-center gap-3 p-3 bg-green-50 border border-green-200 rounded-lg">
            <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center flex-shrink-0">
              <Check className="w-5 h-5 text-green-600" />
            </div>
            <div className="flex-1">
              <p className="font-medium text-sm text-green-800">Disponible hors-ligne</p>
              <p className="text-xs text-green-600">Carte, étapes et photos sauvegardées (8.2 MB)</p>
            </div>
            <button
              onClick={() => setOfflineReady(false)}
              className="text-xs text-green-600 hover:underline"
            >
              Supprimer
            </button>
          </div>
        ) : offlineDownloading ? (
          <div className="flex items-center gap-3 p-3 bg-primary/5 border border-primary/20 rounded-lg">
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0 animate-pulse">
              <Download className="w-5 h-5 text-primary" />
            </div>
            <div className="flex-1">
              <p className="font-medium text-sm">Téléchargement en cours...</p>
              <div className="mt-1 h-1.5 bg-muted rounded-full overflow-hidden">
                <div className="h-full bg-primary rounded-full animate-[pulse_1.5s_ease-in-out_infinite]" style={{ width: "65%" }} />
              </div>
              <p className="text-xs text-muted-foreground mt-1">Carte, photos et infos des étapes</p>
            </div>
          </div>
        ) : (
          <button
            onClick={handleOfflineDownload}
            className="w-full flex items-center gap-3 p-3 bg-muted/50 border border-border rounded-lg hover:bg-muted transition-colors"
          >
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
              <WifiOff className="w-5 h-5 text-primary" />
            </div>
            <div className="flex-1 text-left">
              <p className="font-medium text-sm">Sauvegarder hors-ligne</p>
              <p className="text-xs text-muted-foreground">Accédez à ce parcours sans connexion (~8 MB)</p>
            </div>
            <Download className="w-5 h-5 text-muted-foreground" />
          </button>
        )}
      </div>

      {/* Steps */}
      <div className="p-4 space-y-4">
        <h3 className="font-semibold">Étapes du parcours</h3>

        <div className="space-y-3">
          {steps.map((step, index) => {
            const Icon = getIcon(step.type);
            const isGalleryExpanded = expandedGallery === step.id;
            return (
              <div key={step.id} className="relative">
                {/* Timeline line */}
                {index < steps.length - 1 && (
                  <div className="absolute left-6 top-14 bottom-0 w-0.5 bg-border -mb-3" />
                )}

                <div className="flex gap-3">
                  {/* Time badge */}
                  <div className="flex flex-col items-center">
                    <div className="w-12 h-12 rounded-full bg-primary flex items-center justify-center text-white font-semibold text-sm">
                      {step.time}
                    </div>
                  </div>

                  {/* Content card */}
                  <div className="flex-1 bg-card rounded-lg border border-border overflow-hidden">
                    <div className="flex gap-3 p-3">
                      <img
                        src={step.image}
                        alt={step.name}
                        className="w-20 h-20 rounded-lg object-cover flex-shrink-0"
                      />
                      <div className="flex-1 min-w-0">
                        <div className="flex items-start gap-2 mb-1">
                          <Icon className="w-4 h-4 text-primary flex-shrink-0 mt-0.5" />
                          <div className="flex-1">
                            <h4 className="font-semibold text-sm">{step.name}</h4>
                            <p className="text-xs text-muted-foreground mt-0.5">
                              {step.description}
                            </p>
                          </div>
                        </div>

                        {/* Créneaux d'ouverture */}
                        <div className="flex items-center gap-1.5 mt-1.5">
                          <Clock className="w-3 h-3 text-muted-foreground flex-shrink-0" />
                          <span className="text-xs text-muted-foreground">{step.openingHours}</span>
                          <Badge
                            variant="secondary"
                            className={`text-[10px] px-1.5 py-0 ${
                              step.isOpenNow
                                ? "bg-green-100 text-green-700"
                                : "bg-red-100 text-red-700"
                            }`}
                          >
                            {step.isOpenNow ? "Ouvert" : "Fermé"}
                          </Badge>
                        </div>

                        <div className="flex items-center gap-3 mt-2 text-xs text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <Clock className="w-3 h-3" />
                            {step.duration}
                          </span>
                          <span className="flex items-center gap-1">
                            <Euro className="w-3 h-3" />
                            {step.cost}€
                          </span>
                          <Badge variant="secondary" className="text-xs">
                            {step.period}
                          </Badge>
                        </div>
                      </div>
                    </div>

                    {/* Gallery toggle */}
                    <button
                      onClick={() => setExpandedGallery(isGalleryExpanded ? null : step.id)}
                      className="w-full flex items-center justify-center gap-2 py-2 px-3 border-t border-border text-xs text-primary hover:bg-primary/5 transition-colors"
                    >
                      <Image className="w-3.5 h-3.5" />
                      <span>
                        {step.gallery.filter((g) => g.type === "photo").length} photos
                        {step.gallery.some((g) => g.type === "video") &&
                          ` • ${step.gallery.filter((g) => g.type === "video").length} vidéo${step.gallery.filter((g) => g.type === "video").length > 1 ? "s" : ""}`}
                      </span>
                      {isGalleryExpanded ? (
                        <ChevronUp className="w-3.5 h-3.5" />
                      ) : (
                        <ChevronDown className="w-3.5 h-3.5" />
                      )}
                    </button>

                    {/* Expanded gallery */}
                    {isGalleryExpanded && (
                      <div className="px-3 pb-3 border-t border-border pt-2">
                        <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
                          {step.gallery.map((item, i) => (
                            <div
                              key={i}
                              className="relative flex-shrink-0 w-24 h-24 rounded-lg overflow-hidden cursor-pointer"
                              onClick={() => setLightboxImage(item)}
                            >
                              <img
                                src={item.url}
                                alt={item.caption || ""}
                                className="w-full h-full object-cover"
                              />
                              {item.type === "video" && (
                                <div className="absolute inset-0 bg-black/30 flex items-center justify-center">
                                  <div className="w-8 h-8 rounded-full bg-white/90 flex items-center justify-center">
                                    <Play className="w-4 h-4 text-foreground ml-0.5" />
                                  </div>
                                </div>
                              )}
                              {item.caption && (
                                <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/60 to-transparent p-1">
                                  <p className="text-[9px] text-white truncate">{item.caption}</p>
                                </div>
                              )}
                            </div>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Bottom actions */}
      <div className="p-4 space-y-3 border-t border-border">
        {/* Save toast */}
        {showSaveToast && (
          <div className="bg-green-50 border border-green-200 rounded-lg p-3 flex items-center gap-2 animate-in fade-in">
            <Heart className="w-4 h-4 text-green-600 fill-green-600" />
            <span className="text-sm text-green-700 font-medium">Parcours sauvegardé dans vos favoris !</span>
          </div>
        )}
        <div className="grid grid-cols-2 gap-3">
          <Button variant="outline" className="gap-2">
            <RotateCw className="w-4 h-4" />
            Regénérer
          </Button>
          <Button
            className="bg-primary hover:bg-primary/90 gap-2"
            onClick={handleSave}
          >
            <Heart
              className={`w-4 h-4 ${
                isItineraryLiked(itineraryId) ? "fill-white" : ""
              }`}
            />
            {isItineraryLiked(itineraryId) ? "Sauvegardé" : "Sauvegarder"}
          </Button>
        </div>
        <div className="grid grid-cols-2 gap-3">
          <Button variant="outline" className="gap-2" onClick={() => setIsShareDialogOpen(true)}>
            <Share2 className="w-4 h-4" />
            Partager
          </Button>
          <Button variant="outline" className="gap-2" onClick={handleExportPdf}>
            <Download className="w-4 h-4" />
            Exporter PDF
          </Button>
        </div>
      </div>

      <BottomNav />

      <ShareDialog
        open={isShareDialogOpen}
        onOpenChange={setIsShareDialogOpen}
        title="Parcours Équilibré - Paris"
        url={currentUrl}
        description="Découvrez mon parcours de visite à Paris avec 6 étapes pour une journée inoubliable !"
      />

      {/* PDF Export Dialog */}
      <Dialog open={showPdfDialog} onOpenChange={setShowPdfDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          {pdfGenerating ? (
            <div className="py-8 text-center">
              <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mx-auto mb-4 animate-pulse">
                <Download className="w-8 h-8 text-primary" />
              </div>
              <DialogTitle className="mb-2">Génération du PDF...</DialogTitle>
              <DialogDescription>
                Préparation de votre itinéraire au format PDF
              </DialogDescription>
              <div className="mt-4 h-2 bg-muted rounded-full overflow-hidden">
                <div className="h-full bg-primary rounded-full animate-[pulse_1s_ease-in-out_infinite]" style={{ width: "70%" }} />
              </div>
            </div>
          ) : pdfReady ? (
            <div className="py-8 text-center">
              <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
                <Download className="w-8 h-8 text-green-600" />
              </div>
              <DialogTitle className="mb-2">PDF prêt !</DialogTitle>
              <DialogDescription>
                Votre itinéraire "Parcours Équilibré - Paris" est prêt au téléchargement.
              </DialogDescription>
              <div className="mt-4 bg-muted rounded-lg p-3 text-left text-sm space-y-1">
                <p><span className="font-medium">Fichier :</span> parcours-paris-2026.pdf</p>
                <p><span className="font-medium">Taille :</span> 2.4 MB</p>
                <p><span className="font-medium">Pages :</span> 3</p>
              </div>
              <div className="mt-4 space-y-2">
                <Button className="w-full bg-primary hover:bg-primary/90 gap-2" onClick={() => setShowPdfDialog(false)}>
                  <Download className="w-4 h-4" />
                  Télécharger le PDF
                </Button>
                <Button variant="outline" className="w-full gap-2" onClick={() => setShowPdfDialog(false)}>
                  <Share2 className="w-4 h-4" />
                  Partager le PDF
                </Button>
              </div>
            </div>
          ) : null}
        </DialogContent>
      </Dialog>

      {/* Lightbox Gallery */}
      <Dialog open={!!lightboxImage} onOpenChange={() => setLightboxImage(null)}>
        <DialogContent className="max-w-[calc(100vw-1rem)] sm:max-w-lg p-0 rounded-xl overflow-hidden bg-black border-none">
          <DialogTitle className="sr-only">
            {lightboxImage?.caption || "Image"}
          </DialogTitle>
          <DialogDescription className="sr-only">
            Aperçu de {lightboxImage?.caption || "l'image"}
          </DialogDescription>
          {lightboxImage && (
            <div className="relative">
              <img
                src={lightboxImage.url}
                alt={lightboxImage.caption || ""}
                className="w-full max-h-[70vh] object-contain"
              />
              {lightboxImage.type === "video" && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="w-16 h-16 rounded-full bg-white/80 flex items-center justify-center">
                    <Play className="w-8 h-8 text-foreground ml-1" />
                  </div>
                </div>
              )}
              {lightboxImage.caption && (
                <div className="absolute bottom-0 inset-x-0 bg-gradient-to-t from-black/80 to-transparent p-4">
                  <p className="text-white text-sm">{lightboxImage.caption}</p>
                </div>
              )}
              <button
                onClick={() => setLightboxImage(null)}
                className="absolute top-3 right-3 w-8 h-8 rounded-full bg-black/50 flex items-center justify-center text-white hover:bg-black/70"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </MobileContainer>
  );
}
