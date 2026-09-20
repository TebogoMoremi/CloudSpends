import { useEffect, useState } from "react";

import {
  getAccountCostBreakdown,
  getAccountCostRecords,
  getAccountCostTotal,
} from "../services/costApi";

import CostBreakdownChart from "../components/CostBreakdownChart";
import { useAccount } from "../context/AccountContext";

function CostsPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [total, setTotal] = useState(null);
  const [breakdown, setBreakdown] = useState([]);
  const [costRecords, setCostRecords] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [startDate, setStartDate] =
    useState("2026-09-01");

  const [endDate, setEndDate] =
    useState("2026-09-30");

  async function loadCosts() {
    if (!selectedAccountId) {
      setTotal(null);
      setBreakdown([]);
      setCostRecords([]);
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const [
        totalData,
        breakdownData,
        recordsData,
      ] = await Promise.all([
        getAccountCostTotal(
          selectedAccountId,
          "USD",
          startDate,
          endDate
        ),
        getAccountCostBreakdown(
          selectedAccountId,
          "USD",
          startDate,
          endDate
        ),
        getAccountCostRecords(
          selectedAccountId,
          "USD",
          startDate,
          endDate
        ),
      ]);

      setTotal(totalData);
      setBreakdown(breakdownData);
      setCostRecords(recordsData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (selectedAccountId) {
      loadCosts();
    } else {
      setTotal(null);
      setBreakdown([]);
      setCostRecords([]);
      setLoading(false);
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

    loadCosts();
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

  const topService =
    breakdown.length > 0
      ? breakdown[0]
      : null;

  const averageCost =
    breakdown.length > 0 && total
      ? Number(total.totalCost) / breakdown.length
      : 0;

  const recordsTotal = costRecords.reduce(
    (sum, record) =>
      sum + Number(record.amount ?? 0),
    0
  );

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
            FinOps
          </p>

          <h1>
            Cloud Costs
          </h1>

          <p className="subtitle">
            {selectedAccount
              ? `${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Analyse your cloud spending."}
          </p>
        </div>

        <form
          className="date-filter"
          onSubmit={handleApplyPeriod}
        >
          <div className="date-field">
            <label htmlFor="costStartDate">
              Start date
            </label>

            <input
              id="costStartDate"
              type="date"
              value={startDate}
              onChange={(event) =>
                setStartDate(event.target.value)
              }
            />
          </div>

          <div className="date-field">
            <label htmlFor="costEndDate">
              End date
            </label>

            <input
              id="costEndDate"
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
            $
            {Number(
              total?.totalCost ?? 0
            ).toFixed(2)}
          </h2>

          <p>
            USD for selected period
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Top Service</span>
            <span className="card-icon">↑</span>
          </div>

          <h2>
            {topService?.resourceType ?? "N/A"}
          </h2>

          <p>
            {topService
              ? `$${Number(
                  topService.cost
                ).toFixed(2)} spend`
              : "No spending data"}
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Services With Spend</span>
            <span className="card-icon">#</span>
          </div>

          <h2>
            {breakdown.length}
          </h2>

          <p>
            Resource types generating cost
          </p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Average Per Service</span>
            <span className="card-icon">$</span>
          </div>

          <h2>
            ${averageCost.toFixed(2)}
          </h2>

          <p>
            Average across resource types
          </p>
        </article>
      </section>

      <section className="dashboard-grid">
        <article className="panel">
          <div className="panel-header">
            <div>
              <h3>
                Cost by Service
              </h3>

              <p>
                {startDate} → {endDate}
              </p>
            </div>

            <span className="currency-badge">
              USD
            </span>
          </div>

          {loading ? (
            <div className="table-message">
              Loading costs...
            </div>
          ) : (
            <CostBreakdownChart
              breakdown={breakdown}
              currency="USD"
            />
          )}
        </article>

        <article className="panel summary-panel">
          <div className="panel-header">
            <div>
              <h3>
                Cost Summary
              </h3>

              <p>
                Selected account spending
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
              <span>Provider</span>

              <strong>
                {selectedAccount?.provider ??
                  "N/A"}
              </strong>
            </div>

            <div>
              <span>Currency</span>

              <strong>
                USD
              </strong>
            </div>

            <div>
              <span>Total cost</span>

              <strong>
                $
                {Number(
                  total?.totalCost ?? 0
                ).toFixed(2)}
              </strong>
            </div>

            <div>
              <span>Top service</span>

              <strong>
                {topService?.resourceType ??
                  "N/A"}
              </strong>
            </div>

            <div>
              <span>Cost records</span>

              <strong>
                {costRecords.length}
              </strong>
            </div>
          </div>
        </article>
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>
              Service Cost Breakdown
            </h3>

            <p>
              Spending grouped by cloud resource type.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="table-message">
            Loading cost breakdown...
          </div>
        ) : breakdown.length === 0 ? (
          <div className="table-message">
            No cost data found for{" "}
            {selectedAccount?.accountName ??
              "this account"}.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="resources-table">
              <thead>
                <tr>
                  <th>Service</th>
                  <th>Cost</th>
                  <th>Share of Spend</th>
                </tr>
              </thead>

              <tbody>
                {breakdown.map((item) => {
                  const totalCost =
                    Number(
                      total?.totalCost ?? 0
                    );

                  const percentage =
                    totalCost > 0
                      ? (Number(item.cost) /
                          totalCost) *
                        100
                      : 0;

                  return (
                    <tr
                      key={item.resourceType}
                    >
                      <td>
                        <span className="type-badge">
                          {item.resourceType}
                        </span>
                      </td>

                      <td>
                        <strong className="resource-cost">
                          $
                          {Number(
                            item.cost
                          ).toFixed(2)}
                        </strong>
                      </td>

                      <td>
                        {percentage.toFixed(1)}%
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>
              Cost Records
            </h3>

            <p>
              Individual billing records for the selected
              cloud account and period.
            </p>
          </div>

          <span className="resource-count">
            {costRecords.length} records
          </span>
        </div>

        {loading ? (
          <div className="table-message">
            Loading cost records...
          </div>
        ) : costRecords.length === 0 ? (
          <div className="table-message">
            No individual cost records found for{" "}
            {selectedAccount?.accountName ??
              "this account"}.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="resources-table">
              <thead>
                <tr>
                  <th>Resource</th>
                  <th>Type</th>
                  <th>Provider Resource ID</th>
                  <th>Period</th>
                  <th>Amount</th>
                </tr>
              </thead>

              <tbody>
                {costRecords.map((record) => (
                  <tr key={record.id}>
                    <td>
                      <div className="resource-name">
                        <div className="resource-icon">
                          {record.resourceType?.charAt(0) ??
                            "R"}
                        </div>

                        <div>
                          <strong>
                            {record.resourceName}
                          </strong>

                          <small>
                            Record #{record.id}
                          </small>
                        </div>
                      </div>
                    </td>

                    <td>
                      <span className="type-badge">
                        {record.resourceType}
                      </span>
                    </td>

                    <td className="resource-id">
                      {record.providerResourceId}
                    </td>

                    <td>
                      <div className="cost-period">
                        <span>
                          {formatDate(
                            record.periodStart
                          )}
                        </span>

                        <span className="period-arrow">
                          →
                        </span>

                        <span>
                          {formatDate(
                            record.periodEnd
                          )}
                        </span>
                      </div>
                    </td>

                    <td>
                      <strong className="resource-cost">
                        $
                        {Number(
                          record.amount
                        ).toFixed(2)}
                      </strong>
                    </td>
                  </tr>
                ))}
              </tbody>

              <tfoot>
                <tr>
                  <td colSpan="4">
                    <strong>
                      Records Total
                    </strong>
                  </td>

                  <td>
                    <strong className="resource-cost">
                      ${recordsTotal.toFixed(2)}
                    </strong>
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>
        )}
      </section>
    </main>
  );
}

export default CostsPage;