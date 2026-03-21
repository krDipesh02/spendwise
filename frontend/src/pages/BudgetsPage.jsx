import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

function getMonth() {
  return new Date().toISOString().slice(0, 7);
}

export default function BudgetsPage() {
  const [month, setMonth] = useState(getMonth());
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({ categoryId: "", amount: "" });

  useEffect(() => {
    Promise.all([api.getBudgets(month), api.listCategories()]).then(([budgetPayload, categoryPayload]) => {
      setBudgets(budgetPayload);
      setCategories(categoryPayload);
    });
  }, [month]);

  async function submitBudget(event) {
    event.preventDefault();
    await api.createBudget({
      categoryId: form.categoryId || null,
      month,
      amount: Number(form.amount)
    });
    setForm({ categoryId: "", amount: "" });
    setBudgets(await api.getBudgets(month));
  }

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Planning"
        title="Budgets"
        description="Define monthly category budgets and inspect remaining balance."
        actions={<input className="month-input" type="month" value={month} onChange={(event) => setMonth(event.target.value)} />}
      />

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Create budget</p>
          <h2>Add or replace a monthly cap</h2>
        </div>
        <form className="form-grid" onSubmit={submitBudget}>
          <label className="field">
            <span>Category</span>
            <select
              className="select"
              value={form.categoryId}
              onChange={(event) => setForm({ ...form, categoryId: event.target.value })}
            >
              <option value="">Overall</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>Amount</span>
            <input
              value={form.amount}
              onChange={(event) => setForm({ ...form, amount: event.target.value })}
              type="number"
              min="0"
              step="0.01"
              required
            />
          </label>
          <button className="button button--primary" type="submit">
            Save Budget
          </button>
        </form>
      </section>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Status</p>
          <h2>Current month budgets</h2>
        </div>
        {budgets.length ? (
          <ul className="list">
            {budgets.map((budget) => (
              <li className="list__item" key={budget.budgetId}>
                <div>
                  <strong>{budget.categoryName}</strong>
                  <p>
                    Budget {budget.amount} · Spent {budget.spent} · Remaining {budget.remaining}
                  </p>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="muted">No budgets defined for this month.</p>
        )}
      </section>
    </div>
  );
}
