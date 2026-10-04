import { useEffect, useMemo, useState } from "react";

import {
  evaluateAlerts,
  getAlerts,
  resolveAlert,
} from "../services/alertApi";

import { useAccount } from "../context/AccountContext";

function AlertsPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);
  const [resolvingId, setResolvingId] = useState(null);
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

      const data = await getAlerts(
        selectedAccountId
      );

      setAlerts(data);
    } catch (err) {
      console.error(err);

      setError(
        err.message || "Failed to load alerts."
      );
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

    if (endDate < startDate) {
      setError(
        "End date cannot be before start date."
      );
      return;
    }

    try {
      setEvaluating(true);
      setError("");

      await evaluateAlerts(
        selectedAccountId,
        startDate,
        endDate
      );

      await loadAlerts();
    } catch (err) {
      console.error(err);

      setError(
        err.message || "Failed to evaluate alerts."
      );
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
      console.error(err);

      setError(
        err.message || "Failed to resolve alert."
      );
    } finally {
      setResolvingId(null);
    }
  }

  const stats = useMemo(() => {
    return {
      open: alerts.filter(
        (alert) => alert.status === "OPEN"
      ).length,

      warning: alerts.filter(
        (alert) =>
          alert.status === "OPEN" &&
          alert.severity === "WARNING"
      ).length,

      critical: alerts.filter(
        (alert) =>
          alert.status === "OPEN" &&
          alert.severity === "CRITICAL"
      ).length,

      resolved: alerts.filter(
        (alert) => alert.status === "RESOLVED"
      ).length,
    };
  }, [alerts]);

  function formatDateTime(value) {
    if (!value) {
      return "—";
    }

    return new Date(value).toLocaleString(
      "en-ZA",
      {
        dateStyle: "medium",
        timeStyle: "short",
      }
    );
  }

  function formatAlertType(type) {
    if (!type) {
      return "Unknown";
    }

    return type
      .split("_")
      .map(
        (word) =>
          word.charAt(0) +
          word.slice(1).toLowerCase()
      )
      .join(" ");
  }

  if (accountsLoading) {
    return (
      <main className="main-content">
        <div className="state-message">
          Loading CloudSpend...
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

          <h1>Cost Alerts</h1>

          <p className="subtitle">
            {selectedAccount
              ? `${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Monitor budget thresholds and cloud spending."}
          </p>
        </div>

        <form
          className="date-filter"
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
            disabled={evaluating || loading}
          >
            {evaluating
              ? "Evaluating..."
              : "Evaluate Alerts"}
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
            <span>Open Alerts</span>
            <span className="card-icon">!</span>
          </div>

          <h2>{stats.open}</h2>

          <p>Alerts requiring attention</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Warnings</span>
            <span className="card-icon">△</span>
          </div>

          <h2>{stats.warning}</h2>

          <p>Open budget warnings</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Critical</span>
            <span className="card-icon">!</span>
          </div>

          <h2>{stats.critical}</h2>

          <p>Budgets requiring action</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Resolved</span>
            <span className="card-icon">✓</span>
          </div>

          <h2>{stats.resolved}</h2>

          <p>Previously resolved alerts</p>
        </article>
      </section>

      <section className="panel">
        <div className="panel-header">
          <div>
            <h3>Alert History</h3>

            <p>
              Budget threshold and exceeded
              notifications
            </p>
          </div>

          <span className="currency-badge">
            {alerts.length} Alerts
          </span>
        </div>

        {loading ? (
          <div className="state-message">
            Loading alerts...
          </div>
        ) : alerts.length === 0 ? (
          <div className="state-message">
            No alerts found for this account.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Severity</th>
                  <th>Alert</th>
                  <th>Budget</th>
                  <th>Status</th>
                  <th>Created</th>
                  <th>Resolved</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {alerts.map((alert) => (
                  <tr key={alert.id}>
                    <td>
                      <span
                        className={`alert-severity ${alert.severity?.toLowerCase()}`}
                      >
                        {alert.severity}
                      </span>
                    </td>

                    <td>
                      <div className="alert-description">
                        <strong>
                          {alert.title}
                        </strong>

                        <span>
                          {formatAlertType(
                            alert.alertType
                          )}
                        </span>

                        <small>
                          {alert.message}
                        </small>
                      </div>
                    </td>

                    <td>
                      {alert.budgetName}
                    </td>

                    <td>
                      <span
                        className={`alert-status ${alert.status?.toLowerCase()}`}
                      >
                        {alert.status}
                      </span>
                    </td>

                    <td>
                      {formatDateTime(
                        alert.createdAt
                      )}
                    </td>

                    <td>
                      {formatDateTime(
                        alert.resolvedAt
                      )}
                    </td>

                    <td>
                      {alert.status === "OPEN" ? (
                        <button
                          className="resolve-alert-button"
                          type="button"
                          disabled={
                            resolvingId === alert.id
                          }
                          onClick={() =>
                            handleResolve(
                              alert.id
                            )
                          }
                        >
                          {resolvingId === alert.id
                            ? "Resolving..."
                            : "Resolve"}
                        </button>
                      ) : (
                        <span className="resolved-text">
                          Resolved
                        </span>
                      )}
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

export default AlertsPage;