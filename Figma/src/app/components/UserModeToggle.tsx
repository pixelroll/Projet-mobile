import { User, UserX } from 'lucide-react';
import { useUserMode } from '../contexts/UserModeContext';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from './ui/dialog';

export function UserModeToggle() {
  const { mode, setMode, isConnected, user } = useUserMode();

  return (
    <Dialog>
      <DialogTrigger asChild>
        <button className="flex items-center gap-2 p-2 hover:bg-muted rounded-lg transition-colors">
          {isConnected ? (
            <>
              <img 
                src={user?.avatar} 
                alt={user?.name}
                className="w-7 h-7 rounded-full object-cover"
              />
              <Badge variant="default" className="bg-primary text-xs">
                Connecté
              </Badge>
            </>
          ) : (
            <>
              <div className="w-7 h-7 rounded-full bg-muted flex items-center justify-center">
                <UserX className="w-4 h-4 text-muted-foreground" />
              </div>
              <Badge variant="outline" className="text-xs">
                Anonyme
              </Badge>
            </>
          )}
        </button>
      </DialogTrigger>
      
      <DialogContent className="max-w-[calc(100vw-1rem)] rounded-xl">
        <DialogHeader>
          <DialogTitle>Mode utilisateur</DialogTitle>
          <DialogDescription>
            Choisissez comment vous souhaitez utiliser Traveling
          </DialogDescription>
        </DialogHeader>
        
        <div className="space-y-3">
          <button
            onClick={() => setMode('anonymous')}
            className={`w-full p-4 rounded-lg border-2 text-left transition-colors ${
              mode === 'anonymous'
                ? 'border-primary bg-primary/5'
                : 'border-border hover:border-primary/50'
            }`}
          >
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center">
                <UserX className="w-6 h-6 text-muted-foreground" />
              </div>
              <div className="flex-1">
                <p className="font-semibold">Mode anonyme</p>
                <p className="text-sm text-muted-foreground">
                  Parcourir et rechercher des photos publiques
                </p>
              </div>
            </div>
          </button>

          <button
            onClick={() => setMode('connected')}
            className={`w-full p-4 rounded-lg border-2 text-left transition-colors ${
              mode === 'connected'
                ? 'border-primary bg-primary/5'
                : 'border-border hover:border-primary/50'
            }`}
          >
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center">
                <User className="w-6 h-6 text-primary" />
              </div>
              <div className="flex-1">
                <p className="font-semibold">Mode connecté</p>
                <p className="text-sm text-muted-foreground">
                  Publier, partager et accéder aux fonctionnalités avancées
                </p>
              </div>
            </div>
          </button>
        </div>

        {isConnected && user && (
          <div className="mt-4 p-3 bg-muted rounded-lg">
            <p className="text-sm font-medium">Connecté en tant que</p>
            <p className="text-sm text-muted-foreground">{user.name}</p>
            <p className="text-xs text-muted-foreground">{user.email}</p>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}