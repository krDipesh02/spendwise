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
        description="Quick view of monthly activity from the browser-authenticated account."
        actions={
          <input className="month-input" type="month" value={month} onChange={(event) => setMonth(event.target.value)} />
        }
      />

      <div className="summary">
        <div className="summary__metric">
          <span>Total spend</span>
          <strong>{summary?.totalSpend ?? "-"}</strong>
        </div>
        <div className="summary__metric">
          <span>Average spend</span>
          <strong>{summary?.averageSpend ?? "-"}</strong>
        </div>
        <div className="summary__metric">
          <span>Expense count</span>
          <strong>{summary?.expenseCount ?? "-"}</strong>
        </div>
      </div>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Breakdown</p>
          <h2>Category totals</h2>
        </div>
        {categorySummary.length ? (
          <ul className="list">
            {categorySummary.map((entry) => (
              <li className="list__item" key={entry.categoryName}>
                <strong>{entry.categoryName}</strong>
                <span>{entry.total}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p className="muted">No spending data found for this month.</p>
        )}
      </section>
    </div>
  );
}
