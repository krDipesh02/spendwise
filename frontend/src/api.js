const JSON_HEADERS = {
  "Content-Type": "application/json"
};

const BACKEND_BASE_PATH = "/api/v1";

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
  getSession: () => request(`${BACKEND_BASE_PATH}/auth/session`),
  loginWithPassword: (body) =>
    request(`${BACKEND_BASE_PATH}/auth/password/login`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  registerWithPassword: (body) =>
    request(`${BACKEND_BASE_PATH}/auth/password/register`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  requestPasswordReset: (body) =>
    request(`${BACKEND_BASE_PATH}/auth/password/reset/request`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  confirmPasswordReset: (body) =>
    request(`${BACKEND_BASE_PATH}/auth/password/reset/confirm`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  logout: () =>
    request(`${BACKEND_BASE_PATH}/auth/logout`, {
      method: "POST"
    }),
  getProfile: () => request(`${BACKEND_BASE_PATH}/profile`),
  updateProfile: (body) =>
    request(`${BACKEND_BASE_PATH}/profile`, {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  listExpenses: (query = "") => request(`${BACKEND_BASE_PATH}/expenses${query}`),
  createExpense: (body) =>
    request(`${BACKEND_BASE_PATH}/expenses`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  updateExpense: (id, body) =>
    request(`${BACKEND_BASE_PATH}/expenses/${id}`, {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  deleteExpense: (id) =>
    request(`${BACKEND_BASE_PATH}/expenses/${id}`, {
      method: "DELETE"
    }),
  listCategories: () => request(`${BACKEND_BASE_PATH}/categories`),
  createCategory: (body) =>
    request(`${BACKEND_BASE_PATH}/categories`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  updateCategory: (id, body) =>
    request(`${BACKEND_BASE_PATH}/categories/${id}`, {
      method: "PUT",
      body: JSON.stringify(body)
    }),
  getBudgets: (month) => request(`${BACKEND_BASE_PATH}/budgets?month=${month}`),
  createBudget: (body) =>
    request(`${BACKEND_BASE_PATH}/budgets`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  getMonthlySummary: (month) => request(`${BACKEND_BASE_PATH}/analytics/monthly-summary?month=${month}`),
  getCategorySummary: (month) => request(`${BACKEND_BASE_PATH}/analytics/category-summary?month=${month}`),
  getTrend: (from, to) => request(`${BACKEND_BASE_PATH}/analytics/trend?from=${from}&to=${to}`),
  listApiKeys: () => request(`${BACKEND_BASE_PATH}/api-keys`),
  createApiKey: (body) =>
    request(`${BACKEND_BASE_PATH}/api-keys`, {
      method: "POST",
      body: JSON.stringify(body)
    }),
  revokeApiKey: (id) =>
    request(`${BACKEND_BASE_PATH}/api-keys/${id}`, {
      method: "DELETE"
    })
};
