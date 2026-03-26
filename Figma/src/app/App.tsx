import { RouterProvider } from 'react-router';
import { router } from './routes';
import { UserModeProvider } from './contexts/UserModeContext';
import { ItineraryProvider } from './contexts/ItineraryContext';

export default function App() {
  return (
    <UserModeProvider>
      <ItineraryProvider>
        <RouterProvider router={router} />
      </ItineraryProvider>
    </UserModeProvider>
  );
}