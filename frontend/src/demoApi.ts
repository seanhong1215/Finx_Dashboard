import type { AdminSummary, AuthPayload, CardNetwork, CreditCard, Dashboard, Expense, Role, User } from './types';

type StoredUser = User & { password: string };
type StoredCreditCard = CreditCard & { userId: number };
type StoredExpense = Expense & { userId: number };

interface DemoState {
  users: StoredUser[];
  creditCards: StoredCreditCard[];
  expenses: StoredExpense[];
  nextUserId: number;
  nextCardId: number;
  nextExpenseId: number;
}

const stateKey = 'finx:demo-state:v1';
const sessionKey = 'finx:demo-session:v1';
const demoTokenPrefix = 'demo-token-';

export const isDemoMode = import.meta.env.VITE_DEMO_MODE === 'true';

const seedExpenses: Expense[] = [
  { id: 1, category: '餐飲', merchant: '星巴克', note: '工作日咖啡', amount: 195, spentOn: '2026-08-04', creditCardId: 1 },
  { id: 2, category: '交通', merchant: '台灣高鐵', note: '台北到台中', amount: 700, spentOn: '2026-08-06', creditCardId: 1 },
  { id: 3, category: '訂閱', merchant: 'Netflix', note: '月費', amount: 449, spentOn: '2026-08-08', creditCardId: 1 },
  { id: 4, category: '購物', merchant: 'PChome', note: '鍵盤與滑鼠', amount: 2680, spentOn: '2026-08-10', creditCardId: 2 },
  { id: 5, category: '生活', merchant: '全聯福利中心', note: '日用品', amount: 1260, spentOn: '2026-08-12' },
  { id: 6, category: '醫療', merchant: '藥局', note: '感冒藥', amount: 380, spentOn: '2026-08-16', creditCardId: 1 },
  { id: 7, category: '娛樂', merchant: '威秀影城', note: '電影票', amount: 640, spentOn: '2026-08-18', creditCardId: 2 },
  { id: 8, category: '餐飲', merchant: 'Uber Eats', note: '晚餐', amount: 520, spentOn: '2026-08-20', creditCardId: 1 },
  { id: 9, category: '教育', merchant: '線上課程', note: 'React 課程', amount: 1800, spentOn: '2026-08-22' },
  { id: 10, category: '交通', merchant: 'Uber', note: '市區移動', amount: 310, spentOn: '2026-08-25', creditCardId: 1 },
  { id: 11, category: '購物', merchant: 'momo 購物', note: '居家用品', amount: 1590, spentOn: '2026-07-03', creditCardId: 2 },
  { id: 12, category: '生活', merchant: '家樂福', note: '生活採買', amount: 2380, spentOn: '2026-07-08' },
  { id: 13, category: '餐飲', merchant: '鼎泰豐', note: '聚餐', amount: 1850, spentOn: '2026-07-12', creditCardId: 1 },
  { id: 14, category: '訂閱', merchant: 'Spotify', note: '音樂訂閱', amount: 149, spentOn: '2026-07-15', creditCardId: 1 },
  { id: 15, category: '娛樂', merchant: '威秀影城', note: '電影票', amount: 760, spentOn: '2026-07-21', creditCardId: 2 },
  { id: 16, category: '交通', merchant: '台灣高鐵', note: '出差交通', amount: 1490, spentOn: '2026-06-05', creditCardId: 1 },
  { id: 17, category: '醫療', merchant: '診所', note: '門診掛號', amount: 850, spentOn: '2026-06-09' },
  { id: 18, category: '購物', merchant: '蝦皮購物', note: '辦公用品', amount: 980, spentOn: '2026-06-14', creditCardId: 2 },
  { id: 19, category: '餐飲', merchant: '路易莎咖啡', note: '日常咖啡', amount: 180, spentOn: '2026-06-18', creditCardId: 1 },
  { id: 20, category: '教育', merchant: 'Udemy', note: '線上課程', amount: 620, spentOn: '2026-06-24' },
  { id: 21, category: '生活', merchant: '全聯福利中心', note: '日用品', amount: 1760, spentOn: '2026-05-04', creditCardId: 2 },
  { id: 22, category: '交通', merchant: '加油站', note: '汽車加油', amount: 1200, spentOn: '2026-05-11', creditCardId: 1 },
  { id: 23, category: '娛樂', merchant: 'KKBOX', note: '影音訂閱', amount: 149, spentOn: '2026-05-16', creditCardId: 2 },
  { id: 24, category: '餐飲', merchant: '燒肉店', note: '朋友聚餐', amount: 2240, spentOn: '2026-05-23' },
  { id: 25, category: '購物', merchant: '無印良品', note: '居家用品', amount: 1290, spentOn: '2026-04-07', creditCardId: 1 },
  { id: 26, category: '生活', merchant: '家樂福', note: '生活採買', amount: 1980, spentOn: '2026-04-13', creditCardId: 2 },
  { id: 27, category: '醫療', merchant: '藥局', note: '日常備藥', amount: 460, spentOn: '2026-04-19' },
  { id: 28, category: '餐飲', merchant: '外送平台', note: '週末晚餐', amount: 680, spentOn: '2026-04-25', creditCardId: 1 },
  { id: 29, category: '交通', merchant: '台灣高鐵', note: '返鄉交通', amount: 1320, spentOn: '2026-03-06', creditCardId: 2 },
  { id: 30, category: '訂閱', merchant: 'Netflix', note: '影音訂閱', amount: 390, spentOn: '2026-03-11', creditCardId: 1 },
  { id: 31, category: '購物', merchant: 'PChome', note: '3C 配件', amount: 2150, spentOn: '2026-03-17' },
  { id: 32, category: '餐飲', merchant: '日式料理', note: '晚餐', amount: 1280, spentOn: '2026-03-22', creditCardId: 2 }
];

