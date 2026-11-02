import type { ApiError } from "@/types/user";
import { ApiRequestError } from "./client";

// Turns anything thrown by an API call into an ApiError the forms can display
export function toApiError(e: unknown): ApiError {
  if (e instanceof ApiRequestError) return e.body;
  return {
    status: 0,
    error: "NETWORK_ERROR",
    message: "Could not reach the server",
    fieldErrors: {},
    timestamp: new Date().toISOString(),
  };
}
