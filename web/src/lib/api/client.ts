import type { ApiError } from "@/types/user";

const SERVER_API_URL = process.env.API_URL ?? "http://localhost:8080";

/** 
 * Thrown for any non-2xx response, carrying the parsed ApiError body
 */
export class ApiRequestError extends Error {
  readonly body: ApiError;

  constructor(body: ApiError) {
    super(body.message);
    this.body = body;
  }

  get status() {
    return this.body.status;
  }
}

/** 
 * Server-side only where the Cookie header to forward to the API
 */
export interface ApiFetchOptions extends RequestInit {
  cookie?: string | null;
}

/** 
 * Send a JSON request and parse the JSON response (resolves to undefined for empty bodies) 
 */
export async function apiFetch<T>(path: string, { cookie, headers, ...init }: ApiFetchOptions = {}): Promise<T> {
  const base = typeof window === "undefined" ? SERVER_API_URL : "";
  const res = await fetch(base + path, {
    ...init,
    credentials: "same-origin",
    headers: {
      ...(init.body ? { "Content-Type": "application/json" } : {}),
      ...(cookie ? { Cookie: cookie } : {}),
      ...headers,
    },
  });

  const text = await res.text();
  const data = text ? JSON.parse(text) : undefined;

  if (!res.ok) {
    // Spring Security's 401 entry point sends no body, so this builds one
    throw new ApiRequestError(
      data ?? {
        status: res.status,
        error: `HTTP_${res.status}`,
        message: res.statusText || "Request failed",
        fieldErrors: {},
        timestamp: new Date().toISOString(),
      },
    );
  }
  return data as T;
}
