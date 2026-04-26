import { createBrowserRouter } from "react-router";
import { Welcome } from "./pages/Welcome";
import { Home } from "./pages/Home";
import { PhotoDetail } from "./pages/PhotoDetail";
import { PublishPhoto } from "./pages/PublishPhoto";
import { TravelPathPreferences } from "./pages/TravelPathPreferences";
import { ItineraryList } from "./pages/ItineraryList";
import { ItineraryDetail } from "./pages/ItineraryDetail";
import { Passerelle } from "./pages/Passerelle";
import { Notifications } from "./pages/Notifications";
import { Profile } from "./pages/Profile";
import { TripMap } from "./pages/TripMap";
import { AdvancedSearch } from "./pages/AdvancedSearch";
import { NotificationSettings } from "./pages/NotificationSettings";
import { EditProfile } from "./pages/EditProfile";
import { Settings } from "./pages/Settings";
import { NotFound } from "./pages/NotFound";
import { PhotoMap } from "./pages/PhotoMap";
import { Groups } from "./pages/Groups";
import { GroupAdmin } from "./pages/GroupAdmin";
import { GroupFeed } from "./pages/GroupFeed";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Welcome,
  },
  {
    path: "/home",
    Component: Home,
  },
  {
    path: "/photo/:id",
    Component: PhotoDetail,
  },
  {
    path: "/publish",
    Component: PublishPhoto,
  },
  {
    path: "/search",
    Component: AdvancedSearch,
  },
  {
    path: "/travelpath/preferences",
    Component: TravelPathPreferences,
  },
  {
    path: "/travelpath/itineraries",
    Component: ItineraryList,
  },
  {
    path: "/travelpath/itinerary/:id",
    Component: ItineraryDetail,
  },
  {
    path: "/passerelle",
    Component: Passerelle,
  },
  {
    path: "/notifications",
    Component: Notifications,
  },
  {
    path: "/notifications/settings",
    Component: NotificationSettings,
  },
  {
    path: "/profile",
    Component: Profile,
  },
  {
    path: "/profile/edit",
    Component: EditProfile,
  },
  {
    path: "/settings",
    Component: Settings,
  },
  {
    path: "/map",
    Component: PhotoMap,
  },
  {
    path: "/groups",
    Component: Groups,
  },
  {
    path: "/groups/:groupId",
    Component: GroupFeed,
  },
  {
    path: "/groups/:groupId/admin",
    Component: GroupAdmin,
  },
  {
    path: "/trip/:id",
    Component: TripMap,
  },
  {
    path: "*",
    Component: NotFound,
  },
]);