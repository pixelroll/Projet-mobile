import { Home, Route, Search, User, Map } from "lucide-react";
import { useNavigate, useLocation } from "react-router";

export function BottomNav() {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <div className="fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-md bg-white border-t border-border z-50">
      <div className="flex justify-around items-center h-16">
        <button
          onClick={() => navigate("/home")}
          className={`flex flex-col items-center gap-1 p-2 ${
            isActive("/home") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <Home className="w-6 h-6" />
          <span className="text-xs">Accueil</span>
        </button>
        <button
          onClick={() => navigate("/map")}
          className={`flex flex-col items-center gap-1 p-2 ${
            isActive("/map") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <Map className="w-6 h-6" />
          <span className="text-xs">Carte</span>
        </button>
        <button
          onClick={() => navigate("/travelpath/preferences")}
          className={`flex flex-col items-center gap-1 p-2 ${
            location.pathname.startsWith("/travelpath")
              ? "text-primary"
              : "text-muted-foreground"
          }`}
        >
          <Route className="w-6 h-6" />
          <span className="text-xs">Parcours</span>
        </button>
        <button
          onClick={() => navigate("/search")}
          className={`flex flex-col items-center gap-1 p-2 ${
            isActive("/search") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <Search className="w-6 h-6" />
          <span className="text-xs">Recherche</span>
        </button>
        <button
          onClick={() => navigate("/profile")}
          className={`flex flex-col items-center gap-1 p-2 ${
            isActive("/profile") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <User className="w-6 h-6" />
          <span className="text-xs">Profil</span>
        </button>
      </div>
    </div>
  );
}