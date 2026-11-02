import type { ApiError } from "@/types/user";

// Acts as a plain text list of API error message
export default function FormErrors({ error }: { error: ApiError | null }) {
  if (!error) return null;
  const fields = Object.entries(error.fieldErrors ?? {});
  return (
    <div role="alert">
      <p>{error.message}</p>
      {fields.length > 0 && (
        <ul>
          {fields.map(([field, message]) => (
            <li key={field}>
              {field}: {message}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
