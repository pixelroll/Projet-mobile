import { useNavigate } from "react-router";
import {
  ArrowLeft,
  Bell,
  Shield,
  User,
  Globe,
  HelpCircle,
  LogOut,
  ChevronRight,
} from "lucide-react";
import { MobileContainer } from "../components/MobileContainer";
import { useUserMode } from "../contexts/UserModeContext";

export function Settings() {
  const navigate = useNavigate();
  const { isConnected, user, setMode } = useUserMode();

  const handleLogout = () => {
    setMode("anonymous");
    navigate("/home");
  };

  const settingsSections = [
    {
      title: "Compte",
      items: [
        {
          icon: User,
          label: "Modifier le profil",
          onClick: () => navigate("/profile/edit"),
        },
        {
          icon: Bell,
          label: "Notifications",
          onClick: () => navigate("/notifications/settings"),
        },
      ],
    },
    {
      title: "Préférences",
      items: [
        {
          icon: Globe,
          label: "Langue",
          onClick: () => {},
          value: "Français",
        },
        {
          icon: Shield,
          label: "Confidentialité",
          onClick: () => {},
        },
      ],
    },
    {
      title: "Support",
      items: [
        {
          icon: HelpCircle,
          label: "Aide & Support",
          onClick: () => {},
        },
      ],
    },
  ];

  return (
    <MobileContainer>
      {/* Header */}
      <div className="sticky top-0 bg-background z-10 border-b border-border px-4 py-3">
        <div className="flex items-center gap-3">
          <button onClick={() => navigate(-1)}>
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-xl font-semibold">Paramètres</h1>
        </div>
      </div>

      {/* Content */}
      <div className="px-4 py-4 space-y-6">
        {/* User Info */}
        {isConnected && (
          <div className="flex items-center gap-3 p-4 bg-muted/50 rounded-lg">
            <img
              src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&h=200&fit=crop"
              alt="Profile"
              className="w-16 h-16 rounded-full object-cover"
            />
            <div className="flex-1">
              <h2 className="font-semibold">{user?.name}</h2>
              <p className="text-sm text-muted-foreground">{user?.email}</p>
            </div>
          </div>
        )}

        {/* Settings Sections */}
        {settingsSections.map((section, index) => (
          <div key={index} className="space-y-2">
            <h3 className="text-sm font-semibold text-muted-foreground uppercase px-2">
              {section.title}
            </h3>
            <div className="bg-card rounded-lg border border-border overflow-hidden">
              {section.items.map((item, itemIndex) => {
                const Icon = item.icon;
                return (
                  <button
                    key={itemIndex}
                    onClick={item.onClick}
                    className="w-full flex items-center gap-3 p-4 hover:bg-muted/50 transition-colors border-b border-border last:border-b-0"
                  >
                    <Icon className="w-5 h-5 text-muted-foreground" />
                    <span className="flex-1 text-left">{item.label}</span>
                    {item.value && (
                      <span className="text-sm text-muted-foreground">
                        {item.value}
                      </span>
                    )}
                    <ChevronRight className="w-5 h-5 text-muted-foreground" />
                  </button>
                );
              })}
            </div>
          </div>
        ))}

        {/* Logout Button */}
        {isConnected && (
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 p-4 bg-destructive/10 text-destructive rounded-lg hover:bg-destructive/20 transition-colors font-semibold"
          >
            <LogOut className="w-5 h-5" />
            Se déconnecter
          </button>
        )}

        {/* App Info */}
        <div className="text-center text-sm text-muted-foreground pt-4">
          <p>Traveling</p>
          <p>Version 1.0.0</p>
        </div>
      </div>
    </MobileContainer>
  );
}
