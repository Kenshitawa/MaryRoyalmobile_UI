import ChangePasswordForm from "./change-password-form";
import Image from "next/image";
import { ThemeStyleControls } from "@/components/design-system/theme-provider";

export default function Page() {
  return (
    <main className="relative grid min-h-screen place-items-center bg-[radial-gradient(ellipse_at_top_left,_var(--tw-gradient-stops))] from-blue-100/60 via-slate-50 to-white p-5 dark:from-slate-950 dark:via-slate-900 dark:to-slate-950">
      <div className="absolute right-5 top-5">
        <ThemeStyleControls compact />
      </div>

      <div className="w-full max-w-md">
        <div className="mb-6 flex items-center gap-3.5">
          <span className="grid h-12 w-12 place-items-center rounded-2xl bg-white p-1.5 shadow-md shadow-blue-900/10 dark:bg-slate-800">
            <Image
              src="/mary-royal-logo.png"
              width={42}
              height={42}
              alt="Mary Royal School logo"
              priority
              className="object-contain"
            />
          </span>
          <div>
            <p className="font-extrabold tracking-tight text-slate-900 dark:text-white">
              Mary Royal School
            </p>
            <p className="text-xs font-medium text-slate-500 dark:text-slate-400">
              Obunga, Kisumu · First-time Setup
            </p>
          </div>
        </div>

        <ChangePasswordForm />
      </div>
    </main>
  );
}
