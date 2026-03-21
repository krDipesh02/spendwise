import { Link } from "react-router-dom";
import { useSession } from "../session";

export default function LandingPage() {
  const { loading, session, refreshSession } = useSession();

  return (
    <div className="shell">
      <header className="hero">
        <div className="hero__copy">
          <p className="eyebrow">Spendwise</p>
          <h1>Personal finance UI for people, secure API keys for n8n.</h1>
          <p className="lede">
            Sign in with Google to manage your spending data from the browser, then issue
            user-owned API keys for the automation workflows that call the backend.
          </p>
          <div className="hero__actions">
            <a className="button button--primary" href="/oauth2/authorization/google">
              Sign In With Google
            </a>
            <button className="button button--ghost" onClick={refreshSession} type="button">
              Refresh Session
            </button>
            {session.authenticated ? (
              <Link className="button button--ghost" to="/app/dashboard">
                Open App
              </Link>
            ) : null}
          </div>
        </div>

        <div className="hero__card">
          <span className="hero__card-label">Status</span>
          <strong>{loading ? "Checking session" : session.authenticated ? "Authenticated" : "Signed out"}</strong>
          <p>
            {session.authenticated
              ? `${session.email || session.name} is ready for dashboard access.`
              : "Use Google OAuth to enter the frontend and create API keys."}
          </p>
        </div>
      </header>
    </div>
  );
}
