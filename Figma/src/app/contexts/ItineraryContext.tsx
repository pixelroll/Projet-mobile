import { createContext, useContext, useState, useEffect, ReactNode } from "react";

interface LikedItinerary {
  id: number;
  likedAt: Date;
}

interface ItineraryContextType {
  likedItineraries: number[];
  toggleLikeItinerary: (id: number) => void;
  isItineraryLiked: (id: number) => boolean;
}

const ItineraryContext = createContext<ItineraryContextType | undefined>(undefined);

export function ItineraryProvider({ children }: { children: ReactNode }) {
  const [likedItineraries, setLikedItineraries] = useState<number[]>([]);

  // Charger les itinéraires likés depuis localStorage au montage
  useEffect(() => {
    const stored = localStorage.getItem("likedItineraries");
    if (stored) {
      try {
        const parsed = JSON.parse(stored);
        setLikedItineraries(parsed);
      } catch (error) {
        console.error("Error parsing liked itineraries:", error);
      }
    }
  }, []);

  // Sauvegarder dans localStorage à chaque changement
  useEffect(() => {
    localStorage.setItem("likedItineraries", JSON.stringify(likedItineraries));
  }, [likedItineraries]);

  const toggleLikeItinerary = (id: number) => {
    setLikedItineraries((prev) => {
      if (prev.includes(id)) {
        return prev.filter((itinId) => itinId !== id);
      } else {
        return [...prev, id];
      }
    });
  };

  const isItineraryLiked = (id: number) => {
    return likedItineraries.includes(id);
  };

  return (
    <ItineraryContext.Provider
      value={{
        likedItineraries,
        toggleLikeItinerary,
        isItineraryLiked,
      }}
    >
      {children}
    </ItineraryContext.Provider>
  );
}

export function useItinerary() {
  const context = useContext(ItineraryContext);
  if (context === undefined) {
    throw new Error("useItinerary must be used within an ItineraryProvider");
  }
  return context;
}
