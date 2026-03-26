import { useState } from 'react';
import { useNavigate } from 'react-router';
import { ArrowLeft, Bell, User, Users, MapPin, Tag, Plus, X } from 'lucide-react';
import { MobileContainer } from '../components/MobileContainer';
import { BottomNav } from '../components/BottomNav';
import { Label } from '../components/ui/label';
import { Switch } from '../components/ui/switch';
import { Button } from '../components/ui/button';
import { Input } from '../components/ui/input';
import { Badge } from '../components/ui/badge';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '../components/ui/dialog';

interface NotificationPreference {
  id: string;
  type: 'person' | 'group' | 'place' | 'tag';
  name: string;
  enabled: boolean;
}

export function NotificationSettings() {
  const navigate = useNavigate();
  const [preferences, setPreferences] = useState<NotificationPreference[]>([
    { id: '1', type: 'person', name: 'Marie Dubois', enabled: true },
    { id: '2', type: 'person', name: 'Thomas Martin', enabled: true },
    { id: '3', type: 'group', name: 'Famille', enabled: true },
    { id: '4', type: 'group', name: 'Groupe voyage Paris 2026', enabled: false },
    { id: '5', type: 'place', name: 'Paris, France', enabled: true },
    { id: '6', type: 'place', name: 'Tokyo, Japon', enabled: false },
    { id: '7', type: 'tag', name: 'Architecture', enabled: true },
    { id: '8', type: 'tag', name: 'Coucher de soleil', enabled: false },
  ]);

  const [newItemName, setNewItemName] = useState('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogType, setDialogType] = useState<'person' | 'group' | 'place' | 'tag'>('person');

  const togglePreference = (id: string) => {
    setPreferences(prev =>
      prev.map(pref =>
        pref.id === id ? { ...pref, enabled: !pref.enabled } : pref
      )
    );
  };

  const removePreference = (id: string) => {
    setPreferences(prev => prev.filter(pref => pref.id !== id));
  };

  const addPreference = () => {
    if (newItemName.trim()) {
      const newPref: NotificationPreference = {
        id: Date.now().toString(),
        type: dialogType,
        name: newItemName.trim(),
        enabled: true,
      };
      setPreferences(prev => [...prev, newPref]);
      setNewItemName('');
      setDialogOpen(false);
    }
  };

  const openAddDialog = (type: 'person' | 'group' | 'place' | 'tag') => {
    setDialogType(type);
    setNewItemName('');
    setDialogOpen(true);
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'person':
        return <User className="w-5 h-5" />;
      case 'group':
        return <Users className="w-5 h-5" />;
      case 'place':
        return <MapPin className="w-5 h-5" />;
      case 'tag':
        return <Tag className="w-5 h-5" />;
      default:
        return <Bell className="w-5 h-5" />;
    }
  };

  const getTypeLabel = (type: string) => {
    switch (type) {
      case 'person':
        return 'Personne';
      case 'group':
        return 'Groupe';
      case 'place':
        return 'Lieu';
      case 'tag':
        return 'Tag';
      default:
        return '';
    }
  };

  const filterByType = (type: string) => preferences.filter(p => p.type === type);

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10 p-4 flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="p-1">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <div className="flex-1">
          <h2 className="font-semibold">Paramètres des notifications</h2>
          <p className="text-xs text-muted-foreground">
            Gérez vos préférences de notifications
          </p>
        </div>
      </div>

      <div className="p-4 space-y-6">
        {/* Global notification toggle */}
        <div className="p-4 bg-muted/50 rounded-lg">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                <Bell className="w-5 h-5 text-primary" />
              </div>
              <div>
                <p className="font-medium">Notifications activées</p>
                <p className="text-xs text-muted-foreground">
                  Recevoir des notifications push
                </p>
              </div>
            </div>
            <Switch defaultChecked />
          </div>
        </div>

        {/* Notifications by person */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <Label className="text-base font-semibold flex items-center gap-2">
              <User className="w-5 h-5 text-primary" />
              Publications d'une personne
            </Label>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => openAddDialog('person')}
              className="h-8 text-primary"
            >
              <Plus className="w-4 h-4 mr-1" />
              Ajouter
            </Button>
          </div>
          <div className="space-y-2">
            {filterByType('person').length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                Aucune personne suivie
              </p>
            ) : (
              filterByType('person').map(pref => (
                <div
                  key={pref.id}
                  className="flex items-center justify-between p-3 bg-card rounded-lg border border-border"
                >
                  <div className="flex items-center gap-3 flex-1">
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                      <User className="w-4 h-4 text-primary" />
                    </div>
                    <span className="text-sm font-medium">{pref.name}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Switch
                      checked={pref.enabled}
                      onCheckedChange={() => togglePreference(pref.id)}
                    />
                    <button
                      onClick={() => removePreference(pref.id)}
                      className="p-1 hover:bg-muted rounded"
                    >
                      <X className="w-4 h-4 text-muted-foreground" />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Notifications by group */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <Label className="text-base font-semibold flex items-center gap-2">
              <Users className="w-5 h-5 text-primary" />
              Publications d'un groupe
            </Label>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => openAddDialog('group')}
              className="h-8 text-primary"
            >
              <Plus className="w-4 h-4 mr-1" />
              Ajouter
            </Button>
          </div>
          <div className="space-y-2">
            {filterByType('group').length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                Aucun groupe suivi
              </p>
            ) : (
              filterByType('group').map(pref => (
                <div
                  key={pref.id}
                  className="flex items-center justify-between p-3 bg-card rounded-lg border border-border"
                >
                  <div className="flex items-center gap-3 flex-1">
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                      <Users className="w-4 h-4 text-primary" />
                    </div>
                    <span className="text-sm font-medium">{pref.name}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Switch
                      checked={pref.enabled}
                      onCheckedChange={() => togglePreference(pref.id)}
                    />
                    <button
                      onClick={() => removePreference(pref.id)}
                      className="p-1 hover:bg-muted rounded"
                    >
                      <X className="w-4 h-4 text-muted-foreground" />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Notifications by place */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <Label className="text-base font-semibold flex items-center gap-2">
              <MapPin className="w-5 h-5 text-primary" />
              Photos d'un lieu
            </Label>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => openAddDialog('place')}
              className="h-8 text-primary"
            >
              <Plus className="w-4 h-4 mr-1" />
              Ajouter
            </Button>
          </div>
          <div className="space-y-2">
            {filterByType('place').length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                Aucun lieu suivi
              </p>
            ) : (
              filterByType('place').map(pref => (
                <div
                  key={pref.id}
                  className="flex items-center justify-between p-3 bg-card rounded-lg border border-border"
                >
                  <div className="flex items-center gap-3 flex-1">
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center">
                      <MapPin className="w-4 h-4 text-primary" />
                    </div>
                    <span className="text-sm font-medium">{pref.name}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Switch
                      checked={pref.enabled}
                      onCheckedChange={() => togglePreference(pref.id)}
                    />
                    <button
                      onClick={() => removePreference(pref.id)}
                      className="p-1 hover:bg-muted rounded"
                    >
                      <X className="w-4 h-4 text-muted-foreground" />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Notifications by tag */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <Label className="text-base font-semibold flex items-center gap-2">
              <Tag className="w-5 h-5 text-primary" />
              Photos d'un thème/tag
            </Label>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => openAddDialog('tag')}
              className="h-8 text-primary"
            >
              <Plus className="w-4 h-4 mr-1" />
              Ajouter
            </Button>
          </div>
          <div className="space-y-2">
            {filterByType('tag').length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-4">
                Aucun tag suivi
              </p>
            ) : (
              filterByType('tag').map(pref => (
                <div
                  key={pref.id}
                  className="flex items-center justify-between p-3 bg-card rounded-lg border border-border"
                >
                  <div className="flex items-center gap-3 flex-1">
                    <Badge variant="secondary">{pref.name}</Badge>
                  </div>
                  <div className="flex items-center gap-2">
                    <Switch
                      checked={pref.enabled}
                      onCheckedChange={() => togglePreference(pref.id)}
                    />
                    <button
                      onClick={() => removePreference(pref.id)}
                      className="p-1 hover:bg-muted rounded"
                    >
                      <X className="w-4 h-4 text-muted-foreground" />
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Add notification dialog */}
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent className="max-w-[calc(100vw-1rem)] rounded-xl">
          <DialogHeader>
            <DialogTitle>
              Ajouter une notification
            </DialogTitle>
            <DialogDescription>
              Soyez notifié quand {getTypeLabel(dialogType).toLowerCase()} publie une photo
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4">
            <div>
              <Label htmlFor="new-item">
                Nom {dialogType === 'person' ? 'de la personne' : 
                     dialogType === 'group' ? 'du groupe' : 
                     dialogType === 'place' ? 'du lieu' : 
                     'du tag'}
              </Label>
              <Input
                id="new-item"
                value={newItemName}
                onChange={(e) => setNewItemName(e.target.value)}
                placeholder={
                  dialogType === 'person' ? 'Ex: Marie Dubois' :
                  dialogType === 'group' ? 'Ex: Amis proches' :
                  dialogType === 'place' ? 'Ex: Paris, France' :
                  'Ex: Architecture'
                }
                className="mt-2"
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    addPreference();
                  }
                }}
              />
            </div>
            <div className="flex gap-2">
              <Button
                variant="outline"
                onClick={() => setDialogOpen(false)}
                className="flex-1"
              >
                Annuler
              </Button>
              <Button
                onClick={addPreference}
                disabled={!newItemName.trim()}
                className="flex-1 bg-primary hover:bg-primary/90"
              >
                Ajouter
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>

      <BottomNav />
    </MobileContainer>
  );
}