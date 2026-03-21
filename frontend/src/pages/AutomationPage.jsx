import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

export default function AutomationPage() {
  const [apiKeys, setApiKeys] = useState([]);
  const [name, setName] = useState("n8n workflow key");
  const [createdKey, setCreatedKey] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    refresh();
  }, []);

  async function refresh() {
    setApiKeys(await api.listApiKeys());
  }

  async function createKey(event) {
    event.preventDefault();
    const payload = await api.createApiKey({ name });
    setCreatedKey(payload.apiKey);
    setName("");
    setMessage("Store this key inside n8n credentials. The full value is only returned once.");
    await refresh();
  }

  async function revokeKey(id) {
    await api.revokeApiKey(id);
    setMessage("API key revoked.");
    await refresh();
  }

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Automation"
        title="API Keys"
        description="Generate and revoke the credentials that n8n uses when calling Spendwise."
      />

      {message ? <div className="status-banner">{message}</div> : null}

      <section className="panel panel--accent">
        <div className="panel__header">
          <p className="eyebrow">Generate</p>
          <h2>Create a user-owned API key</h2>
        </div>
        <form className="stack" onSubmit={createKey}>
          <label className="field">
            <span>Key name</span>
            <input value={name} onChange={(event) => setName(event.target.value)} required />
          </label>
          <button className="button button--primary" type="submit">
            Generate API Key
          </button>
        </form>
        {createdKey ? (
          <div className="secret-box">
            <span>Raw API key</span>
            <code>{createdKey}</code>
          </div>
        ) : null}
      </section>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Manage</p>
          <h2>Active keys</h2>
        </div>
        {apiKeys.length ? (
          <ul className="list">
            {apiKeys.map((key) => (
              <li className="list__item" key={key.id}>
                <div>
                  <strong>{key.name}</strong>
                  <p>
                    {key.keyPrefix} · {key.lastUsedAt ? `Last used ${key.lastUsedAt}` : "Never used"}
                  </p>
                </div>
                <button className="button button--ghost button--small" onClick={() => revokeKey(key.id)} type="button">
                  Revoke
                </button>
              </li>
            ))}
          </ul>
        ) : (
          <p className="muted">No active keys yet.</p>
        )}
      </section>
    </div>
  );
}
