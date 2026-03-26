import { useState } from "react";
import { useNavigate } from "react-router";
import { ArrowLeft, Camera, Save } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Label } from "../components/ui/label";
import { useUserMode } from "../contexts/UserModeContext";

export function EditProfile() {
  const navigate = useNavigate();
  const { user } = useUserMode();

  const [formData, setFormData] = useState({
    name: user?.name || "Jean Dupont",
    bio: "🌍 Voyageur passionné | 📸 Photographe amateur",
    location: "Paris, France",
    email: user?.email || "jean.dupont@email.com",
    website: "",
  });

  const [isSaving, setIsSaving] = useState(false);

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = async () => {
    setIsSaving(true);

    // Simuler la sauvegarde
    await new Promise((resolve) => setTimeout(resolve, 1000));

    // Dans une vraie app, on sauvegarderait dans le Context/API
    console.log("Profile updated:", formData);

    setIsSaving(false);
    navigate("/profile");
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="font-semibold flex-1">Modifier le profil</h2>
        <Button
          onClick={handleSave}
          disabled={isSaving}
          size="sm"
          className="bg-primary hover:bg-primary/90"
        >
          <Save className="w-4 h-4 mr-1" />
          {isSaving ? "Enregistrement..." : "Enregistrer"}
        </Button>
      </div>

      <div className="p-4 space-y-6">
        {/* Avatar */}
        <div className="flex flex-col items-center gap-3">
          <div className="relative">
            {user?.avatar ? (
              <img
                src={user.avatar}
                alt="Profile"
                className="w-24 h-24 rounded-full object-cover border-2 border-border"
              />
            ) : (
              <div className="w-24 h-24 rounded-full bg-muted flex items-center justify-center border-2 border-border">
                <span className="text-3xl text-muted-foreground">?</span>
              </div>
            )}
            <button className="absolute bottom-0 right-0 w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center shadow-lg hover:bg-primary/90 transition-colors">
              <Camera className="w-4 h-4" />
            </button>
          </div>
          <p className="text-sm text-muted-foreground">Changer la photo de profil</p>
        </div>

        {/* Form */}
        <div className="space-y-4">
          {/* Name */}
          <div className="space-y-2">
            <Label htmlFor="name">Nom</Label>
            <Input
              id="name"
              value={formData.name}
              onChange={(e) => handleChange("name", e.target.value)}
              placeholder="Votre nom"
            />
          </div>

          {/* Bio */}
          <div className="space-y-2">
            <Label htmlFor="bio">Biographie</Label>
            <Textarea
              id="bio"
              value={formData.bio}
              onChange={(e) => handleChange("bio", e.target.value)}
              placeholder="Parlez de vous..."
              className="resize-none"
              rows={3}
            />
            <p className="text-xs text-muted-foreground">
              {formData.bio.length} / 150 caractères
            </p>
          </div>

          {/* Location */}
          <div className="space-y-2">
            <Label htmlFor="location">Localisation</Label>
            <Input
              id="location"
              value={formData.location}
              onChange={(e) => handleChange("location", e.target.value)}
              placeholder="Ville, Pays"
            />
          </div>

          {/* Email */}
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              value={formData.email}
              onChange={(e) => handleChange("email", e.target.value)}
              placeholder="email@exemple.com"
            />
          </div>

          {/* Website */}
          <div className="space-y-2">
            <Label htmlFor="website">
              Site web <span className="text-muted-foreground">(optionnel)</span>
            </Label>
            <Input
              id="website"
              type="url"
              value={formData.website}
              onChange={(e) => handleChange("website", e.target.value)}
              placeholder="https://votresite.com"
            />
          </div>
        </div>

        {/* Divider */}
        <div className="border-t border-border pt-6">
          <h3 className="font-semibold mb-4">Informations du compte</h3>
          <div className="space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-muted-foreground">Membre depuis</span>
              <span className="font-medium">Janvier 2026</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Photos publiées</span>
              <span className="font-medium">47</span>
            </div>
            <div className="flex justify-between">
              <span className="text-muted-foreground">Parcours créés</span>
              <span className="font-medium">12</span>
            </div>
          </div>
        </div>

        {/* Danger zone */}
        <div className="border-t border-border pt-6">
          <h3 className="font-semibold mb-4 text-destructive">Zone de danger</h3>
          <div className="space-y-2">
            <Button variant="outline" className="w-full text-destructive border-destructive">
              Désactiver le compte
            </Button>
            <Button variant="outline" className="w-full text-destructive border-destructive">
              Supprimer le compte
            </Button>
          </div>
        </div>
      </div>

      <BottomNav />
    </MobileContainer>
  );
}
