const API_BASE_URL = "http://localhost:8080/api";

export async function getCloudResources(accountId) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts/${accountId}/resources`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load resources: ${response.status}`
    );
  }

  return response.json();
}

export async function getCloudResourcesWithCosts(
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
    `${API_BASE_URL}/cloud-accounts/${accountId}/resources/with-costs?${params}`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load resource costs: ${response.status}`
    );
  }

  return response.json();
}