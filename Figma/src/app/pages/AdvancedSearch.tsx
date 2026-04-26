import { useState } from 'react';
import { useNavigate } from 'react-router';
import {
  ArrowLeft,
  Search,
  Mic,
  Shuffle,
  MapPin,
  Calendar,
  User,
  Tag,
  Sparkles,
  SlidersHorizontal,
  X,
  Users
} from 'lucide-react';
import { MobileContainer } from '../components/MobileContainer';
import { BottomNav } from '../components/BottomNav';
import { Input } from '../components/ui/input';
import { Button } from '../components/ui/button';
import { Badge } from '../components/ui/badge';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '../components/ui/tabs';
import { Label } from '../components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../components/ui/select';

export function AdvancedSearch() {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]);
  const [activeTab, setActiveTab] = useState('filters');
  const [selectedGroup, setSelectedGroup] = useState<string>('');

  const placeTypes = [
    { value: 'nature', label: 'Nature', icon: '🌿' },
    { value: 'museum', label: 'Musée', icon: '🏛️' },
    { value: 'street', label: 'Rue', icon: '🏙️' },
    { value: 'shop', label: 'Magasin', icon: '🛍️' },
    { value: 'restaurant', label: 'Restaurant', icon: '🍽️' },
    { value: 'monument', label: 'Monument', icon: '🗿' },
  ];

  const periods = [
    { value: 'morning', label: 'Matin' },
    { value: 'afternoon', label: 'Après-midi' },
    { value: 'evening', label: 'Soirée' },
    { value: 'night', label: 'Nuit' },
  ];

  const userGroups = [
    { id: '1', name: 'Voyage Paris 2026', members: 8 },
    { id: '2', name: 'Famille Martin', members: 5 },
    { id: '3', name: 'Amis proches', members: 12 },
  ];

  const toggleFilter = (filter: string) => {
    setSelectedFilters(prev =>
      prev.includes(filter)
        ? prev.filter(f => f !== filter)
        : [...prev, filter]
    );
  };

  const clearFilters = () => {
    setSelectedFilters([]);
    setSearchQuery('');
    setSelectedGroup('');
  };

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-4 flex items-center gap-3">
          <button onClick={() => navigate(-1)} className="p-1">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-semibold flex-1">Recherche avancée</h2>
          {selectedFilters.length > 0 && (
            <Button
              variant="ghost"
              size="sm"
              onClick={clearFilters}
              className="text-primary"
            >
              <X className="w-4 h-4 mr-1" />
              Effacer
            </Button>
          )}
        </div>

        {/* Search bar with voice */}
        <div className="px-4 pb-4 space-y-3">
          <div className="relative flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
              <Input
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Rechercher un lieu, un thème..."
                className="pl-10 pr-4"
              />
            </div>
            <Button 
              variant="outline" 
              size="icon"
              className="flex-shrink-0"
            >
              <Mic className="w-5 h-5 text-primary" />
            </Button>
          </div>

          {/* Active filters */}
          {selectedFilters.length > 0 && (
            <div className="flex gap-2 overflow-x-auto pb-2">
              {selectedFilters.map((filter) => (
                <Badge
                  key={filter}
                  variant="default"
                  className="bg-primary whitespace-nowrap"
                >
                  {filter}
                  <button
                    onClick={() => toggleFilter(filter)}
                    className="ml-1 hover:bg-primary-foreground/20 rounded-full"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </Badge>
              ))}
            </div>
          )}
        </div>

        {/* Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="w-full rounded-none bg-white border-b">
            <TabsTrigger value="filters" className="flex-1">
              Filtres
            </TabsTrigger>
            <TabsTrigger value="browse" className="flex-1">
              Navigation
            </TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      {/* Filters Tab */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsContent value="filters" className="mt-0">
          <div className="p-4 space-y-6">
            {/* Place type filter */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Type de lieu
              </Label>
              <div className="grid grid-cols-2 gap-2">
                {placeTypes.map((type) => (
                  <button
                    key={type.value}
                    onClick={() => toggleFilter(type.label)}
                    className={`p-4 rounded-lg border-2 text-left transition-colors ${
                      selectedFilters.includes(type.label)
                        ? 'border-primary bg-primary/5'
                        : 'border-border hover:border-primary/50'
                    }`}
                  >
                    <div className="text-2xl mb-1">{type.icon}</div>
                    <p className="text-sm font-medium">{type.label}</p>
                  </button>
                ))}
              </div>
            </div>

            {/* Period filter */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Moment de la journée
              </Label>
              <div className="grid grid-cols-2 gap-2">
                {periods.map((period) => (
                  <button
                    key={period.value}
                    onClick={() => toggleFilter(period.label)}
                    className={`p-3 rounded-lg border-2 text-center transition-colors ${
                      selectedFilters.includes(period.label)
                        ? 'border-primary bg-primary/5'
                        : 'border-border hover:border-primary/50'
                    }`}
                  >
                    <p className="text-sm font-medium">{period.label}</p>
                  </button>
                ))}
              </div>
            </div>

            {/* Date range */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Période
              </Label>
              <Select>
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner une période..." />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="week">Cette semaine</SelectItem>
                  <SelectItem value="month">Ce mois</SelectItem>
                  <SelectItem value="year">Cette année</SelectItem>
                  <SelectItem value="2026">2026</SelectItem>
                  <SelectItem value="2025">2025</SelectItem>
                  <SelectItem value="custom">Personnalisée...</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Place search */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Lieu
              </Label>
              <Input
                placeholder="Ex: Paris, France"
                className="mb-2"
              />
              <Button variant="outline" className="w-full gap-2">
                <MapPin className="w-4 h-4" />
                Autour de ma position
              </Button>
            </div>

            {/* Author filter */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Auteur
              </Label>
              <Input
                placeholder="Rechercher un auteur..."
              />
            </div>

            {/* Group filter */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Groupe
              </Label>
              <Select value={selectedGroup} onValueChange={setSelectedGroup}>
                <SelectTrigger>
                  <SelectValue placeholder="Tous les groupes" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">Tous les groupes</SelectItem>
                  {userGroups.map((group) => (
                    <SelectItem key={group.id} value={group.id}>
                      <div className="flex items-center gap-2">
                        <Users className="w-4 h-4" />
                        <span>{group.name}</span>
                        <span className="text-xs text-muted-foreground">
                          ({group.members})
                        </span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {selectedGroup && selectedGroup !== 'all' && (
                <div className="mt-2 p-2 bg-primary/5 border border-primary/20 rounded-lg flex items-center justify-between">
                  <div className="flex items-center gap-2 text-sm">
                    <Users className="w-4 h-4 text-primary" />
                    <span className="text-primary font-medium">
                      {userGroups.find((g) => g.id === selectedGroup)?.name}
                    </span>
                  </div>
                  <button
                    onClick={() => setSelectedGroup('')}
                    className="p-1 hover:bg-primary/10 rounded-full transition-colors"
                  >
                    <X className="w-4 h-4 text-primary" />
                  </button>
                </div>
              )}
            </div>

            {/* Tags */}
            <div>
              <Label className="text-base font-semibold mb-3 block">
                Tags
              </Label>
              <div className="flex flex-wrap gap-2 mb-2">
                <Badge 
                  variant="outline" 
                  className="cursor-pointer hover:bg-muted"
                  onClick={() => toggleFilter('Architecture')}
                >
                  Architecture
                </Badge>
                <Badge 
                  variant="outline" 
                  className="cursor-pointer hover:bg-muted"
                  onClick={() => toggleFilter('Coucher de soleil')}
                >
                  Coucher de soleil
                </Badge>
                <Badge 
                  variant="outline" 
                  className="cursor-pointer hover:bg-muted"
                  onClick={() => toggleFilter('Paysage')}
                >
                  Paysage
                </Badge>
              </div>
              <Input
                placeholder="Ajouter un tag..."
              />
            </div>
          </div>
        </TabsContent>

        <TabsContent value="browse" className="mt-0">
          <div className="p-4 space-y-3">
            {/* Random discovery */}
            <button
              onClick={() => navigate('/home')}
              className="w-full p-4 rounded-xl border-2 border-border hover:border-primary transition-colors text-left bg-gradient-to-br from-primary/5 to-accent/5"
            >
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center">
                  <Shuffle className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold">Découverte aléatoire</p>
                  <p className="text-sm text-muted-foreground">
                    Explorez des photos au hasard
                  </p>
                </div>
              </div>
            </button>

            {/* Browse by place */}
            <button
              className="w-full p-4 rounded-xl border-2 border-border hover:border-primary transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center">
                  <MapPin className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold">Par lieu</p>
                  <p className="text-sm text-muted-foreground">
                    Parcourir par destination
                  </p>
                </div>
              </div>
            </button>

            {/* Browse by date */}
            <button
              className="w-full p-4 rounded-xl border-2 border-border hover:border-primary transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center">
                  <Calendar className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold">Par période</p>
                  <p className="text-sm text-muted-foreground">
                    Explorer par date ou saison
                  </p>
                </div>
              </div>
            </button>

            {/* Browse by author */}
            <button
              className="w-full p-4 rounded-xl border-2 border-border hover:border-primary transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center">
                  <User className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold">Par auteur</p>
                  <p className="text-sm text-muted-foreground">
                    Découvrir par photographe
                  </p>
                </div>
              </div>
            </button>

            {/* Browse by similarity */}
            <button
              className="w-full p-4 rounded-xl border-2 border-border hover:border-primary transition-colors text-left"
            >
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center">
                  <Sparkles className="w-6 h-6 text-primary" />
                </div>
                <div className="flex-1">
                  <p className="font-semibold">Photos similaires</p>
                  <p className="text-sm text-muted-foreground">
                    Recherche par similarité (IA)
                  </p>
                </div>
              </div>
            </button>
          </div>
        </TabsContent>
      </Tabs>

      {/* Search button */}
      <div className="fixed bottom-16 left-0 right-0 p-4 bg-white border-t border-border pointer-events-none max-w-md mx-auto">
        <button
          onClick={() => navigate('/home')}
          className="w-full bg-primary hover:bg-primary/90 text-white py-3 rounded-lg font-medium pointer-events-auto shadow-lg flex items-center justify-center gap-2"
        >
          <Search className="w-5 h-5" />
          Rechercher
          {selectedFilters.length > 0 && (
            <Badge variant="secondary" className="ml-2 bg-white text-primary">
              {selectedFilters.length}
            </Badge>
          )}
        </button>
      </div>

      <BottomNav />
    </MobileContainer>
  );
}