import { NavLink, Outlet } from "react-router-dom";
import { useSession } from "../session";

const navItems = [
  { to: "/app/dashboard", label: "Dashboard" },
  { to: "/app/expenses", label: "Expenses" },
  { to: "/app/budgets", label: "Budgets" },
  { to: "/app/categories", label: "Categories" },
  { to: "/app/automation", label: "Automation" },
  { to: "/app/profile", label: "Profile" }
];

export default function Layout() {
  const { session } = useSession();

  return (
    <div className="shell shell--app">
      <aside className="sidebar">
        <div className="brand-block">
          <p className="eyebrow">Spendwise</p>
          <h1 className="brand-title">Control panel</h1>
          <p className="sidebar-copy">
            Browser users sign in with Google. n8n receives per-user API keys generated here.
          </p>
        </div>

        <nav className="nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `nav__link${isActive ? " nav__link--active" : ""}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="session-chip">
          <span>{session.email || session.name}</span>
        </div>
      </aside>

      <section className="content">
        <Outlet />
      </section>
    </div>
  );
}
