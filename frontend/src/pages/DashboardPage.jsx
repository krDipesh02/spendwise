import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

function getMonth() {
  return new Date().toISOString().slice(0, 7);
}

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);
  const [categorySummary, setCategorySummary] = useState([]);
  const [month, setMonth] = useState(getMonth());

  useEffect(() => {
    Promise.all([api.getMonthlySummary(month), api.getCategorySummary(month)]).then(
      ([summaryPayload, categoryPayload]) => {
        setSummary(summaryPayload);
        setCategorySummary(categoryPayload);
      }
    );
  }, [month]);

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Overview"
        title="Dashboard"
        description="Your monthly spending at a glance."
        actions={
          <input className="month-input" type="month" value={month} onChange={(event) => setMonth(event.target.value)} />
        }
      />

      <div className="summary">
        <div className="summary__metric">
          <div className="summary__metric-icon">{"\u2197"}</div>
          <span>Total spend</span>
          <strong>{summary?.totalSpend ?? "-"}</strong>
        </div>
        <div className="summary__metric">
          <div className="summary__metric-icon">{"\u2248"}</div>
          <span>Average spend</span>
          <strong>{summary?.averageSpend ?? "-"}</strong>
        </div>
        <div className="summary__metric">
          <div className="summary__metric-icon">{"#"}</div>
          <span>Transactions</span>
          <strong>{summary?.expenseCount ?? "-"}</strong>
        </div>
      </div>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Breakdown</p>
          <h2>Spending by category</h2>
        </div>
        {categorySummary.length ? (
          <ul className="list">
            {categorySummary.map((entry) => {
              const max = Math.max(...categorySummary.map((e) => e.total));
              const pct = max > 0 ? (entry.total / max) * 100 : 0;
              return (
                <li className="list__item" key={entry.categoryName}>
                  <strong>{entry.categoryName}</strong>
                  <div className="category-bar">
                    <div className="category-bar__track">
                      <div className="category-bar__fill" style={{ width: `${pct}%` }} />
                    </div>
                    <span className="category-bar__amount">{entry.total}</span>
                  </div>
                </li>
              );
            })}
          </ul>
        ) : (
          <p className="muted">No spending data found for this month.</p>
        )}
      </section>
    </div>
  );
}
