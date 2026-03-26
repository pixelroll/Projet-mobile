import { createContext, useContext, useState, ReactNode } from 'react';

type UserMode = 'anonymous' | 'connected';

interface UserModeContextType {
  mode: UserMode;
  setMode: (mode: UserMode) => void;
  isConnected: boolean;
  user: {
    name: string;
    email: string;
    avatar: string;
  } | null;
}

const UserModeContext = createContext<UserModeContextType | undefined>(undefined);

export function UserModeProvider({ children }: { children: ReactNode }) {
  const [mode, setMode] = useState<UserMode>('anonymous');
  
  // Mock user data for connected mode
  const user = mode === 'connected' ? {
    name: 'Sophie Martin',
    email: 'sophie.martin@email.com',
    avatar: 'https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&w=200'
  } : null;

  return (
    <UserModeContext.Provider value={{ 
      mode, 
      setMode, 
      isConnected: mode === 'connected',
      user 
    }}>
      {children}
    </UserModeContext.Provider>
  );
}

export function useUserMode() {
  const context = useContext(UserModeContext);
  if (context === undefined) {
    throw new Error('useUserMode must be used within a UserModeProvider');
  }
  return context;
}
