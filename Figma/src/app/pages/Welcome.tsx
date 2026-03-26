import { useNavigate } from "react-router";
import { Button } from "../components/ui/button";
import { MobileContainer } from "../components/MobileContainer";

export function Welcome() {
  const navigate = useNavigate();

  return (
    <MobileContainer className="relative">
      {/* Background image with overlay */}
      <div className="relative h-screen">
        <img
          src="https://images.unsplash.com/photo-1743699537171-750edd44bd87?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cmF2ZWwlMjBhZHZlbnR1cmUlMjBiZWF1dGlmdWwlMjBsYW5kc2NhcGV8ZW58MXx8fHwxNzczNzU2OTUzfDA&ixlib=rb-4.1.0&q=80&w=1080"
          alt="Travel"
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-gradient-to-b from-black/40 via-black/30 to-black/60" />

        {/* Content */}
        <div className="absolute inset-0 flex flex-col items-center justify-between p-6 text-white">
          {/* Top section */}
          <div className="flex-1 flex flex-col items-center justify-center text-center">
            <h1 className="text-5xl font-bold mb-4">Traveling</h1>
            <p className="text-lg opacity-90 max-w-xs">
              Découvrez de nouveaux lieux et planifiez vos voyages parfaits
            </p>
          </div>

          {/* Bottom section */}
          <div className="w-full space-y-4">
            <Button
              onClick={() => navigate("/home")}
              className="w-full bg-primary hover:bg-primary/90 text-white py-6"
            >
              Découvrir des photos
            </Button>
            <Button
              onClick={() => navigate("/travelpath/preferences")}
              variant="outline"
              className="w-full border-white bg-transparent text-white hover:bg-white/20 py-6"
            >
              Créer un parcours de visite
            </Button>
            <button
              onClick={() => navigate("/home")}
              className="w-full text-sm text-white/80 hover:text-white py-2"
            >
              Se connecter / S'inscrire
            </button>
          </div>
        </div>
      </div>
    </MobileContainer>
  );
}