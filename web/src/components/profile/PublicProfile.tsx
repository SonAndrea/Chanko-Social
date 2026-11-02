import type { PublicProfile as PublicProfileData } from "@/types/user";

export default function PublicProfile({ profile }: { profile: PublicProfileData }) {
  return (
    <div>
      <h1>Public profile</h1>
      <ul>
        <li>username: {profile.username}</li>
      </ul>
    </div>
  );
}
