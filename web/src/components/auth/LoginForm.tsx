"use client";

// Login form: username-or-email + password. On success, go to the user's profile.

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { login } from "@/lib/api/auth";
import { toApiError } from "@/lib/api/errors";
import { setCurrentUser } from "@/hooks/useCurrentUser";
import type { ApiError } from "@/types/user";
import FormErrors from "./FormErrors";

export default function LoginForm() {
  const router = useRouter();
  const [error, setError] = useState<ApiError | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setSubmitting(true);
    setError(null);
    try {
      const user = await login({
        login: String(form.get("login")),
        password: String(form.get("password")),
      });
      setCurrentUser(user);
      router.push(`/u/${user.username}`);
    } catch (err) {
      setError(toApiError(err));
      setSubmitting(false);
    }
  }

  return (
    <form onSubmit={onSubmit}>
      <FormErrors error={error} />
      <p>
        <label>
          Username or email <input name="login" required autoComplete="username" />
        </label>
      </p>
      <p>
        <label>
          Password <input name="password" type="password" required autoComplete="current-password" />
        </label>
      </p>
      <button type="submit" disabled={submitting}>
        Log in
      </button>
      
      <a className="nav-profile-button" href="/signup">Create account</a>
    </form>
  );
}
