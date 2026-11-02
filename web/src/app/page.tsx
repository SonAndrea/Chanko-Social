"use client";

// "/": first-load router. Logged-in users go to the home page, everyone else
// lands on the about page (and can still reach /home via the nav).

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useCurrentUser } from "@/hooks/useCurrentUser";

export default function Root() {
  const router = useRouter();
  const { user, loading } = useCurrentUser();

  useEffect(() => {
    if (loading) return;
    router.replace(user ? "/home" : "/about");
  }, [user, loading, router]);

  return null;
}
