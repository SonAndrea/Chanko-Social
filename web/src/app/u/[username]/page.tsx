import { Suspense } from "react";
import { headers } from "next/headers";
import { notFound } from "next/navigation";
import ProfileView from "@/components/profile/ProfileView";
import { getProfile } from "@/lib/api/users";

export default function UserProfilePage({ params }: PageProps<"/u/[username]">) {
  // With cacheComponents, request data must be read inside a Suspense boundary
  return (
    <main>
      <Suspense fallback={<p>Loading…</p>}>
        <Profile params={params} />
      </Suspense>
    </main>
  );
}

async function Profile({ params }: Pick<PageProps<"/u/[username]">, "params">) {
  const { username } = await params;
  const cookie = (await headers()).get("cookie");
  const profile = await getProfile(username, cookie);

  if (!profile) notFound();

  return <ProfileView profile={profile} />;
}
