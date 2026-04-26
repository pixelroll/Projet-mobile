import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import {
  ArrowLeft,
  Heart,
  MessageCircle,
  Bookmark,
  Send,
  MapPin,
  Users,
  Settings,
  Crown,
  Globe,
  Lock,
  Image as ImageIcon,
  UserPlus,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Badge } from "../components/ui/badge";
import { useUserMode } from "../contexts/UserModeContext";

interface GroupPhoto {
  id: string;
  imageUrl: string;
  location: string;
  country: string;
  likes: number;
  comments: number;
  isLiked: boolean;
  isSaved: boolean;
  authorName: string;
  authorAvatar: string;
  postedAt: string;
  caption?: string;
  groupId?: string;
  groupName?: string;
}

const mockGroupPhotos: GroupPhoto[] = [
  {
    id: "1",
    imageUrl: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800",
    location: "Tour Eiffel",
    country: "Paris, France",
    likes: 24,
    comments: 5,
    isLiked: true,
    isSaved: false,
    authorName: "Sophie Martin",
    authorAvatar: "https://i.pravatar.cc/150?img=1",
    postedAt: "Il y a 2h",
    caption: "Vue magnifique depuis le Trocadero !",
  },
  {
    id: "2",
    imageUrl: "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?w=800",
    location: "Musée du Louvre",
    country: "Paris, France",
    likes: 18,
    comments: 3,
    isLiked: false,
    isSaved: true,
    authorName: "Thomas Dubois",
    authorAvatar: "https://i.pravatar.cc/150?img=2",
    postedAt: "Il y a 5h",
    caption: "La pyramide du Louvre au coucher du soleil",
  },
  {
    id: "3",
    imageUrl: "https://images.unsplash.com/photo-1431274172761-fca41d930114?w=800",
    location: "Arc de Triomphe",
    country: "Paris, France",
    likes: 31,
    comments: 8,
    isLiked: true,
    isSaved: true,
    authorName: "Emma Bernard",
    authorAvatar: "https://i.pravatar.cc/150?img=3",
    postedAt: "Il y a 1j",
    caption: "Champs-Élysées illuminés",
  },
  {
    id: "4",
    imageUrl: "https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=800",
    location: "Montmartre",
    country: "Paris, France",
    likes: 15,
    comments: 2,
    isLiked: false,
    isSaved: false,
    authorName: "Lucas Petit",
    authorAvatar: "https://i.pravatar.cc/150?img=4",
    postedAt: "Il y a 2j",
  },
];

const mockGroup = {
  id: "1",
  name: "Voyage Paris 2026",
  description: "Groupe pour notre voyage a Paris en mars 2026. Partagez vos photos et lieux preferes !",
  members: 8,
  photos: 34,
  isPrivate: false,
  isOwner: true,
  coverImage: "https://images.unsplash.com/photo-1431274172761-fca41d930114?w=400",
  code: "PAR2026",
};

