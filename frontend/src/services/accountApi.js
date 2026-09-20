const API_BASE_URL = "http://localhost:8080/api";

export async function getCloudAccounts() {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts`
  );

  if (!response.ok) {
    throw new Error(
      `Failed to load cloud accounts: ${response.status}`
    );
  }

  return response.json();
}

export async function createCloudAccount(account) {
  const response = await fetch(
    `${API_BASE_URL}/cloud-accounts`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(account),
    }
  );

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message || `Failed to create account: ${response.status}`
    );
  }

  return response.json();
}