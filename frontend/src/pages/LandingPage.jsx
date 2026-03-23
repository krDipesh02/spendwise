import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api";
import { useSession } from "../session";

export default function LandingPage() {
  const { loading, session, refreshSession } = useSession();
  const navigate = useNavigate();
  const [loginForm, setLoginForm] = useState({ username: "", password: "" });
  const [registerForm, setRegisterForm] = useState({ username: "", password: "", displayName: "" });
  const [resetRequest, setResetRequest] = useState({ username: "" });
  const [resetConfirm, setResetConfirm] = useState({ token: "", newPassword: "" });
  const [message, setMessage] = useState("");
  const [errors, setErrors] = useState({});
  const [authMode, setAuthMode] = useState("login");
  const [showReset, setShowReset] = useState(false);

  function validateLogin() {
    const nextErrors = {};
    if (loginForm.username.trim().length < 3) nextErrors.loginUsername = "Username must be at least 3 characters.";
    if (loginForm.password.length < 8) nextErrors.loginPassword = "Password must be at least 8 characters.";
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function validateRegister() {
    const nextErrors = {};
    if (registerForm.displayName.trim().length < 2) nextErrors.registerDisplayName = "Display name is too short.";
    if (registerForm.username.trim().length < 3) nextErrors.registerUsername = "Username must be at least 3 characters.";
    if (registerForm.password.length < 8) nextErrors.registerPassword = "Password must be at least 8 characters.";
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function validateResetRequest() {
    const nextErrors = {};
    if (resetRequest.username.trim().length < 3) nextErrors.resetUsername = "Username must be at least 3 characters.";
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  function validateResetConfirm() {
    const nextErrors = {};
    if (resetConfirm.token.trim().length < 10) nextErrors.resetToken = "Token looks too short.";
    if (resetConfirm.newPassword.length < 8) nextErrors.resetPassword = "Password must be at least 8 characters.";
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  }

  async function handleAuthSubmit(event) {
    event.preventDefault();
    if (authMode === "login") {
      if (!validateLogin()) return;
      try {
        await api.loginWithPassword(loginForm);
        setMessage("Logged in with username/password.");
        setShowReset(false);
        await refreshSession();
        navigate("/app/dashboard");
      } catch (error) {
        const errorMessage = error.message || "Login failed.";
        setMessage(errorMessage);
        if (errorMessage.toLowerCase().includes("invalid")) {
          setShowReset(true);
        }
      }
      return;
    }

    if (!validateRegister()) return;
    try {
      await api.registerWithPassword(registerForm);
      setMessage("Account created and logged in.");
      setAuthMode("login");
      await refreshSession();
      navigate("/app/dashboard");
    } catch (error) {
      setMessage(error.message || "Registration failed.");
    }
  }

  async function handleResetRequest(event) {
    event.preventDefault();
    if (!validateResetRequest()) return;
    try {
      const payload = await api.requestPasswordReset(resetRequest);
      setMessage(`Reset token: ${payload.token} (expires ${payload.expiresAt})`);
      setResetConfirm({ ...resetConfirm, token: payload.token });
    } catch (error) {
      setMessage(error.message || "Reset request failed.");
    }
  }

  async function handleResetConfirm(event) {
    event.preventDefault();
    if (!validateResetConfirm()) return;
    try {
      await api.confirmPasswordReset(resetConfirm);
      setMessage("Password reset complete. You can log in now.");
      setResetConfirm({ token: "", newPassword: "" });
    } catch (error) {
      setMessage(error.message || "Reset confirm failed.");
    }
  }

  return (
    <div className="shell landing-grid">
      <section className="landing-left">
        <div className="hero__copy">
          <p className="eyebrow">Spendwise</p>
          <h1>Personal finance UI for people, secure API keys for n8n.</h1>
          <p className="lede">
            Track spending, automate workflows, and keep a personal ledger that works both with a browser UI and
            your n8n agent orchestration.
          </p>
          <div className="status-banner">
            <strong>{loading ? "Checking session" : session.authenticated ? "Authenticated" : "Signed out"}</strong>
            <p className="muted">
              {session.authenticated
                ? `${session.email || session.name || session.username} is ready for dashboard access.`
                : "Sign in to reach your dashboard and automation tools."}
            </p>
          </div>
          {message ? <p className="status-banner">{message}</p> : null}
        </div>

        <div className="placeholder-card">
          <div className="placeholder-image" />
          <div className="placeholder-copy">
            <p className="eyebrow">Preview</p>
            <h2>Spendwise workspace</h2>
            <p className="muted">This is where your dashboard, budgets, and automation controls live.</p>
          </div>
        </div>
      </section>

      <section className="landing-right">
        <div className="panel auth-panel stack-lg">
          <div className="panel__header">
            <p className="eyebrow">{authMode === "login" ? "Sign in" : "Sign up"}</p>
            <h2>{authMode === "login" ? "Username & password" : "Create your account"}</h2>
          </div>

          <form className="form-grid" onSubmit={handleAuthSubmit}>
            {authMode === "signup" ? (
              <label className="field">
                <span>Display name</span>
                <input
                  value={registerForm.displayName}
                  onChange={(event) => setRegisterForm({ ...registerForm, displayName: event.target.value })}
                  required
                />
                {errors.registerDisplayName ? <span className="field-error">{errors.registerDisplayName}</span> : null}
              </label>
            ) : null}
            <label className="field">
              <span>Username</span>
              <input
                value={authMode === "login" ? loginForm.username : registerForm.username}
                onChange={(event) =>
                  authMode === "login"
                    ? setLoginForm({ ...loginForm, username: event.target.value })
                    : setRegisterForm({ ...registerForm, username: event.target.value })
                }
                required
              />
              {authMode === "login" && errors.loginUsername ? (
                <span className="field-error">{errors.loginUsername}</span>
              ) : null}
              {authMode === "signup" && errors.registerUsername ? (
                <span className="field-error">{errors.registerUsername}</span>
              ) : null}
            </label>
            <label className="field">
              <span>Password</span>
              <input
                type="password"
                value={authMode === "login" ? loginForm.password : registerForm.password}
                onChange={(event) =>
                  authMode === "login"
                    ? setLoginForm({ ...loginForm, password: event.target.value })
                    : setRegisterForm({ ...registerForm, password: event.target.value })
                }
                required
              />
              {authMode === "login" && errors.loginPassword ? (
                <span className="field-error">{errors.loginPassword}</span>
              ) : null}
              {authMode === "signup" && errors.registerPassword ? (
                <span className="field-error">{errors.registerPassword}</span>
              ) : null}
            </label>
            <button className="button button--primary" type="submit">
              {authMode === "login" ? "Log In" : "Sign Up"}
            </button>
          </form>

          {authMode === "login" && showReset ? (
            <div className="reset-panel stack">
              <p className="eyebrow">Forgot password?</p>
              <form className="form-grid" onSubmit={handleResetRequest}>
                <label className="field field--wide">
                  <span>Username</span>
                  <input
                    value={resetRequest.username}
                    onChange={(event) => setResetRequest({ username: event.target.value })}
                    required
                  />
                  {errors.resetUsername ? <span className="field-error">{errors.resetUsername}</span> : null}
                </label>
                <button className="button button--ghost" type="submit">
                  Request reset token
                </button>
              </form>

              <form className="form-grid" onSubmit={handleResetConfirm}>
                <label className="field">
                  <span>Reset token</span>
                  <input
                    value={resetConfirm.token}
                    onChange={(event) => setResetConfirm({ ...resetConfirm, token: event.target.value })}
                    required
                  />
                  {errors.resetToken ? <span className="field-error">{errors.resetToken}</span> : null}
                </label>
                <label className="field">
                  <span>New password</span>
                  <input
                    type="password"
                    value={resetConfirm.newPassword}
                    onChange={(event) => setResetConfirm({ ...resetConfirm, newPassword: event.target.value })}
                    required
                  />
                  {errors.resetPassword ? <span className="field-error">{errors.resetPassword}</span> : null}
                </label>
                <button className="button button--primary" type="submit">
                  Confirm reset
                </button>
              </form>
            </div>
          ) : null}

          <div className="auth-divider">
            <span>or</span>
          </div>

          <div className="auth-alt-actions">
            <a className="button button--primary" href="/oauth2/authorization/google">
              Sign In With Google
            </a>
            <button
              className="button button--ghost"
              onClick={() => {
                const nextMode = authMode === "login" ? "signup" : "login";
                setAuthMode(nextMode);
                setErrors({});
                setMessage("");
                setShowReset(false);
              }}
              type="button"
            >
              {authMode === "login" ? "Sign Up" : "Back to Login"}
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}
