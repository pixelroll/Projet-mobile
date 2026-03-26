import { useState } from "react";
import { useNavigate } from "react-router";
import { Bell, MapPin, Heart, MessageCircle, Plus, Share2, Users, Bookmark } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { useUserMode } from "../contexts/UserModeContext";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "../components/ui/dialog";
import { ShareDialog } from "../components/ShareDialog";

interface PhotoPost {
  id: number;
  image: string;
  place: string;
  city: string;
  date: string;
  description: string;
  likes: number;
  comments: number;
  author?: string;
}

const mockPhotos: PhotoPost[] = [
  {
    id: 1,
    image: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Tour Eiffel",
    city: "Paris, France",
    date: "Janvier 2026",
    description: "Une vue magnifique au coucher du soleil",
    likes: 342,
    comments: 28,
    author: "Sophie Martin",
  },
  {
    id: 2,
    image: "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0b2t5byUyMHN0cmVldCUyMG5pZ2h0fGVufDF8fHx8MTc3Mzc1Njk2N3ww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Shibuya Crossing",
    city: "Tokyo, Japon",
    date: "Décembre 2025",
    description: "L'énergie nocturne de Tokyo",
    likes: 521,
    comments: 45,
    author: "Marie Dubois",
  },
  {
    id: 3,
    image: "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYXJjZWxvbmElMjBhcmNoaXRlY3R1cmV8ZW58MXx8fHwxNzczNzM4MzE3fDA&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Sagrada Familia",
    city: "Barcelone, Espagne",
    date: "Mars 2026",
    description: "Architecture spectaculaire de Gaudí",
    likes: 687,
    comments: 62,
    author: "Thomas Martin",
  },
  {
    id: 4,
    image: "https://images.unsplash.com/photo-1514565131-fce0801e5785?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxuZXclMjB5b3JrJTIwY2l0eSUyMHNreWxpbmV8ZW58MXx8fHwxNzczNjg2NDEwfDA&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Manhattan Skyline",
    city: "New York, USA",
    date: "Février 2026",
    description: "Vue emblématique de la grosse pomme",
    likes: 892,
    comments: 73,
    author: "Lucas Petit",
  },
  {
    id: 5,
    image: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxyb21lJTIwY29sb3NzZXVtfGVufDF8fHx8MTc3MzczMTE3Nnww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Colisée",
    city: "Rome, Italie",
    date: "Janvier 2026",
    description: "Monument historique impressionnant",
    likes: 456,
    comments: 31,
    author: "Emma Leroy",
  },
  {
    id: 6,
    image: "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYWxpJTIwdGVtcGxlJTIwc3Vuc2V0fGVufDF8fHx8MTc3Mzc0Mzk1M3ww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Temple Ulun Danu",
    city: "Bali, Indonésie",
    date: "Mars 2026",
    description: "Sérénité au bord du lac",
    likes: 734,
    comments: 54,
    author: "Sophie Martin",
  },
];

