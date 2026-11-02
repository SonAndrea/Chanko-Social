"use client";

import { useState, type FormEvent } from "react";
import { useRouter } from "next/navigation";
import { register } from "@/lib/api/auth";
import { toApiError } from "@/lib/api/errors";
import { setCurrentUser } from "@/hooks/useCurrentUser";
import type { ApiError } from "@/types/user";
import FormErrors from "./FormErrors";

export default function SignupForm() {
  const router = useRouter();
  const [error, setError] = useState<ApiError | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: FormEvent<HTMLFormElement>) {
    
    e.preventDefault();
    const form = new FormData(e.currentTarget);
    setSubmitting(true);
    setError(null);

    try {
      const user = await register({
        username: String(form.get("username")),
        email: String(form.get("email")),
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
          Username <input name="username" required autoComplete="username" />
        </label>
      </p>
      <p>
        <label>
          Email <input name="email" type="email" required autoComplete="email" />
        </label>
      </p>
      <p>
        <label>
          Password <input name="password" type="password" required autoComplete="new-password" />
        </label>
      </p>
      <button type="submit" disabled={submitting}>
        Create account
      </button>
    </form>
  );
}
