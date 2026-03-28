import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

const initialForm = {
  categoryId: "",
  amount: "",
  currency: "INR",
  spentAt: new Date().toISOString().slice(0, 10),
  merchant: "",
  description: ""
};

export default function ExpensesPage() {
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [message, setMessage] = useState("");

  useEffect(() => {
    reload();
  }, []);

  async function reload() {
    const [expensesPayload, categoriesPayload] = await Promise.all([api.listExpenses(), api.listCategories()]);
    setExpenses(expensesPayload);
    setCategories(categoriesPayload);
  }

  async function submitExpense(event) {
    event.preventDefault();
    await api.createExpense({
      ...form,
      categoryId: form.categoryId || null,
      receiptId: null,
      amount: Number(form.amount)
    });
    setForm(initialForm);
    setMessage("Expense recorded.");
    await reload();
  }

  async function deleteExpense(id) {
    await api.deleteExpense(id);
    setMessage("Expense deleted.");
    await reload();
  }

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Transactions"
        title="Expenses"
        description="Create and manage personal spending records."
      />

      {message ? <div className="status-banner">{message}</div> : null}

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">New expense</p>
          <h2>Add an entry</h2>
        </div>
        <form className="form-grid" onSubmit={submitExpense}>
          <label className="field">
            <span>Amount</span>
            <input
              value={form.amount}
              onChange={(event) => setForm({ ...form, amount: event.target.value })}
              required
              type="number"
              min="0"
              step="0.01"
            />
          </label>
          <label className="field">
            <span>Currency</span>
            <input
              value={form.currency}
              onChange={(event) => setForm({ ...form, currency: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Date</span>
            <input
              value={form.spentAt}
              onChange={(event) => setForm({ ...form, spentAt: event.target.value })}
              required
              type="date"
            />
          </label>
          <label className="field">
            <span>Category</span>
            <select
              className="select"
              value={form.categoryId}
              onChange={(event) => setForm({ ...form, categoryId: event.target.value })}
            >
              <option value="">Uncategorized</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>
          <label className="field">
            <span>Merchant</span>
            <input
              value={form.merchant}
              onChange={(event) => setForm({ ...form, merchant: event.target.value })}
            />
          </label>
          <label className="field field--wide">
            <span>Description</span>
            <input
              value={form.description}
              onChange={(event) => setForm({ ...form, description: event.target.value })}
            />
          </label>
          <button className="button button--primary" type="submit">
            Save Expense
          </button>
        </form>
      </section>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">History</p>
          <h2>Recent expenses</h2>
        </div>
        {expenses.length ? (
          <ul className="list">
            {expenses.map((expense) => (
              <li className="list__item" key={expense.id}>
                <div>
                  <strong>{expense.merchant || expense.categoryName || "Expense"}</strong>
                  <p>
                    {expense.spentAt} · <span className="tag">{expense.categoryName || "Uncategorized"}</span> · {expense.description || "No note"}
                  </p>
                </div>
                <div className="list__actions">
                  <strong>
                    {expense.amount} {expense.currency}
                  </strong>
                  <button className="button button--ghost button--small" onClick={() => deleteExpense(expense.id)} type="button">
                    Delete
                  </button>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="muted">No expenses recorded yet.</p>
        )}
      </section>
    </div>
  );
}
