import { useState } from "react";
import { useNavigate, useParams } from "react-router";
import {
  ArrowLeft,
  Users,
  Shield,
  Settings,
  TrendingUp,
  Crown,
  UserCheck,
  UserX,
  MoreVertical,
  AlertCircle,
  Image,
  Trash2,
  RefreshCw,
  Copy,
  Check,
  Eye,
  EyeOff,
  Globe,
  Lock,
  Upload,
  X,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuSeparator,
} from "../components/ui/dropdown-menu";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";

interface Member {
  id: string;
  name: string;
  avatar: string;
  role: "owner" | "admin" | "moderator" | "member";
  joinedAt: string;
  photosCount: number;
  lastActive: string;
}

interface ReportedPhoto {
  id: string;
  photoUrl: string;
  reportedBy: string;
  reason: string;
  reportedAt: string;
  status: "pending" | "resolved" | "dismissed";
}

const mockMembers: Member[] = [
  {
    id: "1",
    name: "Sophie Martin",
    avatar: "https://i.pravatar.cc/150?img=1",
    role: "owner",
    joinedAt: "2026-01-15",
    photosCount: 45,
    lastActive: "Il y a 2h",
  },
  {
    id: "2",
    name: "Thomas Dubois",
    avatar: "https://i.pravatar.cc/150?img=2",
    role: "admin",
    joinedAt: "2026-01-16",
    photosCount: 32,
    lastActive: "Il y a 5h",
  },
  {
    id: "3",
    name: "Emma Bernard",
    avatar: "https://i.pravatar.cc/150?img=3",
    role: "moderator",
    joinedAt: "2026-01-18",
    photosCount: 28,
    lastActive: "Il y a 1j",
  },
  {
    id: "4",
    name: "Lucas Petit",
    avatar: "https://i.pravatar.cc/150?img=4",
    role: "member",
    joinedAt: "2026-02-01",
    photosCount: 12,
    lastActive: "Il y a 3h",
  },
  {
    id: "5",
    name: "Chloe Moreau",
    avatar: "https://i.pravatar.cc/150?img=5",
    role: "member",
    joinedAt: "2026-02-05",
    photosCount: 8,
    lastActive: "Il y a 1j",
  },
];

const mockReports: ReportedPhoto[] = [
  {
    id: "1",
    photoUrl: "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=400",
    reportedBy: "Lucas Petit",
    reason: "Contenu inapproprie",
    reportedAt: "2026-04-25",
    status: "pending",
  },
  {
    id: "2",
    photoUrl: "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=400",
    reportedBy: "Emma Bernard",
    reason: "Spam",
    reportedAt: "2026-04-24",
    status: "pending",
  },
];

