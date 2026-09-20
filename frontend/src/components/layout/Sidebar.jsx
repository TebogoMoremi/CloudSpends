import { NavLink } from "react-router-dom";
import { useAccount } from "../../context/AccountContext";

function Sidebar() {
  const {
    accounts,
    selectedAccountId,
    setSelectedAccountId,
    accountsLoading,
  } = useAccount();

  const navClass = ({ isActive }) =>
    `nav-item ${isActive ? "active" : ""}`;

  function handleAccountChange(event) {
    setSelectedAccountId(Number(event.target.value));
  }

  return (
    <aside className="sidebar">
      <div>
        <div className="brand">
          <div className="brand-icon">
            C
          </div>

          <span>CloudSpend</span>
        </div>

        <div className="account-selector">
          <label htmlFor="accountSelector">
            CLOUD ACCOUNT
          </label>

          {accountsLoading ? (
            <div className="account-selector-loading">
              Loading accounts...
            </div>
          ) : accounts.length === 0 ? (
            <div className="account-selector-loading">
              No accounts
            </div>
          ) : (
            <select
              id="accountSelector"
              value={selectedAccountId ?? ""}
              onChange={handleAccountChange}
            >
              {accounts.map((account) => (
                <option
                  key={account.id}
                  value={account.id}
                >
                  {account.accountName}
                </option>
              ))}
            </select>
          )}
        </div>

        <nav className="nav">
          <NavLink
            to="/"
            end
            className={navClass}
          >
            Overview
          </NavLink>

          <NavLink
            to="/accounts"
            className={navClass}
          >
            Accounts
          </NavLink>

          <NavLink
            to="/resources"
            className={navClass}
          >
            Resources
          </NavLink>

          <NavLink
            to="/costs"
            className={navClass}
          >
            Costs
          </NavLink>

          <NavLink
            to="/budgets"
            className={navClass}
          >
            Budgets
          </NavLink>

          <NavLink
            to="/alerts"
            className={navClass}
          >
            Alerts
          </NavLink>
        </nav>
      </div>

      <div className="sidebar-footer">
        <span>CloudSpend</span>
        <small>FinOps Platform</small>
      </div>
    </aside>
  );
}

export default Sidebar;