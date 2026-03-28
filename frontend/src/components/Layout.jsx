import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { api } from "../api";
import { useSession } from "../session";

const navItems = [
  { to: "/app/dashboard", label: "Dashboard", icon: "\u2302" },
  { to: "/app/expenses", label: "Expenses", icon: "\u2197" },
  { to: "/app/budgets", label: "Budgets", icon: "\u25CE" },
  { to: "/app/categories", label: "Categories", icon: "\u2630" },
  { to: "/app/automation", label: "API Keys", icon: "\u26BF" },
  { to: "/app/profile", label: "Profile", icon: "\u2699" }
];

export default function Layout() {
  const { session, refreshSession } = useSession();
  const navigate = useNavigate();

  async function handleLogout() {
    await api.logout();
    await refreshSession();
    navigate("/");
  }

  return (
    <div className="shell shell--app">
      <aside className="sidebar">
        <div className="brand-block">
          <p className="eyebrow">Spendwise</p>
          <h1 className="brand-title">Dashboard</h1>
          <p className="sidebar-copy">
            Track your spending, manage budgets, and stay on top of your finances.
          </p>
        </div>

        <nav className="nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `nav__link${isActive ? " nav__link--active" : ""}`}
            >
              <span className="nav__icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="session-chip">
          <span>{session.email || session.name || session.username}</span>
          <button className="button button--ghost button--small" onClick={handleLogout} type="button">
            Log out
          </button>
        </div>
      </aside>

      <section className="content">
        <Outlet />
      </section>
    </div>
  );
}
