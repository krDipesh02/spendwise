const JSON_HEADERS = {
  "Content-Type": "application/json"
};

async function request(path, options = {}) {
  const response = await fetch(path, {
    credentials: "include",
    ...options,
    headers: {
      ...(options.body ? JSON_HEADERS : {}),
      ...(options.headers || {})
    }
  });

  if (response.status === 204) {
    return null;
  }

  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      typeof payload === "string"
        ? payload
        : payload?.message || payload?.error || "Request failed";
    throw new Error(message);
  }

  return payload;
}

export const api = {
  getSession: () => request("/auth/session"),
  loginWithPassword: (body) =>
    request("/auth/password/login", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  registerWithPassword: (body) =>
    request("/auth/password/register", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  requestPasswordReset: (body) =>
    request("/auth/password/reset/request", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  confirmPasswordReset: (body) =>
    request("/auth/password/reset/confirm", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  logout: () =>
    request("/auth/logout", {
      method: "POST"
    }),
  getProfile: () => request("/api/profile"),
  updateProfile: (body) =>
    request("/api/profile", {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  listExpenses: (query = "") => request(`/api/expenses${query}`),
  createExpense: (body) =>
    request("/api/expenses", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  updateExpense: (id, body) =>
    request(`/api/expenses/${id}`, {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  deleteExpense: (id) =>
    request(`/api/expenses/${id}`, {
      method: "DELETE"
    }),
  listCategories: () => request("/api/categories"),
  createCategory: (body) =>
    request("/api/categories", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  updateCategory: (id, body) =>
    request(`/api/categories/${id}`, {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  getBudgets: (month) => request(`/api/budgets?month=${month}`),
  createBudget: (body) =>
    request("/api/budgets", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  getMonthlySummary: (month) => request(`/api/analytics/monthly-summary?month=${month}`),
  getCategorySummary: (month) => request(`/api/analytics/category-summary?month=${month}`),
  getTrend: (from, to) => request(`/api/analytics/trend?from=${from}&to=${to}`),
  listApiKeys: () => request("/api/api-keys"),
  createApiKey: (body) =>
    request("/api/api-keys", {
      method: "POST",
      body: JSON.stringify(body)
    }),
  revokeApiKey: (id) =>
    request(`/api/api-keys/${id}`, {
      method: "DELETE"
    })
};
