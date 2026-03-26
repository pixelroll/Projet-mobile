import { useState } from "react";
import { useNavigate } from "react-router";
import { ArrowLeft, Upload, Mic, Sparkles, Users, Lock, Globe, Calendar, MapPin, StopCircle } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Label } from "../components/ui/label";
import { Badge } from "../components/ui/badge";
import { useUserMode } from "../contexts/UserModeContext";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogFooter,
} from "../components/ui/dialog";

export function PublishPhoto() {
  const navigate = useNavigate();
  const { isConnected } = useUserMode();
  const [visibility, setVisibility] = useState("public");
  const [isRecording, setIsRecording] = useState(false);
  const [hasVoiceNote, setHasVoiceNote] = useState(false);
  const [newGroupDialogOpen, setNewGroupDialogOpen] = useState(false);
  const [newGroupName, setNewGroupName] = useState("");
  const [tags, setTags] = useState<string[]>(["Monument", "Architecture", "Coucher de soleil"]);
  const [isGeneratingTags, setIsGeneratingTags] = useState(false);
  const [newTag, setNewTag] = useState("");
  const [publishSuccess, setPublishSuccess] = useState(false);

  const handleVoiceRecording = () => {
    if (isRecording) {
      setIsRecording(false);
      setHasVoiceNote(true);
    } else {
      setIsRecording(true);
    }
  };

  const createNewGroup = () => {
    if (newGroupName.trim()) {
      // Mock: would save the group
      setNewGroupDialogOpen(false);
      setNewGroupName("");
    }
  };

  const handleGenerateAITags = () => {
    setIsGeneratingTags(true);
    setTimeout(() => {
      const aiTags = ["Voyage", "Panorama", "Historique", "Culture", "Europe"];
      const newTags = aiTags.filter((t) => !tags.includes(t));
      setTags((prev) => [...prev, ...newTags.slice(0, 3)]);
      setIsGeneratingTags(false);
    }, 1500);
  };

  const removeTag = (tag: string) => {
    setTags((prev) => prev.filter((t) => t !== tag));
  };

  const addTag = () => {
    if (newTag.trim() && !tags.includes(newTag.trim())) {
      setTags((prev) => [...prev, newTag.trim()]);
      setNewTag("");
    }
  };

  const handlePublish = () => {
    setPublishSuccess(true);
    setTimeout(() => {
      setPublishSuccess(false);
      navigate("/home");
    }, 2000);
  };

  // Show warning if not connected
  if (!isConnected) {
    return (
      <MobileContainer className="pb-16">
        <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-semibold">Publier une photo</h2>
        </div>

        <div className="flex flex-col items-center justify-center p-8 text-center min-h-[60vh]">
          <Lock className="w-16 h-16 text-muted-foreground mb-4" />
          <h3 className="font-semibold mb-2">Connexion requise</h3>
          <p className="text-sm text-muted-foreground mb-6">
            Vous devez être connecté pour publier des photos
          </p>
          <Button
            onClick={() => navigate("/home")}
            className="bg-primary hover:bg-primary/90"
          >
            Retour à l'accueil
          </Button>
        </div>

        <BottomNav />
      </MobileContainer>
    );
  }

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="font-semibold">Publier une photo</h2>
      </div>

      <div className="p-4 space-y-6">
        {/* Image picker */}
        <div>
          <Label>Photo</Label>
          <div className="mt-2 border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary cursor-pointer transition-colors">
            <Upload className="w-12 h-12 text-muted-foreground mx-auto mb-2" />
            <p className="text-sm text-muted-foreground">
              Cliquez pour sélectionner une photo
            </p>
            <p className="text-xs text-muted-foreground mt-1">
              JPG, PNG ou HEIC jusqu'à 10MB
            </p>
          </div>
        </div>

        {/* Title */}
        <div>
          <Label htmlFor="title">Titre</Label>
          <Input
            id="title"
            placeholder="Ex: Tour Eiffel au coucher du soleil"
            className="mt-2"
          />
        </div>

        {/* Description */}
        <div>
          <Label htmlFor="description">Description</Label>
          <Textarea
            id="description"
            placeholder="Décrivez votre photo, partagez vos impressions..."
            className="mt-2 resize-none"
            rows={4}
          />
        </div>

        {/* Audio note option */}
        <div>
          <Button
            variant="outline"
            className="w-full gap-2"
            onClick={handleVoiceRecording}
          >
            {isRecording ? (
              <StopCircle className="w-5 h-5" />
            ) : (
              <Mic className="w-5 h-5" />
            )}
            {isRecording ? "Arrêter l'enregistrement" : "Ajouter une note audio"}
          </Button>
          {hasVoiceNote && (
            <div className="mt-2 p-3 bg-muted rounded-lg flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Mic className="w-4 h-4 text-primary" />
                <span className="text-sm">Note audio enregistrée (0:12)</span>
              </div>
              <button
                onClick={() => setHasVoiceNote(false)}
                className="text-sm text-destructive hover:underline"
              >
                Supprimer
              </button>
            </div>
          )}
        </div>

        {/* Date and Period */}
        <div>
          <Label htmlFor="date">Date / Période</Label>
          <div className="mt-2 grid grid-cols-2 gap-2">
            <Input
              id="date"
              type="date"
              placeholder="Date"
              className=""
            />
            <Select>
              <SelectTrigger>
                <SelectValue placeholder="Moment..." />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="morning">Matin</SelectItem>
                <SelectItem value="afternoon">Après-midi</SelectItem>
                <SelectItem value="evening">Soirée</SelectItem>
                <SelectItem value="night">Nuit</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        {/* Travel Information */}
        <div>
          <Label htmlFor="travel-info">Informations de voyage</Label>
          <Textarea
            id="travel-info"
            placeholder="Ajoutez des détails sur votre voyage (durée, activités, conseils...)"
            className="mt-2 resize-none"
            rows={3}
          />
        </div>

        {/* AI tags */}
        <div>
          <div className="flex items-center justify-between mb-2">
            <Label>Tags</Label>
            <button className="flex items-center gap-1 text-sm text-primary hover:underline" onClick={handleGenerateAITags}>
              <Sparkles className={`w-4 h-4 ${isGeneratingTags ? "animate-spin" : ""}`} />
              {isGeneratingTags ? "Generation..." : "Tags automatiques (IA)"}
            </button>
          </div>
          <div className="flex flex-wrap gap-2">
            {tags.map((tag) => (
              <Badge key={tag} variant="secondary" className="cursor-pointer hover:bg-destructive/10 gap-1" onClick={() => removeTag(tag)}>
                {tag}
                <span className="text-muted-foreground hover:text-destructive ml-0.5">×</span>
              </Badge>
            ))}
          </div>
          {/* Add new tag */}
          <div className="flex gap-2 mt-2">
            <Input
              placeholder="Ajouter un tag..."
              value={newTag}
              onChange={(e) => setNewTag(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && addTag()}
              className="flex-1"
            />
            <Button variant="outline" size="sm" onClick={addTag} disabled={!newTag.trim()}>
              +
            </Button>
          </div>
          {isGeneratingTags && (
            <div className="mt-2 bg-primary/5 border border-primary/20 rounded-lg p-3 flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-primary animate-pulse" />
              <span className="text-sm text-primary">Analyse de l'image par IA en cours...</span>
            </div>
          )}
        </div>

        {/* Visibility */}
        <div>
          <Label>Visibilité</Label>
          <div className="mt-2 space-y-2">
            <button
              onClick={() => setVisibility("public")}
              className={`w-full p-4 rounded-lg border-2 text-left transition-colors ${
                visibility === "public"
                  ? "border-primary bg-primary/5"
                  : "border-border hover:border-primary/50"
              }`}
            >
              <div className="flex items-center gap-3">
                <Globe className="w-5 h-5 text-primary" />
                <div className="flex-1">
                  <p className="font-medium">Public</p>
                  <p className="text-sm text-muted-foreground">
                    Visible par tous les utilisateurs
                  </p>
                </div>
              </div>
            </button>

            <button
              onClick={() => setVisibility("group")}
              className={`w-full p-4 rounded-lg border-2 text-left transition-colors ${
                visibility === "group"
                  ? "border-primary bg-primary/5"
                  : "border-border hover:border-primary/50"
              }`}
            >
              <div className="flex items-center gap-3">
                <Users className="w-5 h-5 text-primary" />
                <div className="flex-1">
                  <p className="font-medium">Groupe</p>
                  <p className="text-sm text-muted-foreground">
                    Visible par un groupe spécifique
                  </p>
                </div>
              </div>
            </button>

            <button
              onClick={() => setVisibility("private")}
              className={`w-full p-4 rounded-lg border-2 text-left transition-colors ${
                visibility === "private"
                  ? "border-primary bg-primary/5"
                  : "border-border hover:border-primary/50"
              }`}
            >
              <div className="flex items-center gap-3">
                <Lock className="w-5 h-5 text-primary" />
                <div className="flex-1">
                  <p className="font-medium">Privé</p>
                  <p className="text-sm text-muted-foreground">
                    Visible uniquement par vous
                  </p>
                </div>
              </div>
            </button>
          </div>
        </div>

        {/* Group selector (shown when group is selected) */}
        {visibility === "group" && (
          <div>
            <Label htmlFor="group">Sélectionner un groupe</Label>
            <Select>
              <SelectTrigger className="mt-2">
                <SelectValue placeholder="Choisir un groupe..." />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="family">Famille</SelectItem>
                <SelectItem value="friends">Amis proches</SelectItem>
                <SelectItem value="travel">Groupe voyage Paris 2026</SelectItem>
                <SelectItem value="new">+ Créer un nouveau groupe</SelectItem>
              </SelectContent>
            </Select>
          </div>
        )}

        {/* Location */}
        <div>
          <Label htmlFor="location">Lieu</Label>
          <Input
            id="location"
            placeholder="Ex: Paris, France"
            className="mt-2"
          />
        </div>

        {/* Publish button */}
        {publishSuccess ? (
          <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-center">
            <div className="w-12 h-12 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-2">
              <Upload className="w-6 h-6 text-green-600" />
            </div>
            <p className="font-semibold text-green-700">Photo publiee !</p>
            <p className="text-sm text-green-600">Redirection vers l'accueil...</p>
          </div>
        ) : (
          <Button
            onClick={handlePublish}
            className="w-full bg-primary hover:bg-primary/90 py-6"
          >
            Publier la photo
          </Button>
        )}
      </div>

      <BottomNav />

      {/* New Group Dialog */}
      <Dialog open={newGroupDialogOpen} onOpenChange={setNewGroupDialogOpen}>
        <DialogContent className="sm:max-w-[425px]">
          <DialogHeader>
            <DialogTitle>Créer un nouveau groupe</DialogTitle>
            <DialogDescription>
              Entrez le nom de votre nouveau groupe.
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col space-y-4">
            <Input
              id="name"
              placeholder="Nom du groupe"
              value={newGroupName}
              onChange={(e) => setNewGroupName(e.target.value)}
            />
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setNewGroupDialogOpen(false)}
            >
              Annuler
            </Button>
            <Button type="button" onClick={createNewGroup}>
              Créer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </MobileContainer>
  );
}