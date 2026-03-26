import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router";
import { ArrowLeft, MapPin, Calendar, Wallet, ImageIcon } from "lucide-react";
import { MapContainer, TileLayer, Marker, Popup, Polyline, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import { MobileContainer } from "../components/MobileContainer";
import { Button } from "../components/ui/button";
import { mockTrips, Trip } from "../data/trips";

// Component to dynamically update map view when trip changes
function MapUpdater({ center, zoom }: { center: [number, number]; zoom: number }) {
  const map = useMap();
  useEffect(() => {
    map.setView(center, zoom);
  }, [center, zoom, map]);
  return null;
}

export function TripMap() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [trip, setTrip] = useState<Trip | null>(null);
  const [selectedImage, setSelectedImage] = useState<string | null>(null);

  const createCustomIcon = (imageUrl: string) => {
    return L.divIcon({
      className: "bg-transparent border-0",
      html: `
        <div class="flex flex-col items-center justify-center -translate-x-1/2 -translate-y-full drop-shadow-md">
          <div class="w-12 h-12 rounded-lg overflow-hidden border-2 border-white bg-white">
            <img src="${imageUrl}" class="w-full h-full object-cover" />
          </div>
          <div class="w-0 h-0 border-l-[6px] border-l-transparent border-r-[6px] border-r-transparent border-t-[8px] border-t-white -mt-[1px]"></div>
        </div>
      `,
      iconSize: [0, 0],
      iconAnchor: [0, 0],
      popupAnchor: [0, -60],
    });
  };

  useEffect(() => {
    const foundTrip = mockTrips.find((t) => t.id === id);
    if (foundTrip) {
      setTrip(foundTrip);
    }
  }, [id]);

  if (!trip) {
    return (
      <MobileContainer>
        <div className="flex flex-col items-center justify-center h-screen">
          <p>Voyage introuvable.</p>
          <Button onClick={() => navigate(-1)} className="mt-4">Retour</Button>
        </div>
      </MobileContainer>
    );
  }

  return (
    <MobileContainer className="h-screen flex flex-col overflow-hidden">
      {/* Header */}
      <div className="bg-white border-b border-border z-20 shrink-0">
        <div className="p-4 flex items-center justify-between">
          <button
            onClick={() => navigate(-1)}
            className="p-1 hover:bg-muted rounded-lg transition-colors"
          >
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h2 className="font-bold truncate px-4">{trip.title}</h2>
          <div className="w-8" /> {/* Placeholder for balance */}
        </div>
        
        {/* Trip Info Bar */}
        <div className="px-4 pb-4 flex justify-between text-sm text-muted-foreground">
          <div className="flex items-center gap-1">
            <MapPin className="w-4 h-4" />
            <span>{trip.destination}</span>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1">
              <Calendar className="w-4 h-4" />
              <span>{trip.duration}</span>
            </div>
            <div className="flex items-center gap-1 font-medium text-foreground">
              <Wallet className="w-4 h-4 text-teal-600" />
              <span>{trip.budget}</span>
            </div>
          </div>
        </div>

        {/* Trip Description */}
        {trip.description && (
          <div className="px-4 pb-4">
            <p className="text-sm text-muted-foreground leading-relaxed">
              {trip.description}
            </p>
          </div>
        )}
      </div>

      {/* Map Area */}
      <div className="flex-1 relative z-0">
        <MapContainer
          center={trip.center}
          zoom={trip.zoom}
          style={{ height: "100%", width: "100%" }}
          zoomControl={false}
        >
          <MapUpdater center={trip.center} zoom={trip.zoom} />
          
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {/* Path Line */}
          <Polyline 
            positions={trip.path} 
            color="#0d9488" /* Teal-600 */
            weight={4}
            opacity={0.7}
            dashArray="10, 10"
          />

          {/* Picture Markers */}
          {trip.points.map((point) => (
            <Marker 
              key={point.id} 
              position={[point.lat, point.lng]} 
              icon={createCustomIcon(point.image)}
              eventHandlers={{
                click: (e) => {
                  if (point.postId) {
                    navigate(`/photo/${point.postId}`);
                  }
                }
              }}
            >
              {!point.postId && (
                <Popup className="custom-popup">
                  <div className="p-1 max-w-[150px] text-center">
                    <p className="font-semibold text-sm mb-2">{point.name}</p>
                    <div 
                      className="relative aspect-square cursor-pointer rounded overflow-hidden group"
                      onClick={() => setSelectedImage(point.image)}
                    >
                      <img 
                        src={point.image} 
                        alt={point.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform"
                      />
                      <div className="absolute inset-0 bg-black/20 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                        <ImageIcon className="w-6 h-6 text-white drop-shadow-md" />
                      </div>
                    </div>
                    <p className="text-xs text-muted-foreground mt-2">Cliquez pour agrandir</p>
                  </div>
                </Popup>
              )}
            </Marker>
          ))}
        </MapContainer>

        {/* Stats overlay floating on the map */}
        <div className="absolute bottom-6 left-1/2 -translate-x-1/2 bg-white/95 backdrop-blur shadow-lg rounded-full px-6 py-3 flex items-center gap-6 z-[1000] text-sm font-medium border border-border">
          <div className="flex flex-col items-center">
            <span className="text-muted-foreground text-xs">Lieux visités</span>
            <span className="text-teal-600 font-bold">{trip.places}</span>
          </div>
          <div className="w-px h-8 bg-border"></div>
          <div className="flex flex-col items-center">
            <span className="text-muted-foreground text-xs">Photos prises</span>
            <span className="text-teal-600 font-bold">{trip.points.length}</span>
          </div>
        </div>
      </div>

      {/* Full Screen Image Modal */}
      {selectedImage && (
        <div 
          className="fixed inset-0 z-[1000] bg-black/90 flex flex-col items-center justify-center p-4 backdrop-blur-sm"
          onClick={() => setSelectedImage(null)}
        >
          <button 
            className="absolute top-4 right-4 p-2 bg-white/10 rounded-full text-white hover:bg-white/20 transition-colors"
            onClick={() => setSelectedImage(null)}
          >
            <ArrowLeft className="w-6 h-6 rotate-180" />
          </button>
          <img 
            src={selectedImage} 
            alt="Preview" 
            className="max-w-full max-h-[80vh] object-contain rounded-lg shadow-2xl"
            onClick={(e) => e.stopPropagation()} 
          />
        </div>
      )}
    </MobileContainer>
  );
}
