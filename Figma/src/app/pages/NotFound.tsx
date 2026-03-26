import { useNavigate } from "react-router";
import { Home, ArrowLeft } from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";

export function NotFound() {
  const navigate = useNavigate();

  return (
    <MobileContainer>
      <div className="flex flex-col items-center justify-center min-h-screen px-4 -mt-16">
        <div className="text-center space-y-4">
          <h1 className="text-6xl font-bold text-primary">404</h1>
          <h2 className="text-2xl font-semibold">Page introuvable</h2>
          <p className="text-muted-foreground">
            La page que vous recherchez n'existe pas ou a été déplacée.
          </p>
          <div className="flex flex-col sm:flex-row gap-3 pt-4">
            <button
              onClick={() => navigate(-1)}
              className="flex items-center justify-center gap-2 px-6 py-3 bg-muted hover:bg-muted/80 rounded-lg font-semibold transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
              Retour
            </button>
            <button
              onClick={() => navigate("/home")}
              className="flex items-center justify-center gap-2 px-6 py-3 bg-primary hover:bg-primary/90 text-primary-foreground rounded-lg font-semibold transition-colors"
            >
              <Home className="w-5 h-5" />
              Accueil
            </button>
          </div>
        </div>
      </div>
    </MobileContainer>
  );
}
