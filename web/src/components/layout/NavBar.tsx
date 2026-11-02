"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { logout } from "@/lib/api/auth";
import { setCurrentUser, useCurrentUser } from "@/hooks/useCurrentUser";
import './NavBar.css';

export default function NavBar() {
  const { user, loading } = useCurrentUser();
  const router = useRouter();

  async function onLogout() {
    await logout();
    setCurrentUser(null);
    // Re-render server components (e.g. the profile page) without the session
    router.refresh();
  }

  return (
    <nav className="nav-body">
      <div className="nav-content-left">
        <Link href="/home">
          <img className="nav-logo" src="/temp_logo.png" />
        </Link>
        <ul className="nav-menu-container">
          <li><a className="nav-button" href="/home">Home</a></li>
          <li><a className="nav-button" href="/about">About</a></li>
        </ul>    
      </div>
      <div className="nav-content-right">
        {loading ? null : user ? (
          <>
            <a className="nav-profile-button" href={`/u/${user.username}`}>{user.username}</a>
            {" | "}
            <button className="nav-profile-button" type="button" onClick={onLogout}>
              Log out
            </button>
          </>
        ) : (
          <>
            <a className="nav-profile-button" href="/login">Log in</a>
          </>
        )}
   
      </div>
        
    </ nav>
  );
}
