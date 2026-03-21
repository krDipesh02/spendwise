import { Navigate } from "react-router-dom";
import { useSession } from "../session";

export default function ProtectedRoute({ children }) {
  const { loading, session } = useSession();

  if (loading) {
    return <div className="shell loading-screen">Loading session...</div>;
  }

  if (!session.authenticated) {
    return <Navigate to="/" replace />;
  }

  return children;
}
