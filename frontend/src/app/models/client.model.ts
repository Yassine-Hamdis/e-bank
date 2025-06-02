// Client Profile Models
export interface ClientProfile {
  clientId: string;
  address: string;
  identificationNumber: string;
  enrollmentDate: string;
  username: string;
  email: string;
  phoneNumber: string | null;
  status: 'ACTIVE' | 'INACTIVE';
  lastLogin: string | null;
  nationalId: string | null;
  dateOfBirth: string | null;
}

// Account Models
export interface AccountDetails {
  id: number;
  accountId: string;
  clientId: string;
  balance: number;
  accountType: 'CHECKING' | 'SAVINGS';
  status: 'ACTIVE' | 'INACTIVE';
  currency: string;
  createdDate: string;
  lastTransactionDate: string | null;
}

export interface AccountBalance {
  accountId: string;
  lastUpdated: string;
  balance: number;
  currency: string;
}

// Crypto Wallet Models
export interface CryptoWallet {
  totalValue: { [key: string]: number };
  clientId: string;
  createdDate: string;
  lastAccessDate: string;
  walletAddress: string;
  status: 'ACTIVE' | 'INACTIVE';
  supportedCryptos: string[];
}

// Dashboard Summary Models
export interface ClientDashboardData {
  profile: ClientProfile;
  account: AccountDetails;
  balance: AccountBalance;
  cryptoWallet: CryptoWallet;
}

// Transaction Models
export interface TransferRequest {
  sourceAccountId: string;
  destinationAccountId: string;
  transferFee: number;
  amount: number;
  description: string;
}

export interface MobileRechargeRequest {
  phoneNumber: string;
  amount: number;
  description: string;
  operator: string;
  rechargeType: 'PREPAID' | 'POSTPAID';
}

export interface TransactionResponse {
  id: number;
  transactionId: string;
  amount: number;
  date: string;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  description: string;
  type: 'TRANSFER' | 'MOBILE_RECHARGE' | 'DEPOSIT' | 'WITHDRAWAL';
  fromAccountId: string | null;
  toAccountId: string | null;
  verifiedByAgentId: string | null;
  verificationDate: string | null;
  verificationNotes: string | null;
  message?: string;
}

export interface MobileOperator {
  name: string;
  code: string;
  logo?: string;
  supportedTypes: ('PREPAID' | 'POSTPAID')[];
}

// Notification Models
export interface ClientNotification {
  id: number;
  notificationId: string;
  title: string;
  message: string;
  type: 'TRANSACTION' | 'ACCOUNT' | 'SECURITY' | 'SYSTEM' | 'PROMOTION';
  priority: 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
  isRead: boolean;
  readDate: string | null;
  createdDate: string;
  expiresDate: string | null;
  actionUrl: string | null;
  actionLabel: string | null;
}

export interface NotificationDeleteResponse {
  notificationId: string;
  message: string;
  timestamp: number;
}

// Crypto Trading Models
export interface CryptoBalanceDetail {
  id: number;
  cryptoType: string;
  balance: number;
  valueInMAD: number;
  createdDate: string;
  updatedDate: string | null;
}

export interface CryptoWalletDetails {
  id: number;
  walletAddress: string;
  clientId: string;
  createdDate: string;
  updatedDate: string;
  status: 'ACTIVE' | 'INACTIVE';
  supportedCryptos: string[];
  lastAccessDate: string;
  cryptoBalances: { [key: string]: number };
  balanceDetails: CryptoBalanceDetail[];
  totalValueMAD: number;
  totalBalances: number;
}

export interface CryptoRates {
  BTC: number;
  ETH: number;
  USDT: number;
  timestamp: number;
}

export interface CryptoBuyRequest {
  cryptoType: string;
  amount: number;
  exchangeRate: number;
  walletAddress: string;
  description: string;
}

export interface CryptoBuyFromMainRequest {
  cryptoType: string;
  amount: number;
  description: string;
  platformFee: number;
  useRealTimeRate: boolean;
}

export interface CryptoTransferRequest {
  recipientWalletAddress: string;
  cryptoType: string;
  cryptoAmount: number;
  networkFee: number;
  description: string;
}

export interface CryptoSellRequest {
  cryptoType: string;
  cryptoAmount: number;
  exchangeRate: number;
  walletAddress: string;
  description: string;
}

export interface CryptoTransactionResponse {
  transactionId: string;
  amount: number;
  type: 'CRYPTO_BUY' | 'CRYPTO_SELL';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  message?: string;
}

export interface CryptoTransaction {
  transactionId: string;
  type: 'CRYPTO_BUY' | 'CRYPTO_SELL';
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  date: string;
}

export interface WalletAddressUpdateRequest {
  newWalletAddress: string;
}

export interface WalletAddressUpdateResponse {
  clientId: number;
  btcWalletAddress: string;
  message: string;
}
