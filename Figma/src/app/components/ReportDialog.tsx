import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "./ui/dialog";
import { Button } from "./ui/button";
import { Textarea } from "./ui/textarea";
import { Label } from "./ui/label";
import { Flag, AlertTriangle, Ban, ImageOff, UserX } from "lucide-react";

interface ReportDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  photoId?: string | number;
  onReport?: (reason: string, details: string) => void;
}

const reportReasons = [
  { value: "inappropriate", label: "Contenu inapproprié", icon: AlertTriangle },
  { value: "spam", label: "Spam ou publicité", icon: Ban },
  { value: "copyright", label: "Violation de droits d'auteur", icon: ImageOff },
  { value: "harassment", label: "Harcèlement ou intimidation", icon: UserX },
  { value: "other", label: "Autre raison", icon: Flag },
];

export function ReportDialog({ open, onOpenChange, photoId, onReport }: ReportDialogProps) {
  const [selectedReason, setSelectedReason] = useState<string>("");
  const [details, setDetails] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isReported, setIsReported] = useState(false);

  const handleSubmit = async () => {
    if (!selectedReason) return;

    setIsSubmitting(true);

    // Simuler l'envoi du signalement
    await new Promise((resolve) => setTimeout(resolve, 1000));

    // Appeler callback si fourni
    if (onReport) {
      onReport(selectedReason, details);
    }

    setIsReported(true);
    setIsSubmitting(false);

    // Fermer après 2 secondes
    setTimeout(() => {
      onOpenChange(false);
      // Reset après fermeture
      setTimeout(() => {
        setIsReported(false);
        setSelectedReason("");
        setDetails("");
      }, 300);
    }, 2000);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl">
        {!isReported ? (
          <>
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                <Flag className="w-5 h-5 text-destructive" />
                Signaler cette photo
              </DialogTitle>
              <DialogDescription>
                Aidez-nous à maintenir une communauté sûre en signalant le contenu inapproprié.
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-4 py-4">
              {/* Reason selection */}
              <div className="space-y-2">
                <Label>Raison du signalement</Label>
                <div className="space-y-2">
                  {reportReasons.map((reason) => {
                    const Icon = reason.icon;
                    return (
                      <button
                        key={reason.value}
                        onClick={() => setSelectedReason(reason.value)}
                        className={`w-full p-3 rounded-lg border-2 text-left transition-colors flex items-center gap-3 ${
                          selectedReason === reason.value
                            ? "border-destructive bg-destructive/5"
                            : "border-border hover:border-destructive/50"
                        }`}
                      >
                        <Icon className="w-5 h-5 text-destructive flex-shrink-0" />
                        <span className="text-sm font-medium">{reason.label}</span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Additional details */}
              {selectedReason && (
                <div className="space-y-2">
                  <Label htmlFor="details">
                    Détails supplémentaires <span className="text-muted-foreground">(optionnel)</span>
                  </Label>
                  <Textarea
                    id="details"
                    value={details}
                    onChange={(e) => setDetails(e.target.value)}
                    placeholder="Décrivez le problème en détail..."
                    className="resize-none"
                    rows={3}
                  />
                </div>
              )}
            </div>

            <DialogFooter className="flex-col sm:flex-row gap-2">
              <Button
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={isSubmitting}
                className="w-full sm:w-auto"
              >
                Annuler
              </Button>
              <Button
                onClick={handleSubmit}
                disabled={!selectedReason || isSubmitting}
                className="w-full sm:w-auto bg-destructive hover:bg-destructive/90"
              >
                {isSubmitting ? "Envoi..." : "Envoyer le signalement"}
              </Button>
            </DialogFooter>
          </>
        ) : (
          <div className="py-8 text-center">
            <div className="w-16 h-16 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
              <Flag className="w-8 h-8 text-green-600" />
            </div>
            <DialogTitle className="mb-2">Merci pour votre signalement</DialogTitle>
            <DialogDescription>
              Nous allons examiner cette photo dans les plus brefs délais.
            </DialogDescription>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}