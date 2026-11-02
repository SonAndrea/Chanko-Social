// Mirrored Java DTOs from backend

export type Role = "USER" | "ADMIN";

/** 
 * View of user without permissions 
 */
export interface PublicProfile {
  view: "public";
  username: string;
}

/** 
 * View of a user with Admin permissions
 */
export interface AdminProfile {
  view: "admin";
  id: string;
  username: string;
  role: Role;
  emailVerified: boolean;
  lastLoginAt: string | null; // ISO-8601
  createdAt: string;
}

/** 
 * View of a user of their own p 
 */
export interface PrivateProfile {
  view: "private";
  id: string;
  username: string;
  email: string;
  role: Role;
  emailVerified: boolean;
  createdAt: string;
}

/** 
 * What GET /api/users/{username} can return 
 */
export type Profile = PublicProfile | AdminProfile | PrivateProfile;

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

/** 
 * What a login request shape looks like
 */
export interface LoginRequest {
  login: string; // username or email
  password: string;
}

/** 
 * Mirrors com.chanko.api.common.ApiError
 */
export interface ApiError {
  status: number;
  error: string;
  message: string;
  fieldErrors: Record<string, string>;
  timestamp: string;
}