function initialState(): DemoState {
  const jamesCards: StoredCreditCard[] = [
    {
      id: 1,
      userId: 2,
      bankName: '台新銀行',
      cardName: '玫瑰Giving卡',
      network: 'VISA',
      lastFourDigits: '4521',
      creditLimit: 180000,
      statementDay: 12,
      paymentDueDay: 27,
      currentCyclePaid: false,
      currentMonthExpense: 0
    },
    {
      id: 2,
      userId: 2,
      bankName: '國泰世華',
      cardName: 'CUBE卡',
      network: 'MASTERCARD',
      lastFourDigits: '8832',
      creditLimit: 120000,
      statementDay: 18,
      paymentDueDay: 3,
      currentCyclePaid: true,
      currentMonthExpense: 0
    }
  ];
  const adminCards = jamesCards.map((card) => ({
    ...card,
    id: card.id + 100,
    userId: 1,
    bankName: card.id === 1 ? '玉山銀行' : '中國信託',
    cardName: card.id === 1 ? 'Pi 拍錢包卡' : 'LINE Pay卡',
    lastFourDigits: card.id === 1 ? '7610' : '2199'
  }));
  const adminExpenses = seedExpenses.slice(0, 12).map((expense) => ({
    ...expense,
    id: expense.id + 100,
    creditCardId: expense.creditCardId ? expense.creditCardId + 100 : undefined
  }));

  return {
    users: [
      {
        id: 1,
        username: 'admin',
        email: 'admin@finx.local',
        fullName: 'Finx Administrator',
        role: 'ADMIN',
        active: true,
        mustChangePassword: false,
        password: 'password123'
      },
      {
        id: 2,
        username: 'james',
        email: 'james.wilson@example.com',
        fullName: 'James Wilson',
        role: 'USER',
        active: true,
        mustChangePassword: false,
        password: 'password123'
      }
    ],
    creditCards: [
      ...adminCards,
      ...jamesCards.map((card) => ({ ...card }))
    ],
    expenses: [
      ...adminExpenses.map((expense) => ({ ...expense, userId: 1 })),
      ...seedExpenses.map((expense) => ({ ...expense, userId: 2 }))
    ],
    nextUserId: 3,
    nextCardId: 103,
    nextExpenseId: 133
  };
}

function readState(): DemoState {
  const raw = localStorage.getItem(stateKey);
  if (!raw) {
    const seeded = initialState();
    writeState(seeded);
    return seeded;
  }
  return JSON.parse(raw) as DemoState;
}