export function Home() {
  const navigate = useNavigate();
  const { isConnected, user, setMode } = useUserMode();
  const [showLoginDialog, setShowLoginDialog] = useState(false);
  const [likedPhotos, setLikedPhotos] = useState<Set<number>>(new Set());
  const [savedPhotos, setSavedPhotos] = useState<Set<number>>(new Set());
  const [shareDialogOpen, setShareDialogOpen] = useState(false);
  const [sharePhoto, setSharePhoto] = useState<PhotoPost | null>(null);

  const handleAddClick = () => {
    if (isConnected) {
      navigate("/publish");
    } else {
      setShowLoginDialog(true);
    }
  };

  const toggleLike = (photoId: number) => {
    setLikedPhotos((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(photoId)) {
        newSet.delete(photoId);
      } else {
        newSet.add(photoId);
      }
      return newSet;
    });
  };

  const toggleSave = (photoId: number) => {
    setSavedPhotos((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(photoId)) {
        newSet.delete(photoId);
      } else {
        newSet.add(photoId);
      }
      return newSet;
    });
  };

  const handleShare = (photo: PhotoPost) => {
    setSharePhoto(photo);
    setShareDialogOpen(true);
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header - Instagram style */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-3 flex items-center justify-between">
          {/* Add button - Left - Always visible */}
          <div className="flex items-center gap-1">
            <button
              onClick={handleAddClick}
              className="p-2 hover:bg-muted rounded-lg transition-colors"
            >
              <Plus className="w-6 h-6 text-primary" />
            </button>
            <button
              onClick={() => navigate("/groups")}
              className="p-2 hover:bg-muted rounded-lg transition-colors"
            >
              <Users className="w-5 h-5 text-muted-foreground" />
            </button>
          </div>

          {/* Centered Logo */}
          <h1 className="font-bold text-xl text-primary absolute left-1/2 -translate-x-1/2">
            TravelShare
          </h1>

          {/* Notifications - Right */}
          <button
            onClick={() => navigate("/notifications")}
            className="p-2 hover:bg-muted rounded-lg transition-colors relative"
          >
            <Bell className="w-6 h-6" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full"></span>
          </button>
        </div>
      </div>

      {/* Content Feed - TravelShare */}
      <div className="bg-background">
        <div className="space-y-3 p-3">
          {mockPhotos.map((photo) => (
            <div
              key={photo.id}
              className="bg-white rounded-2xl overflow-hidden shadow-sm border border-border"
            >
              {/* Author Header */}
              <div className="flex items-center gap-3 p-3 border-b border-border">
                <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                  <span className="text-primary font-semibold">
                    {photo.author?.[0] || "?"}
                  </span>
                </div>
                <div className="flex-1">
                  <p className="font-semibold text-sm">{photo.author || "Anonyme"}</p>
                  <div className="flex items-center gap-1 text-xs text-muted-foreground">
                    <MapPin className="w-3 h-3" />
                    <span>{photo.city}</span>
                  </div>
                </div>
                <span className="text-xs text-muted-foreground">{photo.date}</span>
              </div>

              {/* Image */}
              <img
                src={photo.image}
                alt={photo.place}
                className="w-full aspect-square object-cover cursor-pointer"
                onClick={() => navigate(`/photo/${photo.id}`)}
              />

              {/* Actions & Info */}
              <div className="p-3">
                {/* Action Buttons */}
                <div className="flex items-center gap-4 mb-3">
                  <button
                    onClick={() => toggleLike(photo.id)}
                    className="flex items-center gap-1 hover:opacity-70 transition-opacity"
                  >
                    <Heart
                      className={`w-6 h-6 ${
                        likedPhotos.has(photo.id)
                          ? "fill-red-500 text-red-500"
                          : "text-foreground"
                      }`}
                    />
                    <span className="text-sm font-semibold">
                      {likedPhotos.has(photo.id) ? photo.likes + 1 : photo.likes}
                    </span>
                  </button>
                  <button
                    onClick={() => navigate(`/photo/${photo.id}`)}
                    className="flex items-center gap-1 hover:opacity-70 transition-opacity"
                  >
                    <MessageCircle className="w-6 h-6" />
                    <span className="text-sm font-semibold">{photo.comments}</span>
                  </button>
                  <button
                    onClick={() => handleShare(photo)}
                    className="flex items-center gap-1 hover:opacity-70 transition-opacity"
                  >
                    <Share2 className="w-6 h-6" />
                  </button>
                  <button
                    onClick={() => toggleSave(photo.id)}
                    className="flex items-center gap-1 hover:opacity-70 transition-opacity"
                  >
                    <Bookmark
                      className={`w-6 h-6 ${
                        savedPhotos.has(photo.id)
                          ? "fill-yellow-500 text-yellow-500"
                          : "text-foreground"
                      }`}
                    />
                  </button>
                </div>

                {/* Location Badge */}
                <div className="inline-flex items-center gap-1.5 bg-primary/10 text-primary px-3 py-1.5 rounded-full mb-2">
                  <MapPin className="w-4 h-4" />
                  <span className="text-sm font-semibold">{photo.place}</span>
                </div>

                {/* Description */}
                <p className="text-sm text-foreground">
                  <span className="font-semibold mr-1">{photo.author}</span>
                  {photo.description}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Login Required Dialog */}
      <Dialog open={showLoginDialog} onOpenChange={setShowLoginDialog}>
        <DialogContent className="rounded-xl p-[16px]">
          <DialogHeader>
            <DialogTitle>Connexion requise</DialogTitle>
            <DialogDescription>
              Connectez-vous pour publier vos photos.
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-2 pt-1">
            <button
              onClick={() => {
                setMode("connected");
                setShowLoginDialog(false);
              }}
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

      {/* Share Dialog */}
      {sharePhoto && (
        <ShareDialog
          open={shareDialogOpen}
          onOpenChange={setShareDialogOpen}
          title={`${sharePhoto.place} - ${sharePhoto.city}`}
          url={`${window.location.origin}/photo/${sharePhoto.id}`}
          description={`Decouvrez "${sharePhoto.place}" par ${sharePhoto.author} sur Traveling !`}
        />
      )}

      <BottomNav />
    </MobileContainer>
  );
}