export function GroupFeed() {
  const navigate = useNavigate();
  const { groupId } = useParams();
  const { isConnected } = useUserMode();
  const [photos, setPhotos] = useState(mockGroupPhotos);

  const handleLike = (photoId: string) => {
    setPhotos(
      photos.map((photo) =>
        photo.id === photoId
          ? {
              ...photo,
              isLiked: !photo.isLiked,
              likes: photo.isLiked ? photo.likes - 1 : photo.likes + 1,
            }
          : photo
      )
    );
  };

  const handleSave = (photoId: string) => {
    setPhotos(
      photos.map((photo) =>
        photo.id === photoId ? { ...photo, isSaved: !photo.isSaved } : photo
      )
    );
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div className="flex-1 min-w-0">
            <h2 className="font-semibold truncate">{mockGroup.name}</h2>
            <div className="flex items-center gap-2 text-xs text-muted-foreground">
              <span className="flex items-center gap-1">
                <Users className="w-3 h-3" />
                {mockGroup.members}
              </span>
              <span className="flex items-center gap-1">
                <ImageIcon className="w-3 h-3" />
                {mockGroup.photos}
              </span>
              {mockGroup.isPrivate ? (
                <Badge variant="outline" className="text-xs py-0 h-4">
                  <Lock className="w-2.5 h-2.5 mr-1" />
                  Prive
                </Badge>
              ) : (
                <Badge variant="outline" className="text-xs py-0 h-4">
                  <Globe className="w-2.5 h-2.5 mr-1" />
                  Public
                </Badge>
              )}
            </div>
          </div>
          {mockGroup.isOwner && (
            <button
              onClick={() => navigate(`/groups/${groupId}/admin`)}
              className="p-2 hover:bg-muted rounded-lg transition-colors"
            >
              <Settings className="w-5 h-5 text-muted-foreground" />
            </button>
          )}
        </div>
      </div>

      {/* Group Info Banner */}
      <div className="bg-gradient-to-r from-primary/10 to-primary/5 border-b border-primary/20 p-4">
        <div className="flex items-start gap-3">
          <img
            src={mockGroup.coverImage}
            alt={mockGroup.name}
            className="w-16 h-16 rounded-lg object-cover flex-shrink-0"
          />
          <div className="flex-1 min-w-0">
            <p className="text-sm text-muted-foreground mb-2">
              {mockGroup.description}
            </p>
            <div className="flex gap-2">
              <Button
                size="sm"
                variant="outline"
                className="gap-2 bg-white"
                onClick={() => navigate("/publish", { state: { groupId } })}
              >
                <ImageIcon className="w-4 h-4" />
                Publier
              </Button>
              <Button
                size="sm"
                variant="outline"
                className="gap-2 bg-white"
                onClick={() => navigate(`/groups/${groupId}/admin`)}
              >
                <UserPlus className="w-4 h-4" />
                Inviter
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Photos Feed */}
      <div className="space-y-0">
        {photos.map((photo) => (
          <div key={photo.id} className="bg-white border-b border-border">
            {/* Author Info */}
            <div className="p-3 flex items-center gap-3">
              <img
                src={photo.authorAvatar}
                alt={photo.authorName}
                className="w-10 h-10 rounded-full object-cover"
              />
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-sm truncate">{photo.authorName}</p>
                <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                  <MapPin className="w-3 h-3 flex-shrink-0" />
                  <span className="truncate">{photo.location}</span>
                  <span>·</span>
                  <span className="flex-shrink-0">{photo.postedAt}</span>
                </div>
              </div>
            </div>

            {/* Photo */}
            <img
              src={photo.imageUrl}
              alt={photo.location}
              className="w-full aspect-square object-cover cursor-pointer"
              onClick={() => navigate(`/photo/${photo.id}`)}
            />

            {/* Actions */}
            <div className="p-3 space-y-2">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                  <button
                    onClick={() => handleLike(photo.id)}
                    className="flex items-center gap-1.5 group"
                  >
                    <Heart
                      className={`w-6 h-6 transition-colors ${
                        photo.isLiked
                          ? "fill-red-500 text-red-500"
                          : "text-foreground group-hover:text-red-500"
                      }`}
                    />
                    <span className="text-sm font-medium">{photo.likes}</span>
                  </button>
                  <button
                    onClick={() => navigate(`/photo/${photo.id}`)}
                    className="flex items-center gap-1.5 group"
                  >
                    <MessageCircle className="w-6 h-6 text-foreground group-hover:text-primary transition-colors" />
                    <span className="text-sm font-medium">{photo.comments}</span>
                  </button>
                  <button className="group">
                    <Send className="w-6 h-6 text-foreground group-hover:text-primary transition-colors" />
                  </button>
                </div>
                <button onClick={() => handleSave(photo.id)} className="group">
                  <Bookmark
                    className={`w-6 h-6 transition-colors ${
                      photo.isSaved
                        ? "fill-foreground text-foreground"
                        : "text-foreground group-hover:text-primary"
                    }`}
                  />
                </button>
              </div>

              {/* Caption */}
              {photo.caption && (
                <p className="text-sm">
                  <span className="font-semibold mr-2">{photo.authorName}</span>
                  {photo.caption}
                </p>
              )}

              {/* Location */}
              <button
                onClick={() => navigate(`/map?location=${encodeURIComponent(photo.location)}`)}
                className="flex items-center gap-1.5 text-sm text-primary hover:underline"
              >
                <MapPin className="w-4 h-4" />
                {photo.country}
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Empty State */}
      {photos.length === 0 && (
        <div className="flex flex-col items-center justify-center p-8 text-center min-h-[50vh]">
          <ImageIcon className="w-16 h-16 text-muted-foreground mb-4 opacity-50" />
          <h3 className="font-semibold mb-2">Aucune photo pour le moment</h3>
          <p className="text-sm text-muted-foreground mb-6">
            Soyez le premier a partager une photo dans ce groupe !
          </p>
          <Button
            onClick={() => navigate("/publish", { state: { groupId } })}
            className="bg-primary hover:bg-primary/90 gap-2"
          >
            <ImageIcon className="w-4 h-4" />
            Publier une photo
          </Button>
        </div>
      )}

      <BottomNav />
    </MobileContainer>
  );
}
