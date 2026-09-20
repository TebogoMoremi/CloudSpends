import { useEffect, useState } from "react";

import { useAccount } from "../context/AccountContext";

import {
  evaluateAlerts,
  getAlerts,
  resolveAlert,
} from "../services/alertApi";

function AlertsPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] =
    useState(false);
  const [resolvingId, setResolvingId] =
    useState(null);
  const [error, setError] = useState("");

  const [startDate, setStartDate] =
    useState("2026-09-01");

  const [endDate, setEndDate] =
    useState("2026-09-30");

  async function loadAlerts() {
    if (!selectedAccountId) {
      setAlerts([]);
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const data =
        await getAlerts(selectedAccountId);

      setAlerts(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAlerts();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedAccountId]);

  async function handleEvaluate(event) {
    event.preventDefault();

    if (!selectedAccountId) {
      return;
    }

    if (endDate < startDate) {
      setError(
        "End date cannot be before start date."
      );
      return;
    }

    try {
      setEvaluating(true);
      setError("");

      const data = await evaluateAlerts(
        selectedAccountId,
        startDate,
        endDate
      );

      setAlerts(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setEvaluating(false);
    }
  }

  async function handleResolve(alertId) {
    try {
      setResolvingId(alertId);
      setError("");

      await resolveAlert(
        selectedAccountId,
        alertId
      );

      await loadAlerts();
    } catch (err) {
      setError(err.message);
    } finally {
      setResolvingId(null);
    }
  }

  function formatDate(dateValue) {
    if (!dateValue) {
      return "—";
    }

    return new Date(dateValue).toLocaleString();
  }

  const openAlerts =
    alerts.filter(
      (alert) => alert.status === "OPEN"
    );

  const warningCount =
    openAlerts.filter(
      (alert) => alert.severity === "WARNING"
    ).length;

  const criticalCount =
    openAlerts.filter(
      (alert) => alert.severity === "CRITICAL"
    ).length;

  const resolvedCount =
    alerts.filter(
      (alert) => alert.status === "RESOLVED"
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
            Monitoring
          </p>

          <h1>Alerts</h1>

          <p className="subtitle">
            {selectedAccount
              ? `${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Monitor cloud spending alerts."}
          </p>
        </div>
      </header>

      {error && (
        <div className="error-banner">
          {error}
        </div>
      )}

      <form
        className="date-filter alert-date-filter"
        onSubmit={handleEvaluate}
      >
        <div className="date-field">
          <label htmlFor="alertStartDate">
            Start date
          </label>

          <input
            id="alertStartDate"
            type="date"
            value={startDate}
            onChange={(event) =>
              setStartDate(event.target.value)
            }
          />
        </div>

        <div className="date-field">
          <label htmlFor="alertEndDate">
            End date
          </label>

          <input
            id="alertEndDate"
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
          disabled={evaluating}
        >
          {evaluating
            ? "Evaluating..."
            : "Evaluate Budgets"}
        </button>
      </form>

      <section className="cards">
        <article className="card">
          <div className="card-heading">
            <span>Open Alerts</span>
            <span className="card-icon">!</span>
          </div>

          <h2>{openAlerts.length}</h2>
          <p>Require attention</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Warnings</span>
            <span className="card-icon">⚠</span>
          </div>

          <h2>{warningCount}</h2>
          <p>Budget thresholds reached</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Critical</span>
            <span className="card-icon">!</span>
          </div>

          <h2>{criticalCount}</h2>
          <p>Budgets exceeded</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Resolved</span>
            <span className="card-icon">✓</span>
          </div>

          <h2>{resolvedCount}</h2>
          <p>Historical alerts</p>
        </article>
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>Alert History</h3>

            <p>
              Budget monitoring events for{" "}
              {selectedAccount?.accountName}.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="table-message">
            Loading alerts...
          </div>
        ) : alerts.length === 0 ? (
          <div className="table-message">
            No alerts found for this account.
          </div>
        ) : (
          <div className="alerts-list">
            {alerts.map((alert) => (
              <article
                className={`alert-item alert-${alert.severity.toLowerCase()}`}
                key={alert.id}
              >
                <div className="alert-indicator">
                  {alert.severity === "CRITICAL"
                    ? "!"
                    : "⚠"}
                </div>

                <div className="alert-content">
                  <div className="alert-heading">
                    <div>
                      <h3>{alert.title}</h3>

                      <p className="alert-budget">
                        {alert.budgetName}
                      </p>
                    </div>

                    <div className="alert-badges">
                      <span
                        className={`severity-badge severity-${alert.severity.toLowerCase()}`}
                      >
                        {alert.severity}
                      </span>

                      <span
                        className={`status-badge status-${alert.status.toLowerCase()}`}
                      >
                        {alert.status}
                      </span>
                    </div>
                  </div>

                  <p className="alert-message">
                    {alert.message}
                  </p>

                  <div className="alert-meta">
                    <span>
                      Type: {alert.alertType}
                    </span>

                    <span>
                      Created:{" "}
                      {formatDate(
                        alert.createdAt
                      )}
                    </span>

                    {alert.resolvedAt && (
                      <span>
                        Resolved:{" "}
                        {formatDate(
                          alert.resolvedAt
                        )}
                      </span>
                    )}
                  </div>
                </div>

                {alert.status === "OPEN" && (
                  <button
                    type="button"
                    className="secondary-button resolve-button"
                    onClick={() =>
                      handleResolve(alert.id)
                    }
                    disabled={
                      resolvingId === alert.id
                    }
                  >
                    {resolvingId === alert.id
                      ? "Resolving..."
                      : "Resolve"}
                  </button>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default AlertsPage;