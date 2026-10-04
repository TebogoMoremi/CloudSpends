const API_BASE_URL = "http://localhost:8080/api";

export async function getAlerts(accountId) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/alerts`
  );

  if (!response.ok) {
    throw new Error("Failed to load alerts");
  }

  return response.json();
}

export async function getOpenAlerts(accountId) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/alerts/open`
  );

  if (!response.ok) {
    throw new Error("Failed to load open alerts");
  }

  return response.json();
}

export async function evaluateAlerts(
  accountId,
  startDate,
  endDate
) {
  const params = new URLSearchParams({
    startDate,
    endDate,
  });

  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/alerts/evaluate?${params}`,
    {
      method: "POST",
    }
  );

  if (!response.ok) {
    throw new Error("Failed to evaluate alerts");
  }

  return response.json();
}

export async function resolveAlert(
  accountId,
  alertId
) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/alerts/${alertId}/resolve`,
    {
      method: "PATCH",
    }
  );

  if (!response.ok) {
    throw new Error("Failed to resolve alert");
  }

  return response.json();
}