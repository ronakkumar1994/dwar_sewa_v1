import { BrandLogo } from "@/components/BrandLogo";

export function Splash() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-4 bg-secondary text-secondary-foreground">
      <BrandLogo className="w-64 max-w-[80%]" />
      <div className="h-1 w-24 overflow-hidden rounded-full bg-secondary-foreground/20">
        <div className="h-full w-1/2 animate-pulse rounded-full bg-primary" />
      </div>
    </div>
  );
}
