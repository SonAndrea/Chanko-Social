import type { PrivateProfile as PrivateProfileData } from "@/types/user";

export default function PrivateProfile({ profile }: { profile: PrivateProfileData }) {
  return (
    <div>
      <h1>Your profile</h1>
      <ul>
        <li>id: {profile.id}</li>
        <li>username: {profile.username}</li>
        <li>email: {profile.email}</li>
        <li>role: {profile.role}</li>
        <li>emailVerified: {String(profile.emailVerified)}</li>
        <li>createdAt: {profile.createdAt}</li>
      </ul>
    </div>
  );
}
