import { useState } from "react";
import { useNavigate } from "react-router";
import {
  ArrowLeft,
  Plus,
  Users,
  Lock,
  Globe,
  Search,
  UserPlus,
  Settings,
  Crown,
  Check,
  Copy,
  Camera,
  MapPin,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Textarea } from "../components/ui/textarea";
import { Label } from "../components/ui/label";
import { Badge } from "../components/ui/badge";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "../components/ui/dialog";
import { useUserMode } from "../contexts/UserModeContext";

interface Group {
  id: string;
  name: string;
  description: string;
  members: number;
  photos: number;
  isPrivate: boolean;
  isJoined: boolean;
  isOwner: boolean;
  coverImage: string;
  code?: string;
}

const mockGroups: Group[] = [
  {
    id: "1",
    name: "Voyage Paris 2026",
    description: "Groupe pour notre voyage a Paris en mars 2026. Partagez vos photos et lieux preferes !",
    members: 8,
    photos: 34,
    isPrivate: false,
    isJoined: true,
    isOwner: true,
    coverImage: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    code: "PAR2026",
  },
  {
    id: "2",
    name: "Famille Martin",
    description: "Photos de voyages en famille",
    members: 5,
    photos: 67,
    isPrivate: true,
    isJoined: true,
    isOwner: false,
    coverImage: "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    code: "FAM-MTN",
  },
  {
    id: "3",
    name: "Amis proches",
    description: "Nos aventures entre amis",
    members: 12,
    photos: 89,
    isPrivate: true,
    isJoined: true,
    isOwner: false,
    coverImage: "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  },
];

const discoverGroups: Group[] = [
  {
    id: "4",
    name: "Backpackers Europe",
    description: "Communaute de voyageurs sac a dos en Europe",
    members: 234,
    photos: 1250,
    isPrivate: false,
    isJoined: false,
    isOwner: false,
    coverImage: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  },
  {
    id: "5",
    name: "Photo Nature",
    description: "Les plus beaux paysages naturels du monde",
    members: 567,
    photos: 3400,
    isPrivate: false,
    isJoined: false,
    isOwner: false,
    coverImage: "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  },
  {
    id: "6",
    name: "Street Food World",
    description: "Les meilleurs spots de street food",
    members: 189,
    photos: 890,
    isPrivate: false,
    isJoined: false,
    isOwner: false,
    coverImage: "https://images.unsplash.com/photo-1514565131-fce0801e5785?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
  },
];

export function Groups() {
  const navigate = useNavigate();
  const { isConnected, setMode } = useUserMode();
  const [activeTab, setActiveTab] = useState("my-groups");
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [joinDialogOpen, setJoinDialogOpen] = useState(false);
  const [groupDetailOpen, setGroupDetailOpen] = useState<Group | null>(null);
  const [joinedGroups, setJoinedGroups] = useState<Set<string>>(new Set());
  const [copiedCode, setCopiedCode] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");

  // Create group form
  const [newGroupName, setNewGroupName] = useState("");
  const [newGroupDesc, setNewGroupDesc] = useState("");
  const [newGroupPrivate, setNewGroupPrivate] = useState(false);
  const [groupCreated, setGroupCreated] = useState(false);

  // Join group
  const [joinCode, setJoinCode] = useState("");
  const [joinSuccess, setJoinSuccess] = useState(false);

  const handleCreateGroup = () => {
    if (!newGroupName.trim()) return;
    setGroupCreated(true);
    setTimeout(() => {
      setCreateDialogOpen(false);
      setGroupCreated(false);
      setNewGroupName("");
      setNewGroupDesc("");
      setNewGroupPrivate(false);
    }, 2000);
  };

  const handleJoinByCode = () => {
    if (!joinCode.trim()) return;
    setJoinSuccess(true);
    setTimeout(() => {
      setJoinDialogOpen(false);
      setJoinSuccess(false);
      setJoinCode("");
    }, 2000);
  };

  const handleJoinGroup = (groupId: string) => {
    setJoinedGroups((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(groupId)) {
        newSet.delete(groupId);
      } else {
        newSet.add(groupId);
      }
      return newSet;
    });
  };

  const handleCopyCode = (code: string) => {
    try {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(code);
      } else {
        const textArea = document.createElement("textarea");
        textArea.value = code;
        textArea.style.position = "fixed";
        textArea.style.left = "-999999px";
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand("copy");
        document.body.removeChild(textArea);
      }
      setCopiedCode(true);
      setTimeout(() => setCopiedCode(false), 2000);
    } catch (e) {
      console.error("Copy failed", e);
    }
  };

  if (!isConnected) {
    return (
      <MobileContainer className="pb-16">
        <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-semibold">Groupes</h2>
        </div>
        <div className="flex flex-col items-center justify-center p-8 text-center min-h-[60vh]">
          <Users className="w-16 h-16 text-muted-foreground mb-4 opacity-50" />
          <h3 className="font-semibold mb-2">Connexion requise</h3>
          <p className="text-sm text-muted-foreground mb-6">
            Connectez-vous pour creer ou rejoindre des groupes
          </p>
          <Button onClick={() => setMode("connected")} className="bg-primary hover:bg-primary/90">
            Se connecter
          </Button>
        </div>
        <BottomNav />
      </MobileContainer>
    );
  }

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-semibold flex-1">Groupes</h2>
          <button
            onClick={() => setJoinDialogOpen(true)}
            className="p-2 hover:bg-muted rounded-lg transition-colors"
          >
            <UserPlus className="w-5 h-5 text-primary" />
          </button>
          <button
            onClick={() => setCreateDialogOpen(true)}
            className="p-2 hover:bg-muted rounded-lg transition-colors"
          >
            <Plus className="w-5 h-5 text-primary" />
          </button>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="w-full rounded-none bg-white border-b grid grid-cols-2">
            <TabsTrigger value="my-groups">Mes groupes</TabsTrigger>
            <TabsTrigger value="discover">Decouvrir</TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsContent value="my-groups" className="mt-0">
          <div className="p-4 space-y-3">
            {mockGroups.map((group) => (
              <div
                key={group.id}
                className="bg-white rounded-xl border border-border overflow-hidden cursor-pointer hover:border-primary/50 transition-colors"
                onClick={() => setGroupDetailOpen(group)}
              >
                <div className="flex gap-3 p-3">
                  <img
                    src={group.coverImage}
                    alt={group.name}
                    className="w-16 h-16 rounded-lg object-cover flex-shrink-0"
                  />
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold truncate">{group.name}</h3>
                      {group.isOwner && (
                        <Crown className="w-4 h-4 text-amber-500 flex-shrink-0" />
                      )}
                    </div>
                    <p className="text-sm text-muted-foreground truncate mt-0.5">
                      {group.description}
                    </p>
                    <div className="flex items-center gap-3 mt-1.5">
                      <span className="text-xs text-muted-foreground flex items-center gap-1">
                        <Users className="w-3 h-3" />
                        {group.members}
                      </span>
                      <span className="text-xs text-muted-foreground flex items-center gap-1">
                        <Camera className="w-3 h-3" />
                        {group.photos}
                      </span>
                      {group.isPrivate ? (
                        <Badge variant="outline" className="text-xs py-0 h-5">
                          <Lock className="w-3 h-3 mr-1" />
                          Prive
                        </Badge>
                      ) : (
                        <Badge variant="outline" className="text-xs py-0 h-5">
                          <Globe className="w-3 h-3 mr-1" />
                          Public
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="discover" className="mt-0">
          <div className="p-4 space-y-4">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
              <Input
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Rechercher un groupe..."
                className="pl-10"
              />
            </div>

            {/* Discover list */}
            <div className="space-y-3">
              {discoverGroups.map((group) => {
                const hasJoined = joinedGroups.has(group.id);
                return (
                  <div
                    key={group.id}
                    className="bg-white rounded-xl border border-border overflow-hidden"
                  >
                    <div className="h-24 relative">
                      <img
                        src={group.coverImage}
                        alt={group.name}
                        className="w-full h-full object-cover"
                      />
                      <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
                      <div className="absolute bottom-2 left-3 right-3">
                        <h3 className="font-semibold text-white">{group.name}</h3>
                      </div>
                    </div>
                    <div className="p-3">
                      <p className="text-sm text-muted-foreground mb-2">
                        {group.description}
                      </p>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center gap-3 text-xs text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <Users className="w-3 h-3" />
                            {group.members} membres
                          </span>
                          <span className="flex items-center gap-1">
                            <Camera className="w-3 h-3" />
                            {group.photos} photos
                          </span>
                        </div>
                        <Button
                          size="sm"
                          variant={hasJoined ? "outline" : "default"}
                          className={hasJoined ? "" : "bg-primary hover:bg-primary/90"}
                          onClick={() => handleJoinGroup(group.id)}
                        >
                          {hasJoined ? (
                            <>
                              <Check className="w-4 h-4 mr-1" />
                              Rejoint
                            </>
                          ) : (
                            <>
                              <UserPlus className="w-4 h-4 mr-1" />
                              Rejoindre
                            </>
                          )}
                        </Button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </TabsContent>
      </Tabs>

      {/* Create Group Dialog */}
      <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          {!groupCreated ? (
            <>
              <DialogHeader>
                <DialogTitle className="flex items-center gap-2">
                  <Plus className="w-5 h-5 text-primary" />
                  Creer un groupe
                </DialogTitle>
                <DialogDescription>
                  Creez un groupe pour partager des photos avec vos proches
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div>
                  <Label htmlFor="group-name">Nom du groupe</Label>
                  <Input
                    id="group-name"
                    value={newGroupName}
                    onChange={(e) => setNewGroupName(e.target.value)}
                    placeholder="Ex: Voyage Italie 2026"
                    className="mt-1"
                  />
                </div>
                <div>
                  <Label htmlFor="group-desc">Description</Label>
                  <Textarea
                    id="group-desc"
                    value={newGroupDesc}
                    onChange={(e) => setNewGroupDesc(e.target.value)}
                    placeholder="Decrivez l'objectif du groupe..."
                    className="mt-1 resize-none"
                    rows={3}
                  />
                </div>
                <div>
                  <Label className="mb-2 block">Visibilite</Label>
                  <div className="space-y-2">
                    <button
                      onClick={() => setNewGroupPrivate(false)}
                      className={`w-full p-3 rounded-lg border-2 text-left transition-colors flex items-center gap-3 ${
                        !newGroupPrivate ? "border-primary bg-primary/5" : "border-border"
                      }`}
                    >
                      <Globe className="w-5 h-5 text-primary" />
                      <div>
                        <p className="font-medium text-sm">Public</p>
                        <p className="text-xs text-muted-foreground">
                          Tout le monde peut trouver et rejoindre
                        </p>
                      </div>
                    </button>
                    <button
                      onClick={() => setNewGroupPrivate(true)}
                      className={`w-full p-3 rounded-lg border-2 text-left transition-colors flex items-center gap-3 ${
                        newGroupPrivate ? "border-primary bg-primary/5" : "border-border"
                      }`}
                    >
                      <Lock className="w-5 h-5 text-primary" />
                      <div>
                        <p className="font-medium text-sm">Prive</p>
                        <p className="text-xs text-muted-foreground">
                          Accessible uniquement par code d'invitation
                        </p>
                      </div>
                    </button>
                  </div>
                </div>
              </div>
              <DialogFooter className="flex-col sm:flex-row gap-2">
                <Button variant="outline" onClick={() => setCreateDialogOpen(false)} className="w-full sm:w-auto">
                  Annuler
                </Button>
                <Button
                  onClick={handleCreateGroup}
                  disabled={!newGroupName.trim()}
                  className="w-full sm:w-auto bg-primary hover:bg-primary/90"
                >
                  Creer le groupe
                </Button>
              </DialogFooter>
            </>
          ) : (
            <div className="py-8 text-center">
              <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
                <Check className="w-8 h-8 text-green-600" />
              </div>
              <DialogTitle className="mb-2">Groupe cree !</DialogTitle>
              <DialogDescription>
                Votre groupe "{newGroupName}" a ete cree avec succes. Partagez le code d'invitation avec vos amis.
              </DialogDescription>
              <div className="mt-4 bg-muted rounded-lg p-3 flex items-center justify-center gap-2">
                <span className="font-mono font-bold text-lg tracking-wider">GRP-{Math.random().toString(36).substring(2, 6).toUpperCase()}</span>
                <Copy className="w-4 h-4 text-muted-foreground" />
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Join Group by Code Dialog */}
      <Dialog open={joinDialogOpen} onOpenChange={setJoinDialogOpen}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          {!joinSuccess ? (
            <>
              <DialogHeader>
                <DialogTitle className="flex items-center gap-2">
                  <UserPlus className="w-5 h-5 text-primary" />
                  Rejoindre un groupe
                </DialogTitle>
                <DialogDescription>
                  Entrez le code d'invitation pour rejoindre un groupe prive
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div>
                  <Label htmlFor="join-code">Code d'invitation</Label>
                  <Input
                    id="join-code"
                    value={joinCode}
                    onChange={(e) => setJoinCode(e.target.value.toUpperCase())}
                    placeholder="Ex: PAR2026"
                    className="mt-1 font-mono text-center text-lg tracking-wider"
                    maxLength={10}
                  />
                </div>
              </div>
              <DialogFooter className="flex-col sm:flex-row gap-2">
                <Button variant="outline" onClick={() => setJoinDialogOpen(false)} className="w-full sm:w-auto">
                  Annuler
                </Button>
                <Button
                  onClick={handleJoinByCode}
                  disabled={!joinCode.trim()}
                  className="w-full sm:w-auto bg-primary hover:bg-primary/90"
                >
                  Rejoindre
                </Button>
              </DialogFooter>
            </>
          ) : (
            <div className="py-8 text-center">
              <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
                <Check className="w-8 h-8 text-green-600" />
              </div>
              <DialogTitle className="mb-2">Groupe rejoint !</DialogTitle>
              <DialogDescription>
                Vous avez rejoint le groupe avec succes. Vous pouvez maintenant voir et partager des photos.
              </DialogDescription>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Group Detail Dialog */}
      <Dialog open={!!groupDetailOpen} onOpenChange={() => setGroupDetailOpen(null)}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl p-0 overflow-hidden">
          {groupDetailOpen && (
            <>
              <div className="h-32 relative">
                <img
                  src={groupDetailOpen.coverImage}
                  alt={groupDetailOpen.name}
                  className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent" />
                <div className="absolute bottom-3 left-4 right-4">
                  <div className="flex items-center gap-2">
                    <h3 className="font-bold text-white text-lg">{groupDetailOpen.name}</h3>
                    {groupDetailOpen.isOwner && (
                      <Crown className="w-4 h-4 text-amber-400" />
                    )}
                  </div>
                </div>
              </div>
              <div className="p-4 space-y-4">
                <p className="text-sm text-muted-foreground">
                  {groupDetailOpen.description}
                </p>

                <div className="grid grid-cols-3 gap-4 text-center py-2">
                  <div>
                    <p className="font-bold">{groupDetailOpen.members}</p>
                    <p className="text-xs text-muted-foreground">Membres</p>
                  </div>
                  <div>
                    <p className="font-bold">{groupDetailOpen.photos}</p>
                    <p className="text-xs text-muted-foreground">Photos</p>
                  </div>
                  <div>
                    <p className="font-bold">{groupDetailOpen.isPrivate ? "Prive" : "Public"}</p>
                    <p className="text-xs text-muted-foreground">Type</p>
                  </div>
                </div>

                {/* Invite code */}
                {groupDetailOpen.code && (
                  <div className="bg-muted rounded-lg p-3">
                    <p className="text-xs text-muted-foreground mb-1">Code d'invitation</p>
                    <div className="flex items-center justify-between">
                      <span className="font-mono font-bold tracking-wider">
                        {groupDetailOpen.code}
                      </span>
                      <Button
                        size="sm"
                        variant="ghost"
                        onClick={() => handleCopyCode(groupDetailOpen.code!)}
                      >
                        {copiedCode ? (
                          <Check className="w-4 h-4 text-green-600" />
                        ) : (
                          <Copy className="w-4 h-4" />
                        )}
                      </Button>
                    </div>
                  </div>
                )}

                {/* Members preview */}
                <div>
                  <p className="text-sm font-semibold mb-2">Membres</p>
                  <div className="flex -space-x-2">
                    {Array.from({ length: Math.min(groupDetailOpen.members, 6) }).map((_, i) => (
                      <div
                        key={i}
                        className="w-8 h-8 rounded-full bg-primary/10 border-2 border-white flex items-center justify-center"
                      >
                        <span className="text-xs font-medium text-primary">
                          {String.fromCharCode(65 + i)}
                        </span>
                      </div>
                    ))}
                    {groupDetailOpen.members > 6 && (
                      <div className="w-8 h-8 rounded-full bg-muted border-2 border-white flex items-center justify-center">
                        <span className="text-xs font-medium text-muted-foreground">
                          +{groupDetailOpen.members - 6}
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex gap-2 pt-2">
                  {groupDetailOpen.isOwner && (
                    <Button variant="outline" className="flex-1 gap-2">
                      <Settings className="w-4 h-4" />
                      Gerer
                    </Button>
                  )}
                  <Button
                    className="flex-1 gap-2 bg-primary hover:bg-primary/90"
                    onClick={() => {
                      setGroupDetailOpen(null);
                      navigate("/home");
                    }}
                  >
                    <Camera className="w-4 h-4" />
                    Voir photos
                  </Button>
                </div>
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>

      <BottomNav />
    </MobileContainer>
  );
}
