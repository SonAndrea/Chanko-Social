import type { AdminProfile as AdminProfileData } from "@/types/user";

export default function AdminProfile({ profile }: { profile: AdminProfileData }) {
  return (
    <div>
      <h1>Admin view</h1>
      <ul>
        <li>id: {profile.id}</li>
        <li>username: {profile.username}</li>
        <li>role: {profile.role}</li>
        <li>emailVerified: {String(profile.emailVerified)}</li>
        <li>lastLoginAt: {profile.lastLoginAt ?? "null"}</li>
        <li>createdAt: {profile.createdAt}</li>
      </ul>
    </div>
  );
}
