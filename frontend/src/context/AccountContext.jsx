import {
  createContext,
  useContext,
  useEffect,
  useState,
} from "react";

import { getCloudAccounts } from "../services/accountApi";

const AccountContext = createContext(null);

export function AccountProvider({ children }) {
  const [accounts, setAccounts] = useState([]);
  const [selectedAccountId, setSelectedAccountId] =
    useState(null);

  const [accountsLoading, setAccountsLoading] =
    useState(true);

  const [accountsError, setAccountsError] =
    useState("");

  async function loadAccounts() {
    try {
      setAccountsLoading(true);
      setAccountsError("");

      const data = await getCloudAccounts();

      setAccounts(data);

      setSelectedAccountId((currentId) => {
        const stillExists = data.some(
          (account) => account.id === currentId
        );

        if (stillExists) {
          return currentId;
        }

        return data.length > 0 ? data[0].id : null;
      });
    } catch (err) {
      setAccountsError(err.message);
    } finally {
      setAccountsLoading(false);
    }
  }

  useEffect(() => {
    loadAccounts();
  }, []);

  const selectedAccount =
    accounts.find(
      (account) => account.id === selectedAccountId
    ) ?? null;

  return (
    <AccountContext.Provider
      value={{
        accounts,
        selectedAccount,
        selectedAccountId,
        setSelectedAccountId,
        accountsLoading,
        accountsError,
        loadAccounts,
      }}
    >
      {children}
    </AccountContext.Provider>
  );
}

export function useAccount() {
  const context = useContext(AccountContext);

  if (!context) {
    throw new Error(
      "useAccount must be used inside AccountProvider"
    );
  }

  return context;
}