function writeState(state: DemoState) {
  localStorage.setItem(stateKey, JSON.stringify(state));
}

function publicUser(user: StoredUser): User {
  const { password: _password, ...rest } = user;
  return rest;
}

function currentSessionUser(state = readState()): StoredUser {
  const raw = localStorage.getItem(sessionKey);
  if (!raw) throw new Error('尚未登入');
  const userId = Number(raw);
  const user = state.users.find((item) => item.id === userId && item.active);
  if (!user) throw new Error('登入狀態已失效');
  return user;
}

function authPayload(user: StoredUser): AuthPayload {
  return {
    accessToken: `${demoTokenPrefix}${user.id}`,
    tokenType: 'Bearer',
    user: publicUser(user)
  };
}

function activeCardsFor(state: DemoState, userId: number) {
  return state.creditCards
    .filter((card) => card.userId === userId)
    .sort((a, b) => b.id - a.id);
}

function userExpenses(state: DemoState, userId: number) {
  return state.expenses.filter((expense) => expense.userId === userId);
}

function cardLabel(state: DemoState, cardId?: number) {
  if (!cardId) return undefined;
  const card = state.creditCards.find((item) => item.id === cardId);
  return card ? `${card.bankName} ${card.cardName}` : undefined;
}

function withCardLabel(state: DemoState, expense: Expense): Expense {
  return { ...expense, creditCardLabel: cardLabel(state, expense.creditCardId) };
}

function monthBounds(month: string) {
  const [year, monthValue] = month.split('-').map(Number);
  const start = `${year}-${String(monthValue).padStart(2, '0')}-01`;
  const endDate = new Date(year, monthValue, 0).getDate();
  const end = `${year}-${String(monthValue).padStart(2, '0')}-${String(endDate).padStart(2, '0')}`;
  return { start, end };
}

function currentMonth() {
  return new Date().toISOString().slice(0, 7);
}

function previousMonth(month: string) {
  const [year, monthValue] = month.split('-').map(Number);
  const date = new Date(year, monthValue - 2, 1);
  return date.toISOString().slice(0, 7);
}

function monthsUpTo(month: string, count: number) {
  const [year, monthValue] = month.split('-').map(Number);
  return Array.from({ length: count }, (_, index) => {
    const date = new Date(year, monthValue - count + index, 1);
    return date.toISOString().slice(0, 7);
  });
}

function inRange(date: string, from: string, to: string) {
  return date >= from && date <= to;
}

function sum(expenses: Expense[]) {
  return expenses.reduce((total, expense) => total + expense.amount, 0);
}

function cardMonthExpense(state: DemoState, cardId: number, month = currentMonth()) {
  const { start, end } = monthBounds(month);
  return sum(state.expenses.filter((expense) => expense.creditCardId === cardId && inRange(expense.spentOn, start, end)));
}

function visibleCard(state: DemoState, card: StoredCreditCard): CreditCard {
  const { userId: _userId, ...visible } = card;
  return {
    ...visible,
    currentMonthExpense: cardMonthExpense(state, card.id)
  };
}

function requireOwnedCard(state: DemoState, userId: number, cardId: number) {
  const card = state.creditCards.find((item) => item.id === cardId && item.userId === userId);
  if (!card) throw new Error('找不到信用卡');
  return card;
}

