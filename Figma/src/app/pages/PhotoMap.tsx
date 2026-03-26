import { useState } from "react";
import { useNavigate } from "react-router";
import { ArrowLeft, MapPin, Layers } from "lucide-react";
import { MapContainer, TileLayer, Marker, Popup, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";

interface PhotoPin {
  id: number;
  image: string;
  place: string;
  city: string;
  lat: number;
  lng: number;
  author: string;
  likes: number;
}

const photoPins: PhotoPin[] = [
  {
    id: 1,
    image: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Tour Eiffel",
    city: "Paris, France",
    lat: 48.8584,
    lng: 2.2945,
    author: "Sophie Martin",
    likes: 342,
  },
  {
    id: 2,
    image: "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Shibuya Crossing",
    city: "Tokyo, Japon",
    lat: 35.6595,
    lng: 139.7005,
    author: "Marie Dubois",
    likes: 521,
  },
  {
    id: 3,
    image: "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Sagrada Familia",
    city: "Barcelone, Espagne",
    lat: 41.4036,
    lng: 2.1744,
    author: "Thomas Martin",
    likes: 687,
  },
  {
    id: 4,
    image: "https://images.unsplash.com/photo-1514565131-fce0801e5785?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Manhattan Skyline",
    city: "New York, USA",
    lat: 40.7128,
    lng: -74.006,
    author: "Lucas Petit",
    likes: 892,
  },
  {
    id: 5,
    image: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Colisee",
    city: "Rome, Italie",
    lat: 41.8902,
    lng: 12.4922,
    author: "Emma Leroy",
    likes: 456,
  },
  {
    id: 6,
    image: "https://images.unsplash.com/photo-1613278435217-de4e5a91a4ee?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=400",
    place: "Temple Ulun Danu",
    city: "Bali, Indonesie",
    lat: -8.2754,
    lng: 115.1669,
    author: "Sophie Martin",
    likes: 734,
  },
];

const createPhotoIcon = (imageUrl: string) => {
  return L.divIcon({
    className: "bg-transparent border-0",
    html: `
      <div style="transform: translate(-50%, -100%); filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));">
        <div style="width: 48px; height: 48px; border-radius: 8px; overflow: hidden; border: 3px solid white; background: white;">
          <img src="${imageUrl}" style="width: 100%; height: 100%; object-fit: cover;" />
        </div>
        <div style="width: 0; height: 0; border-left: 8px solid transparent; border-right: 8px solid transparent; border-top: 10px solid white; margin: -1px auto 0;"></div>
      </div>
    `,
    iconSize: [0, 0],
    iconAnchor: [0, 0],
    popupAnchor: [0, -60],
  });
};

export function PhotoMap() {
  const navigate = useNavigate();
  const [selectedPin, setSelectedPin] = useState<PhotoPin | null>(null);

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
          <h2 className="font-bold">Carte des photos</h2>
          <div className="p-1">
            <Layers className="w-5 h-5 text-muted-foreground" />
          </div>
        </div>
      </div>

      {/* Map */}
      <div className="flex-1 relative z-0">
        <MapContainer
          center={[30, 10]}
          zoom={2}
          style={{ height: "100%", width: "100%" }}
          zoomControl={false}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />

          {photoPins.map((pin) => (
            <Marker
              key={pin.id}
              position={[pin.lat, pin.lng]}
              icon={createPhotoIcon(pin.image)}
              eventHandlers={{
                click: () => setSelectedPin(pin),
              }}
            >
              <Popup>
                <div className="p-1 max-w-[160px] text-center">
                  <img
                    src={pin.image}
                    alt={pin.place}
                    className="w-full h-24 object-cover rounded mb-2"
                  />
                  <p className="font-semibold text-sm">{pin.place}</p>
                  <p className="text-xs text-gray-500">{pin.city}</p>
                  <button
                    onClick={() => navigate(`/photo/${pin.id}`)}
                    className="mt-2 text-xs text-teal-600 font-medium hover:underline"
                  >
                    Voir la photo
                  </button>
                </div>
              </Popup>
            </Marker>
          ))}
        </MapContainer>

        {/* Stats overlay */}
        <div className="absolute bottom-6 left-1/2 -translate-x-1/2 bg-white/95 backdrop-blur shadow-lg rounded-full px-6 py-3 flex items-center gap-6 z-[1000] text-sm font-medium border border-border">
          <div className="flex flex-col items-center">
            <span className="text-muted-foreground text-xs">Photos</span>
            <span className="text-teal-600 font-bold">{photoPins.length}</span>
          </div>
          <div className="w-px h-8 bg-border"></div>
          <div className="flex flex-col items-center">
            <span className="text-muted-foreground text-xs">Pays</span>
            <span className="text-teal-600 font-bold">5</span>
          </div>
        </div>
      </div>

      {/* Selected photo card */}
      {selectedPin && (
        <div className="absolute bottom-24 left-4 right-4 z-[1001] max-w-md mx-auto">
          <div
            className="bg-white rounded-xl shadow-xl border border-border p-3 flex gap-3 cursor-pointer"
            onClick={() => navigate(`/photo/${selectedPin.id}`)}
          >
            <img
              src={selectedPin.image}
              alt={selectedPin.place}
              className="w-20 h-20 rounded-lg object-cover flex-shrink-0"
            />
            <div className="flex-1 min-w-0">
              <h3 className="font-semibold truncate">{selectedPin.place}</h3>
              <div className="flex items-center gap-1 text-sm text-muted-foreground mt-0.5">
                <MapPin className="w-3 h-3" />
                <span>{selectedPin.city}</span>
              </div>
              <p className="text-xs text-muted-foreground mt-1">
                Par {selectedPin.author}
              </p>
              <span className="text-xs text-primary mt-1 inline-block">
                {selectedPin.likes} j'aime
              </span>
            </div>
            <button
              onClick={(e) => {
                e.stopPropagation();
                setSelectedPin(null);
              }}
              className="self-start text-muted-foreground hover:text-foreground"
            >
              ×
            </button>
          </div>
        </div>
      )}

      <BottomNav />
    </MobileContainer>
  );
}
