export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  email: string;
  roles: string[];
  tokenType: string;
  expiresIn: number;
}

export interface User {
  username: string;
  email: string;
  roles: string[];
}

export interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
}

// Admin Statistics Models
export interface AgentStatistics {
  totalAgents: number;
  activeAgents: number;
  inactiveAgents: number;
  totalManagedClients: number;
}

export interface CurrencyStatistics {
  totalCurrencies: number;
  activeCurrencies: number;
  inactiveCurrencies: number;
  manualCurrencies: number;
  binanceCurrencies: number;
}

// Global Statistics Interface
export interface GlobalStatistics {
  totalUsers: number;
  totalClients: number;
  totalBankAgents: number;
  totalAdministrators: number;
  activeUsers: number;
  inactiveUsers: number;
  totalAccounts: number;
  totalTransactions: number;
  pendingTransactions: number;
  completedTransactions: number;
  failedTransactions: number;
  totalCurrencies: number;
  activeCurrencies: number;
  totalNotifications: number;
  unreadNotifications: number;
  systemStatus: string;
  timestamp: number;
}

export interface GlobalStatisticsResponse {
  statistics: GlobalStatistics;
  status: string;
  message: string;
  timestamp: number;
}

// Agent Management Models
export interface Agent {
  id: number;
  username: string;
  email: string;
  phoneNumber: string;
  employeeId: string;
  branch: string;
  position: string;
  status: 'ACTIVE' | 'INACTIVE';
  role: string;
  createdAt: string;
  updatedAt: string | null;
  managedClientsCount: number;
}

export interface CreateAgentRequest {
  username: string;
  password: string;
  email: string;
  phoneNumber: string;
  employeeId: string;
  branch: string;
  position: string;
}

export interface UpdateAgentStatusRequest {
  status: 'ACTIVE' | 'INACTIVE';
}

// Currency Management Models
export interface Currency {
  id: number;
  symbol: string;
  name: string;
  currentPrice: number;
  priceChange24h: number;
  marketCap: number | null;
  volume24h: number;
  lastUpdated: string;
  isActive: boolean;
  isManual: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CurrenciesResponse {
  total: number;
  currencies: Currency[];
  timestamp: number;
}

export interface RefreshBinanceResponse {
  refreshed: number;
  message: string;
  currencies: Currency[];
  timestamp: number;
}

export interface CreateCurrencyRequest {
  symbol: string;
  name: string;
  currentPrice: number;
  priceChange24h: number;
  marketCap?: number;
  volume24h: number;
  isActive: boolean;
}

export interface CreateCurrencyResponse {
  currency: Currency;
  message: string;
  timestamp: number;
}

// Global Settings Models
export interface GlobalSettings {
  maxClientAccountBalance: number;
  maxDailyNewClients: number;
  feePercentage: number;
}

export interface GlobalSettingsResponse {
  settings: GlobalSettings;
  status: string;
  message: string;
  timestamp: number;
}

export interface UpdateGlobalSettingsRequest {
  maxClientAccountBalance: number;
  maxDailyNewClients: number;
  feePercentage: number;
}

export interface UpdateFeePercentageRequest {
  value: string;
}

// Password Change Models
export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ChangePasswordResponse {
  message: string;
  success: boolean;
  timestamp: number;
}

// Agent Management Models
export interface Client {
  id: number;
  clientId: string;
  address: string;
  identificationNumber: string;
  enrollmentDate: string;
  username: string;
  email: string;
  phoneNumber: string | null;
  nationalId: string | null;
  dateOfBirth: string | null;
  status: 'ACTIVE' | 'INACTIVE';
  viewAccount: string[];
  makeTransfer: boolean;
  viewHistory: string[];
}

export interface CreateClientRequest {
  address: string;
  username: string;
  email: string;
  phoneNumber: string;
  nationalId: string;
}

export interface CreateClientResponse {
  client: Client;
  account: {
    id: number;
    accountId: string;
    clientId: string;
    balance: number;
    accountType: string;
    status: string;
    currency: string;
    createdDate: string;
    lastTransactionDate: string | null;
  };
  cryptoWallet: {
    id: number;
    walletAddress: string;
    clientId: string;
    status: string;
    supportedCryptos: string[];
    createdDate: string;
  };
}

export interface DepositRequest {
  clientId: string;
  accountId: string;
  amount: number;
  description: string;
}

export interface DepositResponse {
  id: number;
  transactionId: string;
  amount: number;
  date: string;
  status: string;
  description: string;
  type: string;
  fromAccountId: string | null;
  toAccountId: string;
  verifiedByAgentId: string;
  verificationDate: string;
  verificationNotes: string;
  message?: string;
}

// Transaction Management Models
export interface Transaction {
  id: number;
  transactionId: string;
  amount: number;
  date: string;
  status: 'Pending' | 'Verified' | 'Rejected' | 'PENDING' | 'VERIFIED' | 'REJECTED';
  description: string;
  type: string;
  fromAccountId: string | null;
  toAccountId: string;
  verifiedByAgentId: string | null;
  verificationDate: string | null;
  verificationNotes: string | null;
  clientId?: number;
  accountId?: number;
}

export interface VerifyTransactionRequest {
  status: 'VERIFIED' | 'REJECTED';
  description: string;
}

export interface VerifyTransactionResponse {
  id: number;
  transactionId: string;
  amount: number;
  date: string;
  status: string;
  description: string;
  type: string;
  fromAccountId: string;
  toAccountId: string;
  verifiedByAgentId: string;
  verificationDate: string;
  verificationNotes: string;
  message?: string;
}

// Deposit Statistics Models
export interface DepositStatistics {
  agentId: string;
  agentEmployeeId: string;
  agentName: string;
  branch: string;
  totalDeposits: number;
  totalDepositAmount: number;
  depositsToday: number;
  depositAmountToday: number;
  depositsThisWeek: number;
  depositAmountThisWeek: number;
  depositsThisMonth: number;
  depositAmountThisMonth: number;
  averageDepositAmount: number;
  largestDeposit: number;
  smallestDeposit: number;
  lastDepositDate: string;
  managedClientsCount: number;
  timestamp: number;
}
