import { useState } from "react";
import { useNavigate } from "react-router";
import {
  MapPin,
  Heart,
  Settings,
  Camera,
  Globe,
  Calendar,
  ArrowLeft,
  Share2,
  Grid3x3,
  List,
  Menu,
  Bell,
  Plus,
  LogIn,
  LogOut,
  User as UserIcon,
  ChevronDown,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Badge } from "../components/ui/badge";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../components/ui/tabs";
import { mockTrips } from "../data/trips";
import { useUserMode } from "../contexts/UserModeContext";
import { ShareDialog } from "../components/ShareDialog";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogDescription,
} from "../components/ui/dialog";

interface UserStats {
  posts: number;
  followers: number;
  following: number;
  countriesVisited: number;
}

interface UserPhoto {
  id: number;
  image: string;
  place: string;
  likes: number;
}

const userStats: UserStats = {
  posts: 47,
  followers: 1248,
  following: 532,
  countriesVisited: 12,
};

const userPhotos: UserPhoto[] = [
  {
    id: 1,
    image:
      "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Paris",
    likes: 342,
  },
  {
    id: 2,
    image:
      "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0b2t5byUyMHN0cmVldCUyMG5pZ2h0fGVufDF8fHx8MTc3Mzc1Njk2N3ww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Tokyo",
    likes: 521,
  },
  {
    id: 3,
    image:
      "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYXJjZWxvbmElMjBhcmNoaXRlY3R1cmV8ZW58MXx8fHwxNzczNzM4MzE3fDA&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Barcelone",
    likes: 687,
  },
  {
    id: 4,
    image:
      "https://images.unsplash.com/photo-1514565131-fce0801e5785?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxuZXclMjB5b3JrJTIwY2l0eSUyMHNreWxpbmV8ZW58MXx8fHwxNzczNjg2NDEwfDA&ixlib=rb-4.1.0&q=80&w=1080",
    place: "New York",
    likes: 892,
  },
  {
    id: 5,
    image:
      "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxyb21lJTIwY29sb3NzZXVtfGVufDF8fHx8MTc3MzczMTE3Nnww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Rome",
    likes: 456,
  },
  {
    id: 6,
    image:
      "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYWxpJTIwdGVtcGxlJTIwc3Vuc2V0fGVufDF8fHx8MTc3Mzc0Mzk1M3ww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Bali",
    likes: 734,
  },
  {
    id: 7,
    image:
      "https://images.unsplash.com/photo-1760192686348-1e8432937ff8?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cmF2ZWwlMjBkZXN0aW5hdGlvbiUyMG1vdW50YWluc3xlbnwxfHx8fDE3NzM3NTczOTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Alpes",
    likes: 623,
  },
  {
    id: 8,
    image:
      "https://images.unsplash.com/photo-1724093571967-7fcc7c9afb32?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cmF2ZWwlMjBiZWFjaCUyMHRyb3BpY2FsfGVufDF8fHx8MTc3Mzc1NzM5MHww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Maldives",
    likes: 945,
  },
  {
    id: 9,
    image:
      "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    place: "Londres",
    likes: 412,
  },
];

