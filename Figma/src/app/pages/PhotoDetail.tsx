import { useNavigate, useParams } from "react-router";
import { ArrowLeft, Heart, MessageCircle, Flag, MapPin, Navigation, Share2, Bookmark, Send, Route, ExternalLink } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { Textarea } from "../components/ui/textarea";
import { ReportDialog } from "../components/ReportDialog";
import { ShareDialog } from "../components/ShareDialog";
import { useState } from "react";
import { useUserMode } from "../contexts/UserModeContext";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "../components/ui/dialog";

const photoData: Record<string, { image: string; title: string; author: string; location: string; date: string; description: string; likes: number; tags: string[] }> = {
  "1": {
    image: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    title: "Tour Eiffel au coucher du soleil",
    author: "Sophie Martin",
    location: "Champ de Mars, Paris, France",
    date: "Janvier 2026",
    description: "Une vue magnifique au coucher du soleil depuis le Champ de Mars. L'eclairage dore cree une atmosphere magique autour de ce monument emblematique de Paris. Parfait pour une soiree romantique ou une balade en famille.",
    likes: 342,
    tags: ["Monument", "Coucher de soleil", "Paris", "Architecture"],
  },
  "2": {
    image: "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0b2t5byUyMHN0cmVldCUyMG5pZ2h0fGVufDF8fHx8MTc3Mzc1Njk2N3ww&ixlib=rb-4.1.0&q=80&w=1080",
    title: "Shibuya Crossing de nuit",
    author: "Marie Dubois",
    location: "Shibuya, Tokyo, Japon",
    date: "Decembre 2025",
    description: "L'energie nocturne de Tokyo capturee au celebre carrefour de Shibuya. Des milliers de personnes traversent simultanement dans une danse urbaine fascinante.",
    likes: 521,
    tags: ["Ville", "Nuit", "Tokyo", "Urbain"],
  },
  "3": {
    image: "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYXJjZWxvbmElMjBhcmNoaXRlY3R1cmV8ZW58MXx8fHwxNzczNzM4MzE3fDA&ixlib=rb-4.1.0&q=80&w=1080",
    title: "La Sagrada Familia",
    author: "Thomas Martin",
    location: "Barcelone, Espagne",
    date: "Mars 2026",
    description: "Architecture spectaculaire de Gaudi. Un chef-d'oeuvre inacheve qui continue de fasciner des millions de visiteurs chaque annee.",
    likes: 687,
    tags: ["Architecture", "Monument", "Barcelone", "Art"],
  },
};

