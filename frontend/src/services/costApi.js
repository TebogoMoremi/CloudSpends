const API_BASE_URL = "http://localhost:8080/api";

export async function getAccountCostTotal(
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
    `${API_BASE_URL}/cloud-accounts/${accountId}/costs/total?${params}`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load cost total: ${response.status}`
    );
  }

  return response.json();
}

export async function getAccountCostBreakdown(
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
    `${API_BASE_URL}/cloud-accounts/${accountId}/costs/breakdown?${params}`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load cost breakdown: ${response.status}`
    );
  }

  return response.json();
}
export async function getAccountCostRecords(
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
    `${API_BASE_URL}/cloud-accounts/${accountId}/costs/records?${params}`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load cost records: ${response.status}`
    );
  }

  return response.json();
}