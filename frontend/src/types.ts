export type Role = 'USER' | 'ADMIN';
export type CardNetwork = 'VISA' | 'MASTERCARD' | 'JCB' | 'AMEX' | 'OTHER';

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  active: boolean;
  mustChangePassword: boolean;
}

export interface AuthPayload {
  accessToken: string;
  tokenType: 'Bearer';
  user: User;
}

export interface Expense {
  id: number;
  category: string;
  merchant: string;
  note?: string;
  amount: number;
  spentOn: string;
  creditCardId?: number;
  creditCardLabel?: string;
}

export interface CreditCard {
  id: number;
  bankName: string;
  cardName: string;
  network: CardNetwork;
  lastFourDigits: string;
  creditLimit: number;
  statementDay: number;
  paymentDueDay: number;
  currentCyclePaid: boolean;
  currentMonthExpense: number;
}

export interface Dashboard {
  monthExpense: number;
  unpaidCreditCardAmount: number;
  expenseCount: number;
  creditCardCount: number;
  categoryTotals: Record<string, number>;
  recentExpenses: Expense[];
  monthlyTotals: Record<string, number>;
  cardTotals: Record<string, number>;
  selectedMonth: string;
  previousMonthExpense: number;
}

export interface AdminSummary {
  users: number;
  activeUsers: number;
  expenses: number;
  creditCards: number;
  currentMonthExpense: number;
}
