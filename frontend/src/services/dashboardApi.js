const API_BASE_URL = "http://localhost:8080/api";

export async function getDashboardSummary(
  accountId,
  currency,
  startDate,
  endDate
) {
  const params = new URLSearchParams({
    currency,
    startDate,
    endDate,
  });

  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/dashboard?${params}`
  );

  if (!response.ok) {
    throw new Error(`Failed to load dashboard: ${response.status}`);
  }

  return response.json();
}