const API_BASE_URL = "http://localhost:8080/api";

export async function getBudgets(accountId) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/budgets`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load budgets: ${response.status}`
    );
  }

  return response.json();
}

export async function getBudgetUtilization(
  accountId,
  budgetId,
  startDate,
  endDate
) {
  const params = new URLSearchParams({
    startDate,
    endDate,
  });

  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/budgets/${budgetId}/utilization?${params}`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load budget utilization: ${response.status}`
    );
  }

  return response.json();
}

export async function createBudget(
  accountId,
  budget
) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/budgets`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(budget),
    }
  );

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message ||
        `Failed to create budget: ${response.status}`
    );
  }

  return response.json();
}