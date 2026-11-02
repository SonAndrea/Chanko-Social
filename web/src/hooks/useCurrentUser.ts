"use client";

import { useEffect, useSyncExternalStore } from "react";
import { getMe } from "@/lib/api/users";
import type { PrivateProfile } from "@/types/user";

interface Snapshot {
  user: PrivateProfile | null; // null when logged out
  loading: boolean;
}

let snapshot: Snapshot = { user: null, loading: true };
let fetched = false;
const listeners = new Set<() => void>();
const serverSnapshot: Snapshot = { user: null, loading: true };

function setSnapshot(next: Snapshot) {
  snapshot = next;
  listeners.forEach((l) => l());
}

function subscribe(listener: () => void) {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

/** 
 * Set the current user directly from the login/register response
 */
export function setCurrentUser(user: PrivateProfile | null) {
  fetched = true;
  setSnapshot({ user, loading: false });
}

/** 
 * Re-fetch /api/users/me
 */
export async function refreshCurrentUser() {
  fetched = true;
  try {
    setCurrentUser(await getMe());
  } catch {
    setCurrentUser(null);
  }
}

/* Represents the logged-in user, shared by every component that calls useCurrentUser()
 * Uses a small module-level store instead of a context provider
 */
export function useCurrentUser() {
  const state = useSyncExternalStore(subscribe, () => snapshot, () => serverSnapshot);

  useEffect(() => {
    if (!fetched) void refreshCurrentUser();
  }, []);

  return { ...state, refresh: refreshCurrentUser };
}