export function PhotoDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { isConnected, setMode } = useUserMode();
  const [reportDialogOpen, setReportDialogOpen] = useState(false);
  const [shareDialogOpen, setShareDialogOpen] = useState(false);
  const [showLoginDialog, setShowLoginDialog] = useState(false);
  const [isLiked, setIsLiked] = useState(false);
  const [isSaved, setIsSaved] = useState(false);
  const [commentText, setCommentText] = useState("");
  const [commentSent, setCommentSent] = useState(false);

  const photo = photoData[id || "1"] || photoData["1"];
  const currentLikes = photo.likes + (isLiked ? 1 : 0);

  const handleReport = (reason: string, details: string) => {
    console.log("Photo reported:", { photoId: id, reason, details });
  };

  const handleLike = () => {
    if (!isConnected) {
      setShowLoginDialog(true);
      return;
    }
    setIsLiked(!isLiked);
  };

  const handleSave = () => {
    if (!isConnected) {
      setShowLoginDialog(true);
      return;
    }
    setIsSaved(!isSaved);
  };

  const handleComment = () => {
    if (!isConnected) {
      setShowLoginDialog(true);
      return;
    }
    if (!commentText.trim()) return;
    setCommentSent(true);
    setCommentText("");
    setTimeout(() => setCommentSent(false), 3000);
  };

  const similarPhotos = [
    { id: 7, image: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", place: "Arc de Triomphe" },
    { id: 8, image: "https://images.unsplash.com/photo-1549144511-f099e773c147?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", place: "Notre-Dame" },
    { id: 9, image: "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400", place: "Louvre" },
  ];

  const comments = [
    { id: 1, author: "Marie L.", text: "Magnifique photo ! J'y etais la semaine derniere.", date: "Il y a 2 jours" },
    { id: 2, author: "Thomas B.", text: "Quelle vue ! Merci pour le partage", date: "Il y a 3 jours" },
    { id: 3, author: "Sophie M.", text: "C'est exactement ce que je cherchais pour mon voyage !", date: "Il y a 5 jours" },
  ];

  const currentUrl = window.location.href;

  return (
    <MobileContainer>
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="font-semibold flex-1">Detail de la photo</h2>
        <button onClick={handleSave} className="p-1">
          <Bookmark className={`w-5 h-5 ${isSaved ? "fill-primary text-primary" : "text-muted-foreground"}`} />
        </button>
      </div>

      <div className="pb-6">
        {/* Main photo */}
        <div className="relative">
          <img src={photo.image} alt={photo.title} className="w-full h-80 object-cover" />
          {/* Double-tap like overlay hint */}
        </div>

        {/* Info block */}
        <div className="p-4 space-y-4">
          {/* Author Header */}
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
              <span className="text-primary font-semibold">{photo.author[0]}</span>
            </div>
            <div className="flex-1">
              <p className="font-semibold text-sm">{photo.author}</p>
              <p className="text-xs text-muted-foreground">{photo.date}</p>
            </div>
          </div>

          <div>
            <h1 className="text-xl font-bold mb-2">{photo.title}</h1>
          </div>

          <div className="flex items-start gap-2">
            <MapPin className="w-5 h-5 text-primary flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <p className="font-medium">{photo.location}</p>
              <button onClick={() => navigate("/map")} className="text-sm text-primary hover:underline flex items-center gap-1 mt-1">
                <Navigation className="w-4 h-4" />
                Voir sur la carte
              </button>
              <button
                onClick={() => {
                  // Mock : ouvrirait un Intent Google Maps / app cartographie externe
                  alert("Ouverture de Google Maps vers : " + photo.location);
                }}
                className="text-sm text-primary hover:underline flex items-center gap-1 mt-1"
              >
                <ExternalLink className="w-4 h-4" />
                S'y rendre (Google Maps)
              </button>
            </div>
          </div>

          {/* Créer un itinéraire depuis cette photo */}
          <div className="bg-gradient-to-r from-primary/5 to-accent/5 border border-primary/20 rounded-xl p-4">
            <div className="flex items-start gap-3">
              <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
                <Route className="w-5 h-5 text-primary" />
              </div>
              <div className="flex-1">
                <p className="font-semibold text-sm">Envie de visiter ce lieu ?</p>
                <p className="text-xs text-muted-foreground mt-0.5">
                  Générez un parcours de visite autour de {photo.location.split(",")[0]}
                </p>
                <Button
                  size="sm"
                  className="mt-2 bg-primary hover:bg-primary/90 gap-1.5"
                  onClick={() => navigate("/travelpath/preferences")}
                >
                  <Route className="w-4 h-4" />
                  Créer un parcours
                </Button>
              </div>
            </div>
          </div>

          <div>
            <p className="text-foreground">{photo.description}</p>
          </div>

          {/* Tags */}
          <div className="flex flex-wrap gap-2">
            {photo.tags.map((tag) => (
              <Badge key={tag} variant="secondary">{tag}</Badge>
            ))}
          </div>

          {/* Action buttons - Instagram style */}
          <div className="flex items-center justify-between pt-2 border-t border-border">
            <div className="flex items-center gap-4">
              <button onClick={handleLike} className="flex items-center gap-1.5 hover:opacity-70 transition-opacity">
                <Heart className={`w-6 h-6 transition-colors ${isLiked ? "fill-red-500 text-red-500" : "text-foreground"}`} />
                <span className="text-sm font-semibold">{currentLikes}</span>
              </button>
              <button className="flex items-center gap-1.5 hover:opacity-70 transition-opacity">
                <MessageCircle className="w-6 h-6" />
                <span className="text-sm font-semibold">{comments.length + (commentSent ? 1 : 0)}</span>
              </button>
              <button onClick={() => setShareDialogOpen(true)} className="hover:opacity-70 transition-opacity">
                <Share2 className="w-6 h-6" />
              </button>
            </div>
            <button onClick={() => setReportDialogOpen(true)} className="hover:opacity-70 transition-opacity">
              <Flag className="w-5 h-5 text-muted-foreground" />
            </button>
          </div>

          {/* Like feedback */}
          {isLiked && (
            <p className="text-sm text-muted-foreground">
              Vous et {photo.likes} autres personnes aimez cette photo
            </p>
          )}

          {/* Save feedback */}
          {isSaved && (
            <div className="bg-primary/5 border border-primary/20 rounded-lg p-3 flex items-center gap-2">
              <Bookmark className="w-4 h-4 text-primary fill-primary" />
              <span className="text-sm text-primary font-medium">Photo sauvegardee dans vos favoris</span>
            </div>
          )}

          {/* Comments section */}
          <div className="border-t border-border pt-4 space-y-4">
            <h3 className="font-semibold">Commentaires ({comments.length + (commentSent ? 1 : 0)})</h3>

            {/* Sent comment feedback */}
            {commentSent && (
              <div className="bg-green-50 border border-green-200 rounded-lg p-3 flex items-center gap-2">
                <Send className="w-4 h-4 text-green-600" />
                <span className="text-sm text-green-700">Commentaire publie !</span>
              </div>
            )}

            <div className="space-y-4">
              {comments.map((comment) => (
                <div key={comment.id} className="space-y-1">
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                      <span className="text-sm font-medium text-primary">{comment.author[0]}</span>
                    </div>
                    <div className="flex-1">
                      <p className="font-medium text-sm">{comment.author}</p>
                      <p className="text-xs text-muted-foreground">{comment.date}</p>
                    </div>
                  </div>
                  <p className="text-sm ml-10">{comment.text}</p>
                </div>
              ))}
            </div>

            {/* Add comment */}
            <div className="flex gap-2 items-end">
              <Textarea
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
                placeholder="Ajouter un commentaire..."
                className="resize-none flex-1"
                rows={2}
              />
              <Button
                size="icon"
                onClick={handleComment}
                disabled={!commentText.trim()}
                className="bg-primary hover:bg-primary/90 shrink-0"
              >
                <Send className="w-4 h-4" />
              </Button>
            </div>
          </div>

          {/* Similar photos */}
          <div className="border-t border-border pt-4">
            <h3 className="font-semibold mb-3">Photos similaires</h3>
            <div className="flex gap-3 overflow-x-auto pb-2 scrollbar-hide -mx-4 px-4">
              {similarPhotos.map((p) => (
                <div
                  key={p.id}
                  className="flex-shrink-0 w-40 cursor-pointer"
                  onClick={() => navigate(`/photo/${p.id}`)}
                >
                  <img src={p.image} alt={p.place} className="w-full h-32 object-cover rounded-lg" />
                  <p className="text-sm mt-1">{p.place}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Report dialog */}
      <ReportDialog
        open={reportDialogOpen}
        onOpenChange={setReportDialogOpen}
        photoId={id}
        onReport={handleReport}
      />

      {/* Share dialog */}
      <ShareDialog
        open={shareDialogOpen}
        onOpenChange={setShareDialogOpen}
        title={photo.title}
        url={currentUrl}
        description={`Decouvrez "${photo.title}" par ${photo.author} sur Traveling !`}
      />

      {/* Login dialog */}
      <Dialog open={showLoginDialog} onOpenChange={setShowLoginDialog}>
        <DialogContent className="rounded-xl p-[16px]">
          <DialogHeader>
            <DialogTitle>Connexion requise</DialogTitle>
            <DialogDescription>Connectez-vous pour interagir avec les photos.</DialogDescription>
          </DialogHeader>
          <div className="space-y-2 pt-1">
            <button
              onClick={() => { setMode("connected"); setShowLoginDialog(false); }}
              className="w-full bg-primary hover:bg-primary/90 text-white py-2.5 rounded-lg font-semibold text-sm"
            >
              Se connecter
            </button>
            <button
              onClick={() => setShowLoginDialog(false)}
              className="w-full bg-muted hover:bg-muted/80 py-2.5 rounded-lg font-semibold text-sm"
            >
              Annuler
            </button>
          </div>
        </DialogContent>
      </Dialog>
    </MobileContainer>
  );
}