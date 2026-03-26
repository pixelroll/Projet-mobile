import { ReactNode } from "react";

interface MobileContainerProps {
  children: ReactNode;
  className?: string;
}

export function MobileContainer({ children, className = "" }: MobileContainerProps) {
  return (
    <div className="min-h-screen bg-muted flex justify-center">
      <div className={`w-full max-w-md bg-white shadow-2xl ${className}`}>
        {children}
      </div>
    </div>
  );
}