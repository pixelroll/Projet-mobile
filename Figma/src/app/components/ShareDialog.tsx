import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "./ui/dialog";
import { Button } from "./ui/button";
import { Share2, Copy, Check, Mail, MessageCircle } from "lucide-react";

interface ShareDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  title: string;
  url: string;
  description?: string;
}

export function ShareDialog({ open, onOpenChange, title, url, description }: ShareDialogProps) {
  const [copied, setCopied] = useState(false);

  const handleCopyLink = async () => {
    try {
      // Try modern Clipboard API first
      if (navigator.clipboard && navigator.clipboard.writeText) {
        await navigator.clipboard.writeText(url);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
      } else {
        // Fallback for browsers/contexts that block Clipboard API
        const textArea = document.createElement("textarea");
        textArea.value = url;
        textArea.style.position = "fixed";
        textArea.style.left = "-999999px";
        textArea.style.top = "-999999px";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        
        try {
          const successful = document.execCommand('copy');
          if (successful) {
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
          }
        } catch (err) {
          console.error('Fallback: Oops, unable to copy', err);
        }
        
        document.body.removeChild(textArea);
      }
    } catch (error) {
      console.error("Failed to copy:", error);
    }
  };

  const handleNativeShare = async () => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: title,
          text: description,
          url: url,
        });
      } catch (error) {
        // User cancelled or error occurred
        console.log("Share cancelled or failed:", error);
      }
    } else {
      // Fallback to copy
      handleCopyLink();
    }
  };

  const handleEmailShare = () => {
    const subject = encodeURIComponent(title);
    const body = encodeURIComponent(`${description}\n\n${url}`);
    window.open(`mailto:?subject=${subject}&body=${body}`, "_blank");
  };

  const handleSMSShare = () => {
    const body = encodeURIComponent(`${title}\n${url}`);
    window.open(`sms:?body=${body}`, "_blank");
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-[calc(100vw-2rem)] sm:max-w-[425px] rounded-xl w-[calc(100vw-2rem)] sm:w-full overflow-hidden flex flex-col max-h-[85vh]">
        <DialogHeader className="shrink-0">
          <DialogTitle className="flex items-center gap-2">
            <Share2 className="w-5 h-5 text-primary" />
            Partager
          </DialogTitle>
          <DialogDescription className="break-words">{title}</DialogDescription>
        </DialogHeader>

        <div className="space-y-3 py-4 overflow-y-auto overflow-x-hidden">
          {/* Native share if available */}
          {navigator.share && (
            <Button
              onClick={handleNativeShare}
              className="w-full justify-start gap-2 h-auto py-3 px-3"
              variant="outline"
            >
              <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
                <Share2 className="w-4 h-4 text-primary" />
              </div>
              <div className="text-left flex-1 min-w-0">
                <p className="font-medium text-sm">Partager via...</p>
                <p className="text-xs text-muted-foreground break-words">
                  Utiliser le menu de partage du système
                </p>
              </div>
            </Button>
          )}

          {/* Copy link */}
          <Button
            onClick={handleCopyLink}
            className="w-full justify-start gap-2 h-auto py-3 px-3"
            variant="outline"
          >
            <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
              {copied ? (
                <Check className="w-4 h-4 text-green-600" />
              ) : (
                <Copy className="w-4 h-4 text-primary" />
              )}
            </div>
            <div className="text-left flex-1 min-w-0">
              <p className="font-medium text-sm">{copied ? "Lien copié !" : "Copier le lien"}</p>
              <p className="text-xs text-muted-foreground truncate">{url}</p>
            </div>
          </Button>

          {/* Email */}
          <Button
            onClick={handleEmailShare}
            className="w-full justify-start gap-2 h-auto py-3 px-3"
            variant="outline"
          >
            <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
              <Mail className="w-4 h-4 text-primary" />
            </div>
            <div className="text-left flex-1 min-w-0">
              <p className="font-medium text-sm">Envoyer par email</p>
              <p className="text-xs text-muted-foreground break-words">Partager via votre client email</p>
            </div>
          </Button>

          {/* SMS */}
          <Button
            onClick={handleSMSShare}
            className="w-full justify-start gap-2 h-auto py-3 px-3"
            variant="outline"
          >
            <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
              <MessageCircle className="w-4 h-4 text-primary" />
            </div>
            <div className="text-left flex-1 min-w-0">
              <p className="font-medium text-sm">Envoyer par SMS</p>
              <p className="text-xs text-muted-foreground break-words">Partager via messages</p>
            </div>
          </Button>
        </div>

        <div className="pt-2 shrink-0 border-t border-border mt-2">
          <Button variant="outline" onClick={() => onOpenChange(false)} className="w-full">
            Fermer
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}