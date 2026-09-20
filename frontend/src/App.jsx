import { BrowserRouter, Route, Routes } from "react-router-dom";

import Sidebar from "./components/layout/Sidebar";
import OverviewPage from "./pages/OverviewPage";
import AccountsPage from "./pages/AccountsPage";
import ResourcesPage from "./pages/ResourcesPage";
import CostsPage from "./pages/CostsPage";
import { AccountProvider } from "./context/AccountContext";
import BudgetsPage from "./pages/BudgetsPage";
import AlertsPage from "./pages/AlertsPage";
import "./App.css";

function PlaceholderPage({ title, description }) {
  return (
    <main className="main-content">
      <p className="eyebrow">CloudSpend</p>

      <h1>{title}</h1>

      <p className="subtitle">{description}</p>

      <section className="panel resources-placeholder">
        <p>This section will be implemented next.</p>
      </section>
    </main>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AccountProvider>
        <div className="app">
          <Sidebar />

          <Routes>
            <Route path="/" element={<OverviewPage />} />

            <Route path="/accounts" element={<AccountsPage />} />

            <Route path="/resources" element={<ResourcesPage />} />

            <Route path="/costs" element={<CostsPage />} />

            <Route path="/budgets" element={<BudgetsPage />} />

            <Route path="/alerts" element={<AlertsPage />} />
          </Routes>
        </div>
      </AccountProvider>
    </BrowserRouter>
  );
}

export default App;
