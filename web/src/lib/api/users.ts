import type { PrivateProfile, Profile } from "@/types/user";
import { apiFetch, ApiRequestError } from "./client";

/** 
 * GET /api/users/me (resolves to null when not logged in) 
 */
export async function getMe(): Promise<PrivateProfile | null> {
  try {
    return await apiFetch<PrivateProfile>("/api/users/me");
  } catch (e) {
    if (e instanceof ApiRequestError && e.status === 401) return null;
    throw e;
  }
}

/**
 * GET /api/users/{username} (API picks the view from the session)
 * Will resolves to null on 404
 *
 * @param cookie when called on the server, the incoming Cookie header to forward
 */
export async function getProfile(username: string, cookie?: string | null): Promise<Profile | null> {
  try {
    return await apiFetch<Profile>(`/api/users/${encodeURIComponent(username)}`, { cookie });
  } catch (e) {
    if (e instanceof ApiRequestError && e.status === 404) return null;
    throw e;
  }
}
