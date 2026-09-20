import { useEffect, useState } from "react";

import { useAccount } from "../context/AccountContext";

import {
  createBudget,
  getBudgets,
  getBudgetUtilization,
} from "../services/budgetApi";

function BudgetsPage() {
  const {
    selectedAccount,
    selectedAccountId,
    accountsLoading,
  } = useAccount();

  const [budgets, setBudgets] = useState([]);
  const [utilizations, setUtilizations] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [startDate, setStartDate] =
    useState("2026-09-01");

  const [endDate, setEndDate] =
    useState("2026-09-30");

  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);

  const [form, setForm] = useState({
    budgetName: "",
    amount: "",
    currency: "USD",
    alertThreshold: 80,
    status: "ACTIVE",
  });

  async function loadBudgets() {
    if (!selectedAccountId) {
      setBudgets([]);
      setUtilizations([]);
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const budgetData =
        await getBudgets(selectedAccountId);

      setBudgets(budgetData);

      const utilizationData =
        await Promise.all(
          budgetData.map((budget) =>
            getBudgetUtilization(
              selectedAccountId,
              budget.id,
              startDate,
              endDate
            )
          )
        );

      setUtilizations(utilizationData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadBudgets();

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

    loadBudgets();
  }

  function handleInputChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleCreateBudget(event) {
    event.preventDefault();

    if (!selectedAccountId) {
      return;
    }

    try {
      setSaving(true);
      setError("");

      await createBudget(
        selectedAccountId,
        {
          budgetName: form.budgetName,
          amount: Number(form.amount),
          currency: form.currency,
          alertThreshold:
            Number(form.alertThreshold),
          status: form.status,
        }
      );

      setShowModal(false);

      setForm({
        budgetName: "",
        amount: "",
        currency: "USD",
        alertThreshold: 80,
        status: "ACTIVE",
      });

      await loadBudgets();
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  const activeBudgets =
    utilizations.filter(
      (budget) => budget.status === "ACTIVE"
    ).length;

  const thresholdWarnings =
    utilizations.filter(
      (budget) =>
        budget.thresholdReached &&
        !budget.overBudget
    ).length;

  const overBudgetCount =
    utilizations.filter(
      (budget) => budget.overBudget
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
            FinOps
          </p>

          <h1>Budgets</h1>

          <p className="subtitle">
            {selectedAccount
              ? `${selectedAccount.accountName} · ${selectedAccount.provider} · ${selectedAccount.region}`
              : "Monitor cloud spending budgets."}
          </p>
        </div>

        <button
          className="apply-button"
          type="button"
          onClick={() => setShowModal(true)}
        >
          + Add Budget
        </button>
      </header>

      {error && (
        <div className="error-banner">
          {error}
        </div>
      )}

      <form
        className="date-filter budget-date-filter"
        onSubmit={handleApplyPeriod}
      >
        <div className="date-field">
          <label htmlFor="budgetStartDate">
            Start date
          </label>

          <input
            id="budgetStartDate"
            type="date"
            value={startDate}
            onChange={(event) =>
              setStartDate(event.target.value)
            }
          />
        </div>

        <div className="date-field">
          <label htmlFor="budgetEndDate">
            End date
          </label>

          <input
            id="budgetEndDate"
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

      <section className="cards">
        <article className="card">
          <div className="card-heading">
            <span>Total Budgets</span>
            <span className="card-icon">#</span>
          </div>

          <h2>{budgets.length}</h2>

          <p>Configured budgets</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Active Budgets</span>
            <span className="card-icon">✓</span>
          </div>

          <h2>{activeBudgets}</h2>

          <p>Currently monitored</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Threshold Alerts</span>
            <span className="card-icon">!</span>
          </div>

          <h2>{thresholdWarnings}</h2>

          <p>Budgets above alert threshold</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Over Budget</span>
            <span className="card-icon">↑</span>
          </div>

          <h2>{overBudgetCount}</h2>

          <p>Budgets exceeding limit</p>
        </article>
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>Budget Utilization</h3>

            <p>
              Spending from {startDate} to {endDate}.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="table-message">
            Loading budgets...
          </div>
        ) : utilizations.length === 0 ? (
          <div className="table-message">
            No budgets configured for{" "}
            {selectedAccount?.accountName ??
              "this account"}.
          </div>
        ) : (
          <div className="budget-grid">
            {utilizations.map((budget) => {
              const percentage =
                Number(
                  budget.utilizationPercentage
                );

              const progressWidth =
                Math.min(percentage, 100);

              let budgetState = "Within Budget";

              if (budget.overBudget) {
                budgetState = "Over Budget";
              } else if (
                budget.thresholdReached
              ) {
                budgetState =
                  "Threshold Reached";
              }

              return (
                <article
                  className="budget-card"
                  key={budget.budgetId}
                >
                  <div className="budget-card-header">
                    <div>
                      <h3>
                        {budget.budgetName}
                      </h3>

                      <span
                        className={`status-badge status-${budget.status.toLowerCase()}`}
                      >
                        {budget.status}
                      </span>
                    </div>

                    <strong>
                      {budget.currency}{" "}
                      {Number(
                        budget.budgetAmount
                      ).toFixed(2)}
                    </strong>
                  </div>

                  <div className="budget-progress-info">
                    <span>
                      {percentage.toFixed(2)}%
                      used
                    </span>

                    <span>
                      Alert at{" "}
                      {budget.alertThreshold}%
                    </span>
                  </div>

                  <div className="budget-progress">
                    <div
                      className={`budget-progress-bar ${
                        budget.overBudget
                          ? "over"
                          : budget.thresholdReached
                            ? "warning"
                            : ""
                      }`}
                      style={{
                        width: `${progressWidth}%`,
                      }}
                    />
                  </div>

                  <div className="budget-stats">
                    <div>
                      <span>Spent</span>

                      <strong>
                        $
                        {Number(
                          budget.spentAmount
                        ).toFixed(2)}
                      </strong>
                    </div>

                    <div>
                      <span>Remaining</span>

                      <strong>
                        $
                        {Number(
                          budget.remainingAmount
                        ).toFixed(2)}
                      </strong>
                    </div>

                    <div>
                      <span>Status</span>

                      <strong>
                        {budgetState}
                      </strong>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </section>

      {showModal && (
        <div className="modal-backdrop">
          <div className="modal">
            <div className="modal-header">
              <div>
                <h2>Create Budget</h2>

                <p>
                  Create a spending budget for{" "}
                  {selectedAccount?.accountName}.
                </p>
              </div>

              <button
                className="modal-close"
                type="button"
                onClick={() =>
                  setShowModal(false)
                }
              >
                ×
              </button>
            </div>

            <form
              className="account-form"
              onSubmit={handleCreateBudget}
            >
              <div className="form-field">
                <label htmlFor="budgetName">
                  Budget name
                </label>

                <input
                  id="budgetName"
                  name="budgetName"
                  value={form.budgetName}
                  onChange={handleInputChange}
                  placeholder="Production Monthly Budget"
                  required
                />
              </div>

              <div className="form-field">
                <label htmlFor="amount">
                  Budget amount
                </label>

                <input
                  id="amount"
                  name="amount"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={form.amount}
                  onChange={handleInputChange}
                  placeholder="150.00"
                  required
                />
              </div>

              <div className="form-grid">
                <div className="form-field">
                  <label htmlFor="currency">
                    Currency
                  </label>

                  <select
                    id="currency"
                    name="currency"
                    value={form.currency}
                    onChange={handleInputChange}
                  >
                    <option value="USD">USD</option>
                    <option value="ZAR">ZAR</option>
                    <option value="EUR">EUR</option>
                    <option value="GBP">GBP</option>
                  </select>
                </div>

                <div className="form-field">
                  <label htmlFor="alertThreshold">
                    Alert threshold (%)
                  </label>

                  <input
                    id="alertThreshold"
                    name="alertThreshold"
                    type="number"
                    min="1"
                    max="100"
                    value={form.alertThreshold}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="budgetStatus">
                  Status
                </label>

                <select
                  id="budgetStatus"
                  name="status"
                  value={form.status}
                  onChange={handleInputChange}
                >
                  <option value="ACTIVE">
                    ACTIVE
                  </option>

                  <option value="INACTIVE">
                    INACTIVE
                  </option>
                </select>
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() =>
                    setShowModal(false)
                  }
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  className="apply-button"
                  disabled={saving}
                >
                  {saving
                    ? "Creating..."
                    : "Create Budget"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}

export default BudgetsPage;