export function GroupAdmin() {
  const navigate = useNavigate();
  const { groupId } = useParams();
  const [activeTab, setActiveTab] = useState("members");
  const [groupName, setGroupName] = useState("Voyage Paris 2026");
  const [groupDesc, setGroupDesc] = useState(
    "Groupe pour notre voyage a Paris en mars 2026. Partagez vos photos et lieux preferes !"
  );
  const [groupVisibility, setGroupVisibility] = useState<"public" | "private">("public");
  const [requireApproval, setRequireApproval] = useState(false);
  const [inviteCode, setInviteCode] = useState("PAR2026");
  const [copiedCode, setCopiedCode] = useState(false);
  const [selectedMember, setSelectedMember] = useState<Member | null>(null);
  const [changeRoleDialog, setChangeRoleDialog] = useState(false);
  const [removeMemberDialog, setRemoveMemberDialog] = useState(false);
  const [deleteGroupDialog, setDeleteGroupDialog] = useState(false);
  const [regenerateCodeDialog, setRegenerateCodeDialog] = useState(false);
  const [selectedPhoto, setSelectedPhoto] = useState<ReportedPhoto | null>(null);
  const [moderationDialog, setModerationDialog] = useState(false);
  const [inviteDialog, setInviteDialog] = useState(false);

  const handleCopyCode = () => {
    try {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(inviteCode);
      } else {
        const textArea = document.createElement("textarea");
        textArea.value = inviteCode;
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

  const handleRegenerateCode = () => {
    setInviteCode(`PAR-${Math.random().toString(36).substring(2, 6).toUpperCase()}`);
    setRegenerateCodeDialog(false);
  };

  const getRoleBadge = (role: Member["role"]) => {
    switch (role) {
      case "owner":
        return (
          <Badge className="bg-amber-100 text-amber-700 hover:bg-amber-100 gap-1">
            <Crown className="w-3 h-3" />
            Proprietaire
          </Badge>
        );
      case "admin":
        return (
          <Badge className="bg-blue-100 text-blue-700 hover:bg-blue-100 gap-1">
            <Shield className="w-3 h-3" />
            Admin
          </Badge>
        );
      case "moderator":
        return (
          <Badge className="bg-purple-100 text-purple-700 hover:bg-purple-100 gap-1">
            <Shield className="w-3 h-3" />
            Moderateur
          </Badge>
        );
      default:
        return (
          <Badge variant="outline" className="gap-1">
            <Users className="w-3 h-3" />
            Membre
          </Badge>
        );
    }
  };

  return (
    <MobileContainer>
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <div className="flex-1">
            <h2 className="font-semibold">Administration</h2>
            <p className="text-xs text-muted-foreground">{groupName}</p>
          </div>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="w-full rounded-none bg-white border-b grid grid-cols-4">
            <TabsTrigger value="members" className="text-xs gap-1">
              <Users className="w-4 h-4" />
              Membres
            </TabsTrigger>
            <TabsTrigger value="moderation" className="text-xs gap-1">
              <Shield className="w-4 h-4" />
              Moderation
            </TabsTrigger>
            <TabsTrigger value="settings" className="text-xs gap-1">
              <Settings className="w-4 h-4" />
              Parametres
            </TabsTrigger>
            <TabsTrigger value="stats" className="text-xs gap-1">
              <TrendingUp className="w-4 h-4" />
              Stats
            </TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        {/* Members Tab */}
        <TabsContent value="members" className="mt-0">
          <div className="p-4 space-y-3">
            <div className="flex items-center justify-between mb-2">
              <p className="text-sm text-muted-foreground">{mockMembers.length} membres</p>
              <Button
                size="sm"
                variant="outline"
                className="gap-2"
                onClick={() => setInviteDialog(true)}
              >
                <UserCheck className="w-4 h-4" />
                Inviter
              </Button>
            </div>

            {mockMembers.map((member) => (
              <div
                key={member.id}
                className="bg-white rounded-xl border border-border p-3 flex items-center gap-3"
              >
                <img
                  src={member.avatar}
                  alt={member.name}
                  className="w-12 h-12 rounded-full object-cover"
                />
                <div className="flex-1 min-w-0">
                  <h3 className="font-semibold truncate">{member.name}</h3>
                  <p className="text-xs text-muted-foreground">
                    {member.photosCount} photos · {member.lastActive}
                  </p>
                  <div className="mt-1">{getRoleBadge(member.role)}</div>
                </div>
                {member.role !== "owner" && (
                  <DropdownMenu modal={false}>
                    <DropdownMenuTrigger asChild>
                      <button className="p-2 hover:bg-muted rounded-lg transition-colors">
                        <MoreVertical className="w-4 h-4 text-muted-foreground" />
                      </button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-48">
                      <DropdownMenuItem
                        onSelect={() => {
                          setSelectedMember(member);
                          setChangeRoleDialog(true);
                        }}
                      >
                        <Shield className="w-4 h-4 mr-2" />
                        Changer le role
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onSelect={() => {
                          navigate(`/profile/${member.id}`);
                        }}
                      >
                        <Eye className="w-4 h-4 mr-2" />
                        Voir le profil
                      </DropdownMenuItem>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem
                        onSelect={() => {
                          setSelectedMember(member);
                          setRemoveMemberDialog(true);
                        }}
                        className="text-red-600 focus:text-red-600"
                      >
                        <UserX className="w-4 h-4 mr-2" />
                        Retirer du groupe
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                )}
              </div>
            ))}
          </div>
        </TabsContent>

        {/* Moderation Tab */}
        <TabsContent value="moderation" className="mt-0">
          <div className="p-4 space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-semibold">Photos signalees</h3>
                <p className="text-xs text-muted-foreground">
                  {mockReports.filter((r) => r.status === "pending").length} en attente
                </p>
              </div>
              <Badge variant="outline" className="bg-orange-50 text-orange-700 border-orange-200">
                {mockReports.filter((r) => r.status === "pending").length} nouveau
              </Badge>
            </div>

            <div className="space-y-3">
              {mockReports.map((report) => (
                <div
                  key={report.id}
                  className="bg-white rounded-xl border border-border overflow-hidden"
                >
                  <img
                    src={report.photoUrl}
                    alt="Photo signalée"
                    className="w-full h-48 object-cover"
                  />
                  <div className="p-3 space-y-2">
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex-1">
                        <p className="text-sm font-medium">{report.reason}</p>
                        <p className="text-xs text-muted-foreground">
                          Signale par {report.reportedBy}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          Le {new Date(report.reportedAt).toLocaleDateString("fr-FR")}
                        </p>
                      </div>
                      <Badge
                        variant={
                          report.status === "pending"
                            ? "default"
                            : report.status === "resolved"
                            ? "outline"
                            : "secondary"
                        }
                        className={
                          report.status === "pending"
                            ? "bg-orange-100 text-orange-700 hover:bg-orange-100"
                            : ""
                        }
                      >
                        {report.status === "pending"
                          ? "En attente"
                          : report.status === "resolved"
                          ? "Resolu"
                          : "Rejete"}
                      </Badge>
                    </div>
                    {report.status === "pending" && (
                      <div className="flex gap-2 pt-2">
                        <Button
                          variant="outline"
                          size="sm"
                          className="flex-1 text-red-600 border-red-200 hover:bg-red-50"
                          onClick={() => {
                            setSelectedPhoto(report);
                            setModerationDialog(true);
                          }}
                        >
                          <Trash2 className="w-4 h-4 mr-1" />
                          Supprimer
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          className="flex-1"
                          onClick={() => {
                            /* Dismiss report */
                          }}
                        >
                          <Check className="w-4 h-4 mr-1" />
                          Rejeter
                        </Button>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </TabsContent>

        {/* Settings Tab */}
        <TabsContent value="settings" className="mt-0">
          <div className="p-4 space-y-6">
            {/* Group Info */}
            <div className="space-y-4">
              <h3 className="font-semibold">Informations du groupe</h3>
              <div>
                <Label htmlFor="edit-name">Nom du groupe</Label>
                <Input
                  id="edit-name"
                  value={groupName}
                  onChange={(e) => setGroupName(e.target.value)}
                  className="mt-1"
                />
              </div>
              <div>
                <Label htmlFor="edit-desc">Description</Label>
                <Textarea
                  id="edit-desc"
                  value={groupDesc}
                  onChange={(e) => setGroupDesc(e.target.value)}
                  className="mt-1 resize-none"
                  rows={3}
                />
              </div>
              <div>
                <Label htmlFor="cover-image">Photo de couverture</Label>
                <div className="mt-2 flex items-center gap-3">
                  <div className="w-20 h-20 rounded-lg bg-muted flex items-center justify-center border-2 border-dashed border-border">
                    <Upload className="w-6 h-6 text-muted-foreground" />
                  </div>
                  <Button variant="outline" size="sm">
                    Changer l'image
                  </Button>
                </div>
              </div>
            </div>

            {/* Visibility */}
            <div className="space-y-4">
              <h3 className="font-semibold">Visibilite et confidentialite</h3>
              <div>
                <Label>Type de groupe</Label>
                <Select
                  value={groupVisibility}
                  onValueChange={(v) => setGroupVisibility(v as "public" | "private")}
                >
                  <SelectTrigger className="mt-1">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="public">
                      <div className="flex items-center gap-2">
                        <Globe className="w-4 h-4" />
                        Public
                      </div>
                    </SelectItem>
                    <SelectItem value="private">
                      <div className="flex items-center gap-2">
                        <Lock className="w-4 h-4" />
                        Prive
                      </div>
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex items-center justify-between p-3 bg-muted rounded-lg">
                <div className="flex-1">
                  <p className="font-medium text-sm">Approuver les nouveaux membres</p>
                  <p className="text-xs text-muted-foreground">
                    Les demandes doivent etre approuvees par un admin
                  </p>
                </div>
                <Button
                  variant={requireApproval ? "default" : "outline"}
                  size="sm"
                  onClick={() => setRequireApproval(!requireApproval)}
                  className={requireApproval ? "bg-primary" : ""}
                >
                  {requireApproval ? <Eye className="w-4 h-4" /> : <EyeOff className="w-4 h-4" />}
                </Button>
              </div>
            </div>

            {/* Invite Code */}
            <div className="space-y-4">
              <h3 className="font-semibold">Code d'invitation</h3>
              <div className="bg-muted rounded-lg p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium">Code actuel</p>
                    <p className="text-xs text-muted-foreground">
                      Partagez ce code pour inviter des membres
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono font-bold text-lg">{inviteCode}</span>
                    <Button variant="ghost" size="sm" onClick={handleCopyCode}>
                      {copiedCode ? (
                        <Check className="w-4 h-4 text-green-600" />
                      ) : (
                        <Copy className="w-4 h-4" />
                      )}
                    </Button>
                  </div>
                </div>
                <Button
                  variant="outline"
                  size="sm"
                  className="w-full gap-2"
                  onClick={() => setRegenerateCodeDialog(true)}
                >
                  <RefreshCw className="w-4 h-4" />
                  Regenerer le code
                </Button>
              </div>
            </div>

            {/* Danger Zone */}
            <div className="space-y-4 pt-4 border-t border-border">
              <h3 className="font-semibold text-red-600">Zone de danger</h3>
              <Button
                variant="outline"
                className="w-full gap-2 text-red-600 border-red-200 hover:bg-red-50 hover:text-red-700"
                onClick={() => setDeleteGroupDialog(true)}
              >
                <Trash2 className="w-4 h-4" />
                Supprimer le groupe
              </Button>
            </div>

            {/* Save Button */}
            <div className="sticky bottom-0 bg-white pt-4 pb-2 border-t border-border">
              <Button className="w-full bg-primary hover:bg-primary/90">
                Enregistrer les modifications
              </Button>
            </div>
          </div>
        </TabsContent>

        {/* Stats Tab */}
        <TabsContent value="stats" className="mt-0">
          <div className="p-4 space-y-6">
            {/* Overview */}
            <div>
              <h3 className="font-semibold mb-4">Vue d'ensemble</h3>
              <div className="grid grid-cols-2 gap-3">
                <div className="bg-white rounded-xl border border-border p-4 text-center">
                  <p className="text-2xl font-bold text-primary">8</p>
                  <p className="text-xs text-muted-foreground mt-1">Membres actifs</p>
                </div>
                <div className="bg-white rounded-xl border border-border p-4 text-center">
                  <p className="text-2xl font-bold text-primary">34</p>
                  <p className="text-xs text-muted-foreground mt-1">Photos partagees</p>
                </div>
                <div className="bg-white rounded-xl border border-border p-4 text-center">
                  <p className="text-2xl font-bold text-primary">+3</p>
                  <p className="text-xs text-muted-foreground mt-1">Nouveaux (7j)</p>
                </div>
                <div className="bg-white rounded-xl border border-border p-4 text-center">
                  <p className="text-2xl font-bold text-primary">156</p>
                  <p className="text-xs text-muted-foreground mt-1">Interactions</p>
                </div>
              </div>
            </div>

            {/* Activity */}
            <div>
              <h3 className="font-semibold mb-4">Activite recente</h3>
              <div className="space-y-3">
                <div className="bg-white rounded-xl border border-border p-3">
                  <div className="flex items-center gap-3 mb-2">
                    <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                      <Image className="w-5 h-5 text-primary" />
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-medium">Photos publiees</p>
                      <p className="text-xs text-muted-foreground">Derniere semaine</p>
                    </div>
                    <span className="text-lg font-bold">12</span>
                  </div>
                  <div className="h-2 bg-muted rounded-full overflow-hidden">
                    <div className="h-full bg-primary rounded-full" style={{ width: "75%" }} />
                  </div>
                </div>

                <div className="bg-white rounded-xl border border-border p-3">
                  <div className="flex items-center gap-3 mb-2">
                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center">
                      <Users className="w-5 h-5 text-blue-600" />
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-medium">Nouveaux membres</p>
                      <p className="text-xs text-muted-foreground">Derniere semaine</p>
                    </div>
                    <span className="text-lg font-bold">3</span>
                  </div>
                  <div className="h-2 bg-muted rounded-full overflow-hidden">
                    <div className="h-full bg-blue-500 rounded-full" style={{ width: "60%" }} />
                  </div>
                </div>
              </div>
            </div>

            {/* Top Contributors */}
            <div>
              <h3 className="font-semibold mb-4">Contributeurs actifs</h3>
              <div className="space-y-2">
                {mockMembers.slice(0, 3).map((member, idx) => (
                  <div
                    key={member.id}
                    className="bg-white rounded-xl border border-border p-3 flex items-center gap-3"
                  >
                    <span className="text-lg font-bold text-muted-foreground w-6">
                      #{idx + 1}
                    </span>
                    <img
                      src={member.avatar}
                      alt={member.name}
                      className="w-10 h-10 rounded-full object-cover"
                    />
                    <div className="flex-1">
                      <p className="text-sm font-medium">{member.name}</p>
                      <p className="text-xs text-muted-foreground">
                        {member.photosCount} photos
                      </p>
                    </div>
                    {getRoleBadge(member.role)}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </TabsContent>
      </Tabs>

      {/* Change Role Dialog */}
      <Dialog open={changeRoleDialog} onOpenChange={setChangeRoleDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle>Changer le role</DialogTitle>
            <DialogDescription>
              Modifier le role de {selectedMember?.name}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-2 py-4">
            <Select defaultValue={selectedMember?.role}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="admin">
                  <div className="flex items-center gap-2">
                    <Shield className="w-4 h-4 text-blue-600" />
                    Administrateur
                  </div>
                </SelectItem>
                <SelectItem value="moderator">
                  <div className="flex items-center gap-2">
                    <Shield className="w-4 h-4 text-purple-600" />
                    Moderateur
                  </div>
                </SelectItem>
                <SelectItem value="member">
                  <div className="flex items-center gap-2">
                    <Users className="w-4 h-4" />
                    Membre
                  </div>
                </SelectItem>
              </SelectContent>
            </Select>
            <div className="bg-muted rounded-lg p-3 space-y-1 text-xs">
              <p className="font-medium">Permissions :</p>
              <ul className="space-y-0.5 text-muted-foreground">
                <li>• Admin : Gerer membres, parametres et contenu</li>
                <li>• Moderateur : Moderer le contenu et les signalements</li>
                <li>• Membre : Consulter et publier du contenu</li>
              </ul>
            </div>
          </div>
          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button
              variant="outline"
              onClick={() => setChangeRoleDialog(false)}
              className="w-full sm:w-auto"
            >
              Annuler
            </Button>
            <Button
              onClick={() => setChangeRoleDialog(false)}
              className="w-full sm:w-auto bg-primary hover:bg-primary/90"
            >
              Confirmer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Remove Member Dialog */}
      <Dialog open={removeMemberDialog} onOpenChange={setRemoveMemberDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2 text-red-600">
              <AlertCircle className="w-5 h-5" />
              Retirer un membre
            </DialogTitle>
            <DialogDescription>
              Etes-vous sur de vouloir retirer {selectedMember?.name} du groupe ? Cette action
              est irreversible.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex-col sm:flex-row gap-2 pt-4">
            <Button
              variant="outline"
              onClick={() => setRemoveMemberDialog(false)}
              className="w-full sm:w-auto"
            >
              Annuler
            </Button>
            <Button
              onClick={() => setRemoveMemberDialog(false)}
              className="w-full sm:w-auto bg-red-600 hover:bg-red-700 text-white"
            >
              Retirer du groupe
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Group Dialog */}
      <Dialog open={deleteGroupDialog} onOpenChange={setDeleteGroupDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2 text-red-600">
              <AlertCircle className="w-5 h-5" />
              Supprimer le groupe
            </DialogTitle>
            <DialogDescription>
              Attention ! Cette action est irreversible. Toutes les photos et donnees du
              groupe seront definitivement supprimees.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Label htmlFor="confirm-delete">
              Tapez "{groupName}" pour confirmer
            </Label>
            <Input
              id="confirm-delete"
              placeholder={groupName}
              className="mt-2"
            />
          </div>
          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button
              variant="outline"
              onClick={() => setDeleteGroupDialog(false)}
              className="w-full sm:w-auto"
            >
              Annuler
            </Button>
            <Button className="w-full sm:w-auto bg-red-600 hover:bg-red-700 text-white">
              Supprimer definitivement
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Regenerate Code Dialog */}
      <Dialog open={regenerateCodeDialog} onOpenChange={setRegenerateCodeDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <RefreshCw className="w-5 h-5 text-primary" />
              Regenerer le code
            </DialogTitle>
            <DialogDescription>
              Un nouveau code sera genere. L'ancien code ne fonctionnera plus.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex-col sm:flex-row gap-2 pt-4">
            <Button
              variant="outline"
              onClick={() => setRegenerateCodeDialog(false)}
              className="w-full sm:w-auto"
            >
              Annuler
            </Button>
            <Button
              onClick={handleRegenerateCode}
              className="w-full sm:w-auto bg-primary hover:bg-primary/90"
            >
              Regenerer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Invite Dialog */}
      <Dialog open={inviteDialog} onOpenChange={setInviteDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <UserCheck className="w-5 h-5 text-primary" />
              Inviter des membres
            </DialogTitle>
            <DialogDescription>
              Partagez ce code ou ce lien pour inviter des personnes dans le groupe
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div>
              <Label className="text-sm text-muted-foreground mb-2 block">
                Code d'invitation
              </Label>
              <div className="flex items-center gap-2">
                <div className="flex-1 bg-muted rounded-lg p-3 text-center">
                  <span className="font-mono font-bold text-lg tracking-wider">
                    {inviteCode}
                  </span>
                </div>
                <Button variant="outline" onClick={handleCopyCode}>
                  {copiedCode ? (
                    <Check className="w-4 h-4 text-green-600" />
                  ) : (
                    <Copy className="w-4 h-4" />
                  )}
                </Button>
              </div>
            </div>
            <div>
              <Label className="text-sm text-muted-foreground mb-2 block">
                Lien d'invitation
              </Label>
              <div className="flex items-center gap-2">
                <div className="flex-1 bg-muted rounded-lg p-3">
                  <p className="text-xs truncate">
                    traveling.app/groups/join/{inviteCode}
                  </p>
                </div>
                <Button
                  variant="outline"
                  onClick={() => {
                    const link = `traveling.app/groups/join/${inviteCode}`;
                    if (navigator.clipboard && navigator.clipboard.writeText) {
                      navigator.clipboard.writeText(link);
                    }
                    setCopiedCode(true);
                    setTimeout(() => setCopiedCode(false), 2000);
                  }}
                >
                  {copiedCode ? (
                    <Check className="w-4 h-4 text-green-600" />
                  ) : (
                    <Copy className="w-4 h-4" />
                  )}
                </Button>
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setInviteDialog(false)}
              className="w-full"
            >
              Fermer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Moderation Action Dialog */}
      <Dialog open={moderationDialog} onOpenChange={setModerationDialog}>
        <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2 text-red-600">
              <Trash2 className="w-5 h-5" />
              Supprimer la photo
            </DialogTitle>
            <DialogDescription>
              Cette photo sera definitivement supprimee du groupe et l'auteur sera notifie.
            </DialogDescription>
          </DialogHeader>
          {selectedPhoto && (
            <div className="py-2">
              <img
                src={selectedPhoto.photoUrl}
                alt="Photo à modérer"
                className="w-full rounded-lg"
              />
            </div>
          )}
          <DialogFooter className="flex-col sm:flex-row gap-2">
            <Button
              variant="outline"
              onClick={() => setModerationDialog(false)}
              className="w-full sm:w-auto"
            >
              Annuler
            </Button>
            <Button
              onClick={() => setModerationDialog(false)}
              className="w-full sm:w-auto bg-red-600 hover:bg-red-700 text-white"
            >
              Supprimer la photo
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </MobileContainer>
  );
}
