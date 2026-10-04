import { useEffect, useState } from "react";

import { getDashboardSummary } from "../services/dashboardApi";
import { getCostTrend } from "../services/analyticsApi";

import CostBreakdownChart from "../components/CostBreakdownChart";
import CostTrendChart from "../components/CostTrendChart";

import { useAccount } from "../context/AccountContext";

function OverviewPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [dashboard, setDashboard] = useState(null);
  const [costTrend, setCostTrend] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [startDate, setStartDate] =
    useState("2026-09-01");

  const [endDate, setEndDate] =
    useState("2026-09-30");

  async function loadDashboard() {
    if (!selectedAccountId) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const [dashboardData, trendData] =
        await Promise.all([
          getDashboardSummary(
            selectedAccountId,
            "USD",
            startDate,
            endDate
          ),

          getCostTrend(
            selectedAccountId,
            "USD",
            startDate,
            endDate
          ),
        ]);

      setDashboard(dashboardData);
      setCostTrend(trendData);
    } catch (err) {
      console.error(err);

      setError(
        err.message ||
          "Failed to load CloudSpend dashboard."
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (selectedAccountId) {
      loadDashboard();
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedAccountId]);

  function handleApplyPeriod(event) {
    event.preventDefault();

    if (endDate < startDate) {
      setError(
        "End date cannot be before start date."
      );
      return;
    }

    loadDashboard();
  }

  function formatDate(date) {
    if (!date) {
      return "N/A";
    }

    return new Date(
      `${date}T00:00:00`
    ).toLocaleDateString("en-ZA", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  }

  if (
    accountsLoading ||
    (!dashboard && loading)
  ) {
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

  if (!dashboard && error) {
    return (
      <main className="main-content">
        <div className="state-message error">
          {error}
        </div>
      </main>
    );
  }

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <p className="eyebrow">
            Dashboard
          </p>

          <h1>
            Cloud Cost Overview
          </h1>

          <p className="subtitle">
            {selectedAccount
              ? `${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Monitor your cloud infrastructure and spending."}
          </p>
        </div>

        <form
          className="date-filter"
          onSubmit={handleApplyPeriod}
        >
          <div className="date-field">
            <label htmlFor="startDate">
              Start date
            </label>

            <input
              id="startDate"
              type="date"
              value={startDate}
              onChange={(event) =>
                setStartDate(event.target.value)
              }
            />
          </div>

          <div className="date-field">
            <label htmlFor="endDate">
              End date
            </label>

            <input
              id="endDate"
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
            <span>Total Spend</span>
            <span className="card-icon">$</span>
          </div>

          <h2>
            ${Number(
              costTrend?.totalCost ??
                dashboard.totalCost
            ).toFixed(2)}
          </h2>

          <p>
            {dashboard.currency} for selected period
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Average Daily Cost</span>
            <span className="card-icon">~</span>
          </div>

          <h2>
            $
            {Number(
              costTrend?.averageDailyCost ?? 0
            ).toFixed(2)}
          </h2>

          <p>
            Average spend per day
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Highest Cost Day</span>
            <span className="card-icon">↑</span>
          </div>

          <h2>
            $
            {Number(
              costTrend?.highestDailyCost ?? 0
            ).toFixed(2)}
          </h2>

          <p>
            {formatDate(
              costTrend?.highestCostDate
            )}
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Cloud Resources</span>
            <span className="card-icon">#</span>
          </div>

          <h2>
            {dashboard.resourceCount}
          </h2>

          <p>
            Resources being monitored
          </p>
        </article>
      </section>

      <section className="dashboard-grid">
        <article className="panel">
          <div className="panel-header">
            <div>
              <h3>
                Daily Cost Trend
              </h3>

              <p>
                {dashboard.startDate} →{" "}
                {dashboard.endDate}
              </p>
            </div>

            <span className="currency-badge">
              {dashboard.currency}
            </span>
          </div>

          <CostTrendChart
            dailyCosts={
              costTrend?.dailyCosts ?? []
            }
            currency={dashboard.currency}
          />
        </article>

        <article className="panel">
          <div className="panel-header">
            <div>
              <h3>
                Cost Breakdown
              </h3>

              <p>
                Spending by resource type
              </p>
            </div>

            <span className="currency-badge">
              {dashboard.currency}
            </span>
          </div>

          <CostBreakdownChart
            breakdown={dashboard.breakdown}
            currency={dashboard.currency}
          />
        </article>

        <article className="panel summary-panel">
          <div className="panel-header">
            <div>
              <h3>
                Spend Summary
              </h3>

              <p>
                Current monitoring period
              </p>
            </div>
          </div>

          <div className="summary-list">
            <div>
              <span>Account</span>

              <strong>
                {selectedAccount?.accountName ??
                  "N/A"}
              </strong>
            </div>

            <div>
              <span>Currency</span>

              <strong>
                {dashboard.currency}
              </strong>
            </div>

            <div>
              <span>Total cost</span>

              <strong>
                $
                {Number(
                  costTrend?.totalCost ??
                    dashboard.totalCost
                ).toFixed(2)}
              </strong>
            </div>

            <div>
              <span>Average daily cost</span>

              <strong>
                $
                {Number(
                  costTrend?.averageDailyCost ?? 0
                ).toFixed(2)}
              </strong>
            </div>

            <div>
              <span>Highest cost day</span>

              <strong>
                {formatDate(
                  costTrend?.highestCostDate
                )}
              </strong>
            </div>

            <div>
              <span>Resources</span>

              <strong>
                {dashboard.resourceCount}
              </strong>
            </div>

            <div>
              <span>Highest spend service</span>

              <strong>
                {dashboard.topResourceType ??
                  "N/A"}
              </strong>
            </div>
          </div>
        </article>
      </section>
    </main>
  );
}

export default OverviewPage;