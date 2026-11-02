import type { Profile } from "@/types/user";
import AdminProfile from "./AdminProfile";
import PrivateProfile from "./PrivateProfile";
import PublicProfile from "./PublicProfile";

export default function ProfileView({ profile }: { profile: Profile }) {
  switch (profile.view) {
    case "private":
      return <PrivateProfile profile={profile} />;
    case "admin":
      return <AdminProfile profile={profile} />;
    case "public":
      return <PublicProfile profile={profile} />;
  }
}
