import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { api } from "./api";

const SessionContext = createContext(null);

export function SessionProvider({ children }) {
  const [session, setSession] = useState({ authenticated: false });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    refreshSession();
  }, []);

  async function refreshSession() {
    try {
      const payload = await api.getSession();
      setSession(payload);
    } catch {
      setSession({ authenticated: false });
    } finally {
      setLoading(false);
    }
  }

  const value = useMemo(
    () => ({
      session,
      loading,
      refreshSession
    }),
    [session, loading]
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession() {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error("useSession must be used within SessionProvider");
  }
  return context;
}
