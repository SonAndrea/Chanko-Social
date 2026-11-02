import type { LoginRequest, PrivateProfile, RegisterRequest } from "@/types/user";
import { apiFetch } from "./client";

/** 
 * POST /api/auth/register (also logs in user) 
 */
export function register(body: RegisterRequest): Promise<PrivateProfile> {
  return apiFetch("/api/auth/register", { method: "POST", body: JSON.stringify(body) });
}

/** 
 * POST /api/auth/login (directs to user's profile)
 */
export function login(body: LoginRequest): Promise<PrivateProfile> {
  return apiFetch("/api/auth/login", { method: "POST", body: JSON.stringify(body) });
}

/** 
 * POST /api/auth/logout (closes the session) 
 */
export function logout(): Promise<void> {
  return apiFetch("/api/auth/logout", { method: "POST" });
}
