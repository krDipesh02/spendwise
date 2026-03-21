import { useEffect, useState } from "react";
import { api } from "../api";
import PageHeader from "../components/PageHeader";

export default function CategoriesPage() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState({ name: "", icon: "tag" });

  useEffect(() => {
    reload();
  }, []);

  async function reload() {
    setCategories(await api.listCategories());
  }

  async function createCategory(event) {
    event.preventDefault();
    await api.createCategory(form);
    setForm({ name: "", icon: "tag" });
    await reload();
  }

  async function toggleCategory(category) {
    await api.updateCategory(category.id, {
      name: category.name,
      icon: category.icon,
      active: !category.active
    });
    await reload();
  }

  return (
    <div className="stack-lg">
      <PageHeader
        eyebrow="Classification"
        title="Categories"
        description="Manage the labels used for spend classification."
      />

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">New category</p>
          <h2>Create a label</h2>
        </div>
        <form className="form-grid" onSubmit={createCategory}>
          <label className="field">
            <span>Name</span>
            <input
              value={form.name}
              onChange={(event) => setForm({ ...form, name: event.target.value })}
              required
            />
          </label>
          <label className="field">
            <span>Icon</span>
            <input
              value={form.icon}
              onChange={(event) => setForm({ ...form, icon: event.target.value })}
              required
            />
          </label>
          <button className="button button--primary" type="submit">
            Create Category
          </button>
        </form>
      </section>

      <section className="panel">
        <div className="panel__header">
          <p className="eyebrow">Current</p>
          <h2>Available categories</h2>
        </div>
        <ul className="list">
          {categories.map((category) => (
            <li className="list__item" key={category.id}>
              <div>
                <strong>{category.name}</strong>
                <p>{category.icon}</p>
              </div>
              <button className="button button--ghost button--small" onClick={() => toggleCategory(category)} type="button">
                {category.active ? "Disable" : "Enable"}
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
