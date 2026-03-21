import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

export default function ProfilePage() {
  const [form, setForm] = useState({
    displayName: "",
    baseCurrency: "INR",
    timezone: "Asia/Kolkata",
    monthlyLimit: "0"
  });
  const [message, setMessage] = useState("");

  useEffect(() => {
    api.getProfile().then((profile) => {
      setForm({
        displayName: profile.displayName || "",
        baseCurrency: profile.baseCurrency || "INR",
        timezone: profile.timezone || "Asia/Kolkata",
        monthlyLimit: profile.monthlyLimit ?? "0"
      });
    });
  }, []);

  async function saveProfile(event) {
    event.preventDefault();
    await api.updateProfile({
      ...form,
      monthlyLimit: Number(form.monthlyLimit)
    });
    setMessage("Profile updated.");
  }

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Settings"
        title="Profile"
        description="Maintain the base user settings used across the app."
      />

      {message ? <div className="status-banner">{message}</div> : null}

      <section className="panel">
        <form className="form-grid" onSubmit={saveProfile}>
          <label className="field">
            <span>Display name</span>
            <input
              value={form.displayName}
              onChange={(event) => setForm({ ...form, displayName: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Base currency</span>
            <input
              value={form.baseCurrency}
              onChange={(event) => setForm({ ...form, baseCurrency: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Timezone</span>
            <input
              value={form.timezone}
              onChange={(event) => setForm({ ...form, timezone: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Monthly limit</span>
            <input
              value={form.monthlyLimit}
              onChange={(event) => setForm({ ...form, monthlyLimit: event.target.value })}
              type="number"
              min="0"
              step="0.01"
              required
            />
          </label>
          <button className="button button--primary" type="submit">
            Save Profile
          </button>
        </form>
      </section>
    </div>
  );
}