export const demoApi = {
  async login(username: string, password: string) {
    const state = readState();
    const user = state.users.find((item) => item.username === username && item.password === password && item.active);
    if (!user) throw new Error('帳號或密碼錯誤');
    localStorage.setItem(sessionKey, String(user.id));
    return authPayload(user);
  },

  async refresh() {
    const state = readState();
    return authPayload(currentSessionUser(state));
  },

  async logout() {
    localStorage.removeItem(sessionKey);
  },

  async completeFirstLogin(payload: { currentPassword: string; newPassword: string; fullName: string; email: string }) {
    const state = readState();
    const user = currentSessionUser(state);
    if (payload.currentPassword !== user.password) throw new Error('目前密碼不正確');
    user.password = payload.newPassword;
    user.fullName = payload.fullName;
    user.email = payload.email;
    user.mustChangePassword = false;
    writeState(state);
    return publicUser(user);
  },

  async dashboard(month?: string) {
    const state = readState();
    const user = currentSessionUser(state);
    const selectedMonth = month ?? currentMonth();
    const { start, end } = monthBounds(selectedMonth);
    const expenses = userExpenses(state, user.id);
    const monthExpenses = expenses.filter((expense) => inRange(expense.spentOn, start, end));
    const categoryTotals = monthExpenses.reduce<Record<string, number>>((totals, expense) => {
      totals[expense.category] = (totals[expense.category] ?? 0) + expense.amount;
      return totals;
    }, {});
    const monthlyTotals = monthsUpTo(selectedMonth, 6).reduce<Record<string, number>>((totals, item) => {
      const bounds = monthBounds(item);
      totals[item] = sum(expenses.filter((expense) => inRange(expense.spentOn, bounds.start, bounds.end)));
      return totals;
    }, {});
    const cardTotals = monthExpenses.reduce<Record<string, number>>((totals, expense) => {
      const label = cardLabel(state, expense.creditCardId);
      if (!label) return totals;
      totals[label] = (totals[label] ?? 0) + expense.amount;
      return totals;
    }, {});
    const unpaidCreditCardAmount = activeCardsFor(state, user.id)
      .filter((card) => !card.currentCyclePaid)
      .reduce((total, card) => total + cardMonthExpense(state, card.id, selectedMonth), 0);
    const previous = previousMonth(selectedMonth);
    const previousBounds = monthBounds(previous);
    const previousMonthExpense = sum(expenses.filter((expense) => inRange(expense.spentOn, previousBounds.start, previousBounds.end)));

    return {
      monthExpense: sum(monthExpenses),
      unpaidCreditCardAmount,
      expenseCount: expenses.length,
      creditCardCount: activeCardsFor(state, user.id).length,
      categoryTotals,
      recentExpenses: expenses
        .slice()
        .sort((a, b) => b.spentOn.localeCompare(a.spentOn) || b.id - a.id)
        .slice(0, 8)
        .map((expense) => withCardLabel(state, expense)),
      monthlyTotals,
      cardTotals,
      selectedMonth,
      previousMonthExpense
    } satisfies Dashboard;
  },

  async expenses(params?: { from?: string; to?: string; category?: string; creditCardId?: number | '' }) {
    const state = readState();
    const user = currentSessionUser(state);
    const defaultBounds = monthBounds(currentMonth());
    const from = params?.from || defaultBounds.start;
    const to = params?.to || defaultBounds.end;
    return userExpenses(state, user.id)
      .filter((expense) => inRange(expense.spentOn, from, to))
      .filter((expense) => !params?.category || expense.category === params.category)
      .filter((expense) => !params?.creditCardId || expense.creditCardId === params.creditCardId)
      .sort((a, b) => b.spentOn.localeCompare(a.spentOn) || b.id - a.id)
      .map((expense) => withCardLabel(state, expense));
  },

  async createExpense(payload: Omit<Expense, 'id' | 'creditCardLabel'>) {
    const state = readState();
    const user = currentSessionUser(state);
    if (payload.creditCardId) requireOwnedCard(state, user.id, payload.creditCardId);
    const expense = { ...payload, id: state.nextExpenseId++, userId: user.id };
    state.expenses.push(expense);
    writeState(state);
    return withCardLabel(state, expense);
  },

  async updateExpense(id: number, payload: Omit<Expense, 'id' | 'creditCardLabel'>) {
    const state = readState();
    const user = currentSessionUser(state);
    if (payload.creditCardId) requireOwnedCard(state, user.id, payload.creditCardId);
    const expense = state.expenses.find((item) => item.id === id && item.userId === user.id);
    if (!expense) throw new Error('找不到支出');
    Object.assign(expense, payload);
    writeState(state);
    return withCardLabel(state, expense);
  },

  async deleteExpense(id: number) {
    const state = readState();
    const user = currentSessionUser(state);
    state.expenses = state.expenses.filter((expense) => !(expense.id === id && expense.userId === user.id));
    writeState(state);
  },

  async creditCards() {
    const state = readState();
    const user = currentSessionUser(state);
    return activeCardsFor(state, user.id).map((card) => visibleCard(state, card));
  },

  async createCreditCard(payload: Omit<CreditCard, 'id' | 'currentMonthExpense' | 'currentCyclePaid'>) {
    const state = readState();
    const user = currentSessionUser(state);
    const card: StoredCreditCard = {
      ...payload,
      id: state.nextCardId++,
      userId: user.id,
      currentCyclePaid: false,
      currentMonthExpense: 0
    };
    state.creditCards.push(card);
    writeState(state);
    return visibleCard(state, card);
  },

  async updateCreditCard(id: number, payload: Omit<CreditCard, 'id' | 'currentMonthExpense' | 'currentCyclePaid'>) {
    const state = readState();
    const user = currentSessionUser(state);
    const card = requireOwnedCard(state, user.id, id);
    Object.assign(card, payload);
    writeState(state);
    return visibleCard(state, card);
  },

  async deleteCreditCard(id: number) {
    const state = readState();
    const user = currentSessionUser(state);
    requireOwnedCard(state, user.id, id);
    state.creditCards = state.creditCards.filter((card) => card.id !== id);
    state.expenses = state.expenses.map((expense) => expense.creditCardId === id ? { ...expense, creditCardId: undefined } : expense);
    writeState(state);
  },

  async setCardPaid(id: number, paid: boolean) {
    const state = readState();
    const user = currentSessionUser(state);
    const card = requireOwnedCard(state, user.id, id);
    card.currentCyclePaid = paid;
    writeState(state);
    return visibleCard(state, card);
  },

  async updateProfile(payload: { fullName: string; email: string }) {
    const state = readState();
    const user = currentSessionUser(state);
    user.fullName = payload.fullName;
    user.email = payload.email;
    writeState(state);
    return publicUser(user);
  },

  async changePassword(payload: { currentPassword: string; newPassword: string }) {
    const state = readState();
    const user = currentSessionUser(state);
    if (payload.currentPassword !== user.password) throw new Error('目前密碼不正確');
    user.password = payload.newPassword;
    writeState(state);
  },

  async adminSummary() {
    const state = readState();
    const user = currentSessionUser(state);
    if (user.role !== 'ADMIN') throw new Error('權限不足');
    const bounds = monthBounds(currentMonth());
    return {
      users: state.users.length,
      activeUsers: state.users.filter((item) => item.active).length,
      expenses: state.expenses.length,
      creditCards: state.creditCards.length,
      currentMonthExpense: sum(state.expenses.filter((expense) => inRange(expense.spentOn, bounds.start, bounds.end)))
    } satisfies AdminSummary;
  },

  async adminUsers() {
    const state = readState();
    const user = currentSessionUser(state);
    if (user.role !== 'ADMIN') throw new Error('權限不足');
    return state.users.map(publicUser);
  },

  async createUser(payload: { username: string; email: string; fullName: string; temporaryPassword: string; role: Role }) {
    const state = readState();
    const user = currentSessionUser(state);
    if (user.role !== 'ADMIN') throw new Error('權限不足');
    if (state.users.some((item) => item.username === payload.username)) throw new Error('Username already exists');
    if (state.users.some((item) => item.email === payload.email)) throw new Error('Email already exists');
    const created: StoredUser = {
      id: state.nextUserId++,
      username: payload.username,
      email: payload.email,
      fullName: payload.fullName,
      role: payload.role,
      active: true,
      mustChangePassword: true,
      password: payload.temporaryPassword
    };
    state.users.push(created);
    writeState(state);
    return publicUser(created);
  },

  async updateUserRole(id: number, role: Role) {
    const state = readState();
    const user = currentSessionUser(state);
    if (user.role !== 'ADMIN') throw new Error('權限不足');
    const target = state.users.find((item) => item.id === id);
    if (!target) throw new Error('找不到使用者');
    target.role = role;
    writeState(state);
    return publicUser(target);
  },

  async updateUserStatus(id: number, active: boolean) {
    const state = readState();
    const user = currentSessionUser(state);
    if (user.role !== 'ADMIN') throw new Error('權限不足');
    const target = state.users.find((item) => item.id === id);
    if (!target) throw new Error('找不到使用者');
    target.active = active;
    writeState(state);
    return publicUser(target);
  }
};

export type DemoApi = typeof demoApi;
