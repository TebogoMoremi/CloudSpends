import { useEffect, useState } from "react";

import { getCloudResourcesWithCosts } from "../services/resourceApi";
import { useAccount } from "../context/AccountContext";

function ResourcesPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [startDate, setStartDate] = useState("2026-09-01");
  const [endDate, setEndDate] = useState("2026-09-30");

  async function loadResources() {
    if (!selectedAccountId) {
      setResources([]);
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const data = await getCloudResourcesWithCosts(
        selectedAccountId,
        "USD",
        startDate,
        endDate
      );

      setResources(data);
    } catch (err) {
      setError(err.message);
      setResources([]);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (selectedAccountId) {
      loadResources();
    } else {
      setResources([]);
      setLoading(false);
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedAccountId]);

  function handleApplyPeriod(event) {
    event.preventDefault();

    if (endDate < startDate) {
      setError("End date cannot be before start date.");
      return;
    }

    loadResources();
  }

  const totalCost = resources.reduce(
    (total, resource) =>
      total + Number(resource.cost ?? 0),
    0
  );

  const activeServices = resources.filter(
    (resource) =>
      resource.status === "RUNNING" ||
      resource.status === "AVAILABLE"
  ).length;

  if (accountsLoading) {
    return (
      <main className="main-content">
        <div className="state-message">
          Loading cloud accounts...
        </div>
      </main>
    );
  }

  if (!selectedAccountId) {
    return (
      <main className="main-content">
        <div className="state-message">
          No cloud account selected.
        </div>
      </main>
    );
  }

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <p className="eyebrow">
            Infrastructure
          </p>

          <h1>
            Cloud Resources
          </h1>

          <p className="subtitle">
            {selectedAccount
              ? `Resources for ${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Monitor resources and their associated cloud costs."}
          </p>
        </div>

        <form
          className="date-filter"
          onSubmit={handleApplyPeriod}
        >
          <div className="date-field">
            <label htmlFor="resourceStartDate">
              Start date
            </label>

            <input
              id="resourceStartDate"
              type="date"
              value={startDate}
              onChange={(event) =>
                setStartDate(event.target.value)
              }
            />
          </div>

          <div className="date-field">
            <label htmlFor="resourceEndDate">
              End date
            </label>

            <input
              id="resourceEndDate"
              type="date"
              value={endDate}
              onChange={(event) =>
                setEndDate(event.target.value)
              }
            />
          </div>

          <button
            className="apply-button"
            type="submit"
            disabled={loading}
          >
            {loading ? "Loading..." : "Apply"}
          </button>
        </form>
      </header>

      {error && (
        <div className="error-banner">
          {error}
        </div>
      )}

      <section className="cards">
        <article className="card">
          <div className="card-heading">
            <span>Total Resources</span>
            <span className="card-icon">#</span>
          </div>

          <h2>
            {resources.length}
          </h2>

          <p>
            Resources being monitored
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Resource Spend</span>
            <span className="card-icon">$</span>
          </div>

          <h2>
            ${totalCost.toFixed(2)}
          </h2>

          <p>
            USD for selected period
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Active Services</span>
            <span className="card-icon">↑</span>
          </div>

          <h2>
            {activeServices}
          </h2>

          <p>
            Running or available
          </p>
        </article>
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>
              Resource Inventory
            </h3>

            <p>
              {selectedAccount
                ? `${selectedAccount.accountName} infrastructure and costs for the selected period.`
                : "Infrastructure and costs for the selected period."}
            </p>
          </div>

          <span className="currency-badge">
            USD
          </span>
        </div>

        {loading ? (
          <div className="table-message">
            Loading resources...
          </div>
        ) : resources.length === 0 ? (
          <div className="table-message">
            No cloud resources found for{" "}
            {selectedAccount?.accountName ?? "this account"}.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="resources-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Resource ID</th>
                  <th>Region</th>
                  <th>Status</th>
                  <th>Cost</th>
                </tr>
              </thead>

              <tbody>
                {resources.map((resource) => (
                  <tr key={resource.id}>
                    <td>
                      <div className="resource-name">
                        <div className="resource-icon">
                          {resource.resourceType?.[0] ?? "R"}
                        </div>

                        <div>
                          <strong>
                            {resource.resourceName}
                          </strong>

                          <small>
                            Internal ID: {resource.id}
                          </small>
                        </div>
                      </div>
                    </td>

                    <td>
                      <span className="type-badge">
                        {resource.resourceType}
                      </span>
                    </td>

                    <td className="resource-id">
                      {resource.resourceId}
                    </td>

                    <td>
                      {resource.region}
                    </td>

                    <td>
                      <span
                        className={`status-badge status-${resource.status.toLowerCase()}`}
                      >
                        {resource.status}
                      </span>
                    </td>

                    <td>
                      <strong className="resource-cost">
                        ${Number(resource.cost ?? 0).toFixed(2)}
                      </strong>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </main>
  );
}

export default ResourcesPage;