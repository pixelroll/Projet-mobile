import { useState } from "react";
import { useNavigate } from "react-router";
import {
  Heart,
  MessageCircle,
  UserPlus,
  MapPin,
  Bell,
  Check,
  ArrowLeft,
  Settings,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { BottomNav } from "../components/BottomNav";
import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "../components/ui/tabs";

interface Notification {
  id: number;
  type: "like" | "comment" | "follow" | "itinerary" | "suggestion";
  user?: string;
  userAvatar?: string;
  content: string;
  photo?: string;
  time: string;
  read: boolean;
}

const mockNotifications: Notification[] = [
  {
    id: 1,
    type: "like",
    user: "Marie Dubois",
    userAvatar: "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1c2VyJTIwcHJvZmlsZSUyMGF2YXRhciUyMHBlcnNvbnxlbnwxfHx8fDE3NzM2NTc3NTd8MA&ixlib=rb-4.1.0&q=80&w=1080",
    content: "a aimé votre photo de la Tour Eiffel",
    photo: "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    time: "Il y a 5 min",
    read: false,
  },
  {
    id: 2,
    type: "comment",
    user: "Thomas Martin",
    userAvatar: "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1c2VyJTIwcHJvZmlsZSUyMGF2YXRhciUyMHBlcnNvbnxlbnwxfHx8fDE3NzM2NTc3NTd8MA&ixlib=rb-4.1.0&q=80&w=1080",
    content: 'a commenté : "Magnifique vue ! 😍"',
    photo: "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0b2t5byUyMHN0cmVldCUyMG5pZ2h0fGVufDF8fHx8MTc3Mzc1Njk2N3ww&ixlib=rb-4.1.0&q=80&w=1080",
    time: "Il y a 15 min",
    read: false,
  },
  {
    id: 3,
    type: "follow",
    user: "Sophie Bernard",
    userAvatar: "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1c2VyJTIwcHJvZmlsZSUyMGF2YXRhciUyMHBlcnNvbnxlbnwxfHx8fDE3NzM2NTc3NTd8MA&ixlib=rb-4.1.0&q=80&w=1080",
    content: "a commencé à vous suivre",
    time: "Il y a 1h",
    read: false,
  },
  {
    id: 4,
    type: "itinerary",
    content: "Votre itinéraire 'Paris en 3 jours' est prêt !",
    time: "Il y a 2h",
    read: true,
  },
  {
    id: 5,
    type: "suggestion",
    content: "Nouvelles photos de Barcelone disponibles près de vous",
    time: "Il y a 3h",
    read: true,
  },
  {
    id: 6,
    type: "like",
    user: "Lucas Petit",
    userAvatar: "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1c2VyJTIwcHJvZmlsZSUyMGF2YXRhciUyMHBlcnNvbnxlbnwxfHx8fDE3NzM2NTc3NTd8MA&ixlib=rb-4.1.0&q=80&w=1080",
    content: "a aimé votre photo du Colisée",
    photo: "https://images.unsplash.com/photo-1552832230-c0197dd311b5?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxyb21lJTIwY29sb3NzZXVtfGVufDF8fHx8MTc3MzczMTE3Nnww&ixlib=rb-4.1.0&q=80&w=1080",
    time: "Hier",
    read: true,
  },
  {
    id: 7,
    type: "comment",
    user: "Emma Leroy",
    userAvatar: "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx1c2VyJTIwcHJvZmlsZSUyMGF2YXRhciUyMHBlcnNvbnxlbnwxfHx8fDE3NzM2NTc3NTd8MA&ixlib=rb-4.1.0&q=80&w=1080",
    content: 'a commenté : "Super itinéraire, merci !"',
    time: "Hier",
    read: true,
  },
  {
    id: 8,
    type: "suggestion",
    content: "Découvrez les nouvelles destinations tendances",
    time: "Il y a 2 jours",
    read: true,
  },
];

export function Notifications() {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState(mockNotifications);
  const [activeTab, setActiveTab] = useState("all");

  const unreadCount = notifications.filter((n) => !n.read).length;

  const markAllAsRead = () => {
    setNotifications(notifications.map((n) => ({ ...n, read: true })));
  };

  const markAsRead = (id: number) => {
    setNotifications(
      notifications.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case "like":
        return <Heart className="w-5 h-5 text-red-500" />;
      case "comment":
        return <MessageCircle className="w-5 h-5 text-primary" />;
      case "follow":
        return <UserPlus className="w-5 h-5 text-accent" />;
      case "itinerary":
        return <MapPin className="w-5 h-5 text-primary" />;
      case "suggestion":
        return <Bell className="w-5 h-5 text-secondary" />;
      default:
        return <Bell className="w-5 h-5 text-muted-foreground" />;
    }
  };

  const filteredNotifications = notifications.filter((n) => {
    if (activeTab === "all") return true;
    if (activeTab === "unread") return !n.read;
    return true;
  });

  return (
    <MobileContainer className="pb-16">
      {/* Header */}
      <div className="sticky top-0 bg-white border-b border-border z-10">
        <div className="p-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate("/home")}
              className="p-1 hover:bg-muted rounded-lg transition-colors"
            >
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div>
              <h2 className="font-bold">Notifications</h2>
              {unreadCount > 0 && (
                <p className="text-sm text-muted-foreground">
                  {unreadCount} non lue{unreadCount > 1 ? "s" : ""}
                </p>
              )}
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => navigate("/notifications/settings")}
              className="p-2 hover:bg-muted rounded-lg transition-colors"
            >
              <Settings className="w-5 h-5 text-muted-foreground" />
            </button>
            {unreadCount > 0 && (
              <Button
                variant="ghost"
                size="sm"
                onClick={markAllAsRead}
                className="text-primary"
              >
                <Check className="w-4 h-4 mr-1" />
                Tout marquer
              </Button>
            )}
          </div>
        </div>

        {/* Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="w-full rounded-none bg-white border-b">
            <TabsTrigger value="all" className="flex-1">
              Toutes
              {notifications.length > 0 && (
                <Badge variant="secondary" className="ml-2">
                  {notifications.length}
                </Badge>
              )}
            </TabsTrigger>
            <TabsTrigger value="unread" className="flex-1">
              Non lues
              {unreadCount > 0 && (
                <Badge variant="default" className="ml-2 bg-primary">
                  {unreadCount}
                </Badge>
              )}
            </TabsTrigger>
          </TabsList>
        </Tabs>
      </div>

      {/* Notifications list */}
      <div className="divide-y divide-border">
        {filteredNotifications.length === 0 ? (
          <div className="text-center py-16 px-4">
            <Bell className="w-16 h-16 text-muted-foreground mx-auto mb-4 opacity-50" />
            <h3 className="font-semibold mb-2">
              {activeTab === "unread"
                ? "Aucune notification non lue"
                : "Aucune notification"}
            </h3>
            <p className="text-sm text-muted-foreground">
              {activeTab === "unread"
                ? "Vous êtes à jour !"
                : "Les notifications apparaîtront ici"}
            </p>
          </div>
        ) : (
          filteredNotifications.map((notification) => (
            <div
              key={notification.id}
              onClick={() => markAsRead(notification.id)}
              className={`p-4 cursor-pointer transition-colors hover:bg-muted/50 ${
                !notification.read ? "bg-muted/30" : ""
              }`}
            >
              <div className="flex gap-3">
                {/* Icon/Avatar */}
                <div className="flex-shrink-0">
                  {notification.userAvatar ? (
                    <img
                      src={notification.userAvatar}
                      alt={notification.user}
                      className="w-12 h-12 rounded-full object-cover"
                    />
                  ) : (
                    <div className="w-12 h-12 bg-muted rounded-full flex items-center justify-center">
                      {getNotificationIcon(notification.type)}
                    </div>
                  )}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-2">
                    <div className="flex-1">
                      <p className="text-sm">
                        {notification.user && (
                          <span className="font-semibold">
                            {notification.user}{" "}
                          </span>
                        )}
                        <span className="text-foreground">
                          {notification.content}
                        </span>
                      </p>
                      <div className="flex items-center gap-2 mt-1">
                        <span className="text-xs text-muted-foreground">
                          {notification.time}
                        </span>
                        {!notification.read && (
                          <div className="w-2 h-2 bg-primary rounded-full"></div>
                        )}
                      </div>
                    </div>

                    {/* Photo thumbnail */}
                    {notification.photo && (
                      <img
                        src={notification.photo}
                        alt="Photo"
                        className="w-12 h-12 rounded-lg object-cover flex-shrink-0"
                      />
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <BottomNav />
    </MobileContainer>
  );
}