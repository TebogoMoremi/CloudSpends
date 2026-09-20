import { useEffect, useState } from "react";
import {
  createCloudAccount,
  getCloudAccounts,
} from "../services/accountApi";

const initialForm = {
  accountName: "",
  provider: "AWS",
  providerAccountId: "",
  region: "af-south-1",
  status: "ACTIVE",
};

function AccountsPage() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState(initialForm);

  async function loadAccounts() {
    try {
      setLoading(true);
      setError("");

      const data = await getCloudAccounts();
      setAccounts(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAccounts();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function closeForm() {
    if (saving) {
      return;
    }

    setShowForm(false);
    setForm(initialForm);
    setError("");
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      const createdAccount = await createCloudAccount(form);

      setAccounts((current) => [
        ...current,
        createdAccount,
      ]);

      setForm(initialForm);
      setShowForm(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  const activeAccounts = accounts.filter(
    (account) => account.status === "ACTIVE"
  ).length;

  const providers = new Set(
    accounts.map((account) => account.provider)
  ).size;

  return (
    <main className="main-content">
      <header className="topbar">
        <div>
          <p className="eyebrow">
            Infrastructure
          </p>

          <h1>Cloud Accounts</h1>

          <p className="subtitle">
            Manage cloud accounts connected to CloudSpend.
          </p>
        </div>

        <button
          className="apply-button"
          type="button"
          onClick={() => setShowForm(true)}
        >
          + Add Account
        </button>
      </header>

      {error && !showForm && (
        <div className="error-banner">
          {error}
        </div>
      )}

      <section className="cards">
        <article className="card">
          <div className="card-heading">
            <span>Connected Accounts</span>
            <span className="card-icon">#</span>
          </div>

          <h2>{accounts.length}</h2>

          <p>Total cloud accounts</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Active Accounts</span>
            <span className="card-icon">↑</span>
          </div>

          <h2>{activeAccounts}</h2>

          <p>Accounts currently active</p>
        </article>

        <article className="card">
          <div className="card-heading">
            <span>Cloud Providers</span>
            <span className="card-icon">☁</span>
          </div>

          <h2>{providers}</h2>

          <p>Providers connected</p>
        </article>
      </section>

      <section className="panel resources-panel">
        <div className="panel-header">
          <div>
            <h3>Connected Accounts</h3>

            <p>
              Cloud environments currently monitored by CloudSpend.
            </p>
          </div>
        </div>

        {loading ? (
          <div className="table-message">
            Loading cloud accounts...
          </div>
        ) : accounts.length === 0 ? (
          <div className="table-message">
            No cloud accounts connected.
          </div>
        ) : (
          <div className="table-wrapper">
            <table className="resources-table">
              <thead>
                <tr>
                  <th>Account</th>
                  <th>Provider</th>
                  <th>Provider Account ID</th>
                  <th>Region</th>
                  <th>Status</th>
                  <th>Created</th>
                </tr>
              </thead>

              <tbody>
                {accounts.map((account) => (
                  <tr key={account.id}>
                    <td>
                      <div className="resource-name">
                        <div className="resource-icon">
                          {account.provider?.[0] ?? "C"}
                        </div>

                        <div>
                          <strong>
                            {account.accountName}
                          </strong>

                          <small>
                            Internal ID: {account.id}
                          </small>
                        </div>
                      </div>
                    </td>

                    <td>
                      <span className="type-badge">
                        {account.provider}
                      </span>
                    </td>

                    <td className="resource-id">
                      {account.providerAccountId}
                    </td>

                    <td>
                      {account.region}
                    </td>

                    <td>
                      <span
                        className={`status-badge status-${account.status.toLowerCase()}`}
                      >
                        {account.status}
                      </span>
                    </td>

                    <td>
                      {new Date(
                        account.createdAt
                      ).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {showForm && (
        <div
          className="modal-backdrop"
          onMouseDown={(event) => {
            if (event.target === event.currentTarget) {
              closeForm();
            }
          }}
        >
          <div className="account-modal">
            <div className="modal-header">
              <div>
                <p className="eyebrow">
                  Cloud Account
                </p>

                <h2>Add Account</h2>

                <p>
                  Connect another cloud environment to CloudSpend.
                </p>
              </div>

              <button
                className="modal-close"
                type="button"
                onClick={closeForm}
                disabled={saving}
              >
                ×
              </button>
            </div>

            {error && (
              <div className="error-banner">
                {error}
              </div>
            )}

            <form
              className="account-form"
              onSubmit={handleSubmit}
            >
              <div className="form-field">
                <label htmlFor="accountName">
                  Account name
                </label>

                <input
                  id="accountName"
                  name="accountName"
                  type="text"
                  value={form.accountName}
                  onChange={handleChange}
                  placeholder="Production AWS"
                  required
                />
              </div>

              <div className="form-grid">
                <div className="form-field">
                  <label htmlFor="provider">
                    Provider
                  </label>

                  <select
                    id="provider"
                    name="provider"
                    value={form.provider}
                    onChange={handleChange}
                  >
                    <option value="AWS">
                      AWS
                    </option>

                    <option value="AZURE">
                      Azure
                    </option>

                    <option value="GCP">
                      Google Cloud
                    </option>
                  </select>
                </div>

                <div className="form-field">
                  <label htmlFor="status">
                    Status
                  </label>

                  <select
                    id="status"
                    name="status"
                    value={form.status}
                    onChange={handleChange}
                  >
                    <option value="ACTIVE">
                      Active
                    </option>

                    <option value="INACTIVE">
                      Inactive
                    </option>
                  </select>
                </div>
              </div>

              <div className="form-field">
                <label htmlFor="providerAccountId">
                  Provider Account ID
                </label>

                <input
                  id="providerAccountId"
                  name="providerAccountId"
                  type="text"
                  value={form.providerAccountId}
                  onChange={handleChange}
                  placeholder="123456789012"
                  required
                />
              </div>

              <div className="form-field">
                <label htmlFor="region">
                  Region
                </label>

                <input
                  id="region"
                  name="region"
                  type="text"
                  value={form.region}
                  onChange={handleChange}
                  placeholder="af-south-1"
                  required
                />
              </div>

              <div className="modal-actions">
                <button
                  className="secondary-button"
                  type="button"
                  onClick={closeForm}
                  disabled={saving}
                >
                  Cancel
                </button>

                <button
                  className="apply-button"
                  type="submit"
                  disabled={saving}
                >
                  {saving
                    ? "Adding..."
                    : "Add Account"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}

export default AccountsPage;