export function Profile() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState("photos");
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const { isConnected, user, setMode } = useUserMode();
  const [accountMenuOpen, setAccountMenuOpen] = useState(false);
  const [showLoginDialog, setShowLoginDialog] = useState(false);
  const [shareDialogOpen, setShareDialogOpen] = useState(false);

  const handleAddClick = () => {
    if (isConnected) {
      navigate("/publish");
    } else {
      setShowLoginDialog(true);
    }
  };

  const profileUrl = window.location.origin + "/profile";

  return (
    <MobileContainer>
      {/* Header */}
      <div className="sticky top-0 bg-background z-10 border-b border-border px-4 py-3">
        <div className="flex items-center justify-between">
          <Dialog open={accountMenuOpen} onOpenChange={setAccountMenuOpen}>
            <DialogTrigger asChild>
              <button className="flex items-center gap-2 font-semibold text-lg">
                {isConnected ? user?.name || "Jean Dupont" : "Anonyme"}
                <ChevronDown className="w-4 h-4" />
              </button>
            </DialogTrigger>
            <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[320px]">
              <DialogHeader>
                <DialogTitle>Changer de compte</DialogTitle>
                <DialogDescription>
                  Gérez vos comptes et connexions
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-2">
                {isConnected ? (
                  <>
                    <button
                      className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-muted/50"
                      onClick={() => {
                        setAccountMenuOpen(false);
                      }}
                    >
                      <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                        <UserIcon className="w-5 h-5 text-primary" />
                      </div>
                      <div className="flex-1 text-left">
                        <p className="font-medium">{user?.name}</p>
                        <p className="text-sm text-muted-foreground">{user?.email}</p>
                      </div>
                      <div className="w-5 h-5 rounded-full bg-primary flex items-center justify-center">
                        <div className="w-2 h-2 rounded-full bg-white" />
                      </div>
                    </button>
                    <button
                      className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-muted/50 text-destructive"
                      onClick={() => {
                        setMode("anonymous");
                        setAccountMenuOpen(false);
                        navigate("/home");
                      }}
                    >
                      <LogOut className="w-5 h-5" />
                      <span>Se déconnecter</span>
                    </button>
                  </>
                ) : (
                  <button
                    className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-muted/50 text-primary"
                    onClick={() => {
                      setMode("connected");
                      setAccountMenuOpen(false);
                    }}
                  >
                    <LogIn className="w-5 h-5" />
                    <span>Se connecter</span>
                  </button>
                )}
              </div>
            </DialogContent>
          </Dialog>

          <div className="flex items-center gap-3">
            <button onClick={handleAddClick}>
              <Plus className="w-6 h-6" />
            </button>
            <button onClick={() => navigate("/settings")}>
              <Menu className="w-6 h-6" />
            </button>
          </div>
        </div>
      </div>

      {/* Profile content */}
      <div className="px-4 py-4">
        {/* Profile header */}
        <div className="flex items-start gap-4 mb-4">
          {/* Profile picture */}
          <div className="relative">
            <img
              src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&h=200&fit=crop"
              alt="Profile"
              className="w-20 h-20 rounded-full object-cover ring-2 ring-border"
            />
            {isConnected && (
              <button
                onClick={() => navigate("/profile/edit")}
                className="absolute -bottom-1 -right-1 bg-primary text-primary-foreground rounded-full p-1.5 shadow-lg"
              >
                <Camera className="w-3 h-3" />
              </button>
            )}
          </div>

          {/* Stats */}
          <div className="flex-1 flex justify-around">
            <div className="text-center">
              <div className="font-bold">{userStats.posts}</div>
              <div className="text-xs text-muted-foreground">publications</div>
            </div>
            <div className="text-center">
              <div className="font-bold">{userStats.followers}</div>
              <div className="text-xs text-muted-foreground">abonnés</div>
            </div>
            <div className="text-center">
              <div className="font-bold">{userStats.following}</div>
              <div className="text-xs text-muted-foreground">abonnements</div>
            </div>
          </div>
        </div>

        {/* Name and bio */}
        <div className="mb-4">
          <h2 className="font-semibold">{isConnected ? user?.name || "Jean Dupont" : "Voyageur Anonyme"}</h2>
          <p className="text-sm text-muted-foreground mt-1">
            🌍 {userStats.countriesVisited} pays visités
          </p>
          <p className="text-sm mt-1">
            Passionné de voyages et de photographie 📸
          </p>
        </div>

        {/* Action buttons */}
        <div className="flex gap-2 mb-6">
          {isConnected ? (
            <>
              <button
                onClick={() => navigate("/profile/edit")}
                className="flex-1 bg-muted hover:bg-muted/80 py-1.5 rounded-lg text-sm font-semibold transition-colors"
              >
                Modifier le profil
              </button>
              <button
                onClick={() => setShareDialogOpen(true)}
                className="flex-1 bg-muted hover:bg-muted/80 py-1.5 rounded-lg text-sm font-semibold transition-colors"
              >
                Partager le profil
              </button>
            </>
          ) : (
            <button
              onClick={() => setMode("connected")}
              className="flex-1 bg-primary hover:bg-primary/90 text-primary-foreground py-1.5 rounded-lg text-sm font-semibold transition-colors"
            >
              Se connecter
            </button>
          )}
        </div>

        {/* Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="w-full grid grid-cols-2 mb-4">
            <TabsTrigger value="photos" className="gap-2">
              <Grid3x3 className="w-4 h-4" />
              Photos
            </TabsTrigger>
            <TabsTrigger value="trips" className="gap-2">
              <MapPin className="w-4 h-4" />
              Parcours
            </TabsTrigger>
          </TabsList>

          <TabsContent value="photos" className="mt-0">
            <div className="grid grid-cols-3 gap-1">
              {userPhotos.map((photo) => (
                <div
                  key={photo.id}
                  className="aspect-square cursor-pointer relative group"
                  onClick={() => navigate(`/photo/${photo.id}`)}
                >
                  <img
                    src={photo.image}
                    alt={photo.place}
                    className="w-full h-full object-cover"
                  />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/30 transition-colors flex items-center justify-center">
                    <div className="opacity-0 group-hover:opacity-100 transition-opacity flex items-center gap-1 text-white">
                      <Heart className="w-5 h-5 fill-white" />
                      <span className="font-semibold">{photo.likes}</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </TabsContent>

          <TabsContent value="trips" className="mt-0">
            <div className="space-y-3">
              {mockTrips.slice(0, 5).map((trip) => (
                <div
                  key={trip.id}
                  className="flex gap-3 p-3 rounded-lg border border-border cursor-pointer hover:bg-muted/50 transition-colors"
                  onClick={() => navigate(`/travelpath/itinerary/${trip.id}`)}
                >
                  <img
                    src={trip.coverImage}
                    alt={trip.title}
                    className="w-16 h-16 rounded-lg object-cover"
                  />
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold truncate">{trip.title}</h3>
                    <div className="flex items-center gap-1 text-sm text-muted-foreground mt-1">
                      <Calendar className="w-3 h-3" />
                      {trip.duration}
                    </div>
                    <div className="flex items-center gap-1 text-sm text-muted-foreground">
                      <MapPin className="w-3 h-3" />
                      {trip.destination}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </TabsContent>
        </Tabs>
      </div>

      <BottomNav />

      {/* Share Dialog */}
      <ShareDialog
        open={shareDialogOpen}
        onOpenChange={setShareDialogOpen}
        title={`Profil de ${isConnected ? user?.name || "Jean Dupont" : "Anonyme"}`}
        url={profileUrl}
        description="Découvrez mon profil de voyageur sur Traveling !"
      />

      {/* Login Dialog */}
      <Dialog open={showLoginDialog} onOpenChange={setShowLoginDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Connexion requise</DialogTitle>
            <DialogDescription>
              Vous devez être connecté pour publier une photo.
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-3 pt-4">
            <button
              onClick={() => {
                setMode("connected");
                setShowLoginDialog(false);
                navigate("/publish");
              }}
              className="w-full bg-primary hover:bg-primary/90 text-primary-foreground py-2.5 rounded-lg font-semibold transition-colors"
            >
              Se connecter
            </button>
            <button
              onClick={() => setShowLoginDialog(false)}
              className="w-full bg-muted hover:bg-muted/80 py-2.5 rounded-lg font-semibold transition-colors"
            >
              Annuler
            </button>
          </div>
        </DialogContent>
      </Dialog>
    </MobileContainer>
  );
}