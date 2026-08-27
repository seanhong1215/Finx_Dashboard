import {
  BadgeDollarSign,
  CreditCard as CreditCardIcon,
  LayoutDashboard,
  LockKeyhole,
  LogOut,
  Plus,
  Save,
  Settings,
  ShieldCheck,
  Trash2,
  UserRound,
  UserCog
} from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from 'recharts';
import { api, setAccessToken } from './api';
import type { AdminSummary, CardNetwork, CreditCard, Dashboard, Expense, Role, User } from './types';

type View = 'dashboard' | 'expenses' | 'cards' | 'settings' | 'admin';
type ExpenseFilterTarget = { category?: string; creditCardId?: number };

const categories = ['餐飲', '交通', '購物', '生活', '訂閱', '醫療', '娛樂', '教育', '其他'];
const networks: CardNetwork[] = ['VISA', 'MASTERCARD', 'JCB', 'AMEX', 'OTHER'];
const assetPath = (path: string) => `${import.meta.env.BASE_URL}${path.replace(/^\/+/, '')}`;

const emptyExpense = {
  category: '餐飲',
  merchant: '',
  note: '',
  amount: 0,
  spentOn: new Date().toISOString().slice(0, 10),
  creditCardId: undefined as number | undefined
};

const emptyCard = {
  bankName: '',
  cardName: '',
  network: 'VISA' as CardNetwork,
  lastFourDigits: '',
  creditLimit: 0,
  statementDay: 15,
  paymentDueDay: 5
};

function money(value: number | undefined) {
  return new Intl.NumberFormat('zh-TW', {
    style: 'currency',
    currency: 'TWD',
    maximumFractionDigits: 0
  }).format(Number(value ?? 0));
}

export function App() {
  const [user, setUser] = useState<User | null>(null);
  const [booting, setBooting] = useState(true);
  const [access, setAccess] = useState<string | null>(null);
  const [view, setView] = useState<View>('dashboard');
  const [expenseFilter, setExpenseFilter] = useState<ExpenseFilterTarget>({});
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .refresh()
      .then((payload) => {
        setAccessToken(payload.accessToken);
        setAccess(payload.accessToken);
        setUser(payload.user);
        setView('dashboard');
      })
      .catch(() => undefined)
      .finally(() => setBooting(false));
  }, []);

  function applyAuth(token: string, nextUser: User) {
    setAccessToken(token);
    setAccess(token);
    setUser(nextUser);
    setError('');
    setView('dashboard');
  }

  async function logout() {
    await api.logout().catch(() => undefined);
    setAccessToken(null);
    setAccess(null);
    setUser(null);
    setView('dashboard');
  }

  if (booting) {
    return <div className="center-screen">載入中</div>;
  }

  if (!user || !access) {
    return <LoginScreen onLogin={applyAuth} error={error} setError={setError} />;
  }

  if (user.mustChangePassword) {
    return <FirstLoginScreen user={user} onComplete={setUser} onLogout={logout} />;
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">F</span>
          <span>Finx</span>
        </div>
        <button className={view === 'dashboard' ? 'nav active' : 'nav'} onClick={() => setView('dashboard')}>
          <LayoutDashboard size={18} /> 總覽
        </button>
        <button className={view === 'expenses' ? 'nav active' : 'nav'} onClick={() => setView('expenses')}>
          <BadgeDollarSign size={18} /> 支出紀錄
        </button>
        <button className={view === 'cards' ? 'nav active' : 'nav'} onClick={() => setView('cards')}>
          <CreditCardIcon size={18} /> 信用卡
        </button>
        <button className={view === 'settings' ? 'nav active' : 'nav'} onClick={() => setView('settings')}>
          <Settings size={18} /> 設定
        </button>
        {user.role === 'ADMIN' && (
          <button className={view === 'admin' ? 'nav active' : 'nav'} onClick={() => setView('admin')}>
            <UserCog size={18} /> Admin
          </button>
        )}
        <button className="nav logout" onClick={logout}>
          <LogOut size={18} /> 登出
        </button>
      </aside>
      <main className="workspace">
        <header className="topbar">
          <div>
            <span className="eyebrow">{user.role}</span>
            <h1>{titleFor(view)}</h1>
          </div>
          <div className="profile-chip">{user.fullName}</div>
        </header>
        {view === 'dashboard' && <DashboardView onOpenExpenses={(filter) => { setExpenseFilter(filter); setView('expenses'); }} />}
        {view === 'expenses' && <ExpensesView initialFilter={expenseFilter} />}
        {view === 'cards' && <CardsView />}
        {view === 'settings' && <SettingsView user={user} onUserChange={setUser} />}
        {view === 'admin' && user.role === 'ADMIN' && <AdminView />}
      </main>
    </div>
  );
}

function titleFor(view: View) {
  const map: Record<View, string> = {
    dashboard: '本月支出總覽',
    expenses: '支出紀錄',
    cards: '信用卡管理',
    settings: '帳號設定',
    admin: '使用者管理'
  };
  return map[view];
}

function LoginScreen({
  onLogin,
  error,
  setError
}: {
  onLogin: (token: string, user: User) => void;
  error: string;
  setError: (value: string) => void;
}) {
  const [username, setUsername] = useState('james');
  const [password, setPassword] = useState('password123');
  const [loading, setLoading] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const payload = await api.login(username, password);
      onLogin(payload.accessToken, payload.user);
    } catch (err) {
      setError(err instanceof Error ? err.message : '登入失敗');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-panel">
        <div className="brand large">
          <span className="brand-mark">F</span>
          <span>Finx</span>
        </div>
        <form onSubmit={submit} className="form-stack">
          <label>
            帳號
            <input value={username} onChange={(e) => setUsername(e.target.value)} />
          </label>
          <label>
            密碼
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </label>
          {error && <div className="error">{error}</div>}
          <button className="primary" disabled={loading}>
            {loading ? '登入中' : '登入'}
          </button>
        </form>
        {/* <p className="hint">admin / password123，james / password123</p> */}
      </section>
    </main>
  );
}

function FirstLoginScreen({ user, onComplete, onLogout }: { user: User; onComplete: (user: User) => void; onLogout: () => void }) {
  const [form, setForm] = useState({
    currentPassword: '',
    newPassword: '',
    fullName: user.fullName,
    email: user.email
  });
  const [error, setError] = useState('');

  async function submit(event: FormEvent) {
    event.preventDefault();
    try {
      const updated = await api.completeFirstLogin(form);
      onComplete(updated);
    } catch (err) {
      setError(err instanceof Error ? err.message : '設定失敗');
    }
  }

  return (
    <main className="login-page">
      <section className="login-panel wide">
        <h1>首次登入設定</h1>
        <form onSubmit={submit} className="form-grid">
          <label>
            目前密碼
            <input type="password" value={form.currentPassword} onChange={(e) => setForm({ ...form, currentPassword: e.target.value })} />
          </label>
          <label>
            新密碼
            <input type="password" value={form.newPassword} onChange={(e) => setForm({ ...form, newPassword: e.target.value })} />
          </label>
          <label>
            姓名
            <input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} />
          </label>
          <label>
            Email
            <input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
          </label>
          {error && <div className="error span-2">{error}</div>}
          <div className="actions span-2">
            <button type="button" className="secondary" onClick={onLogout}>登出</button>
            <button className="primary"><Save size={16} /> 儲存</button>
          </div>
        </form>
      </section>
    </main>
  );
}

function DashboardView({ onOpenExpenses }: { onOpenExpenses: (filter: ExpenseFilterTarget) => void }) {
  const [data, setData] = useState<Dashboard | null>(null);
  const [error, setError] = useState('');
  const [selectedMonth, setSelectedMonth] = useState(new Date().toISOString().slice(0, 7));

  useEffect(() => {
    setData(null);
    api.dashboard(selectedMonth).then(setData).catch((err) => setError(err.message));
  }, [selectedMonth]);

  if (error) return <div className="error">{error}</div>;
  if (!data) return <div className="panel">載入中</div>;

  const categoryRows = Object.entries(data.categoryTotals)
    .sort(([, a], [, b]) => b - a)
    .map(([name, amount]) => ({ name, amount }));
  const monthlyRows = Object.entries(data.monthlyTotals).map(([month, amount]) => ({
    month: `${Number(month.slice(5))}月`,
    amount
  }));
  const cardRows = Object.entries(data.cardTotals).map(([name, amount]) => ({ name, amount }));
  const colors = ['#0f766e', '#2563eb', '#f59e0b', '#db2777', '#7c3aed', '#0891b2', '#65a30d', '#ea580c', '#64748b'];
  const chartTotal = categoryRows.reduce((total, row) => total + row.amount, 0);
  const monthChange = data.previousMonthExpense === 0 ? null : ((data.monthExpense - data.previousMonthExpense) / data.previousMonthExpense) * 100;
  const monthOptions = Array.from({ length: 12 }, (_, index) => {
    const date = new Date();
    date.setDate(1);
    date.setMonth(date.getMonth() - index);
    return date.toISOString().slice(0, 7);
  });

  return (
    <section className="dashboard-charts">
      <div className="dashboard-hero">
        <img src={assetPath('assets/dashboard-cover.png')} alt="Finx 個人支出分析" />
        <div className="dashboard-hero-copy"><span>FINX PERSONAL FINANCE</span><h2>掌握每一筆支出</h2><p>用清楚的圖表，看見自己的消費節奏。</p></div>
      </div>
      <div className="dashboard-toolbar">
        <div><span className="section-kicker">個人支出分析</span><h2>{data.selectedMonth.replace('-', ' 年 ')} 月儀表板</h2></div>
        <div className="dashboard-controls"><label htmlFor="dashboard-month">分析月份</label><select id="dashboard-month" value={selectedMonth} onChange={(event) => setSelectedMonth(event.target.value)}>{monthOptions.map((month) => <option key={month} value={month}>{month.replace('-', ' 年 ')} 月</option>)}</select><span className="data-updated">資料即時更新</span></div>
      </div>
      <div className="dashboard-summary">
        <div><span>選定月份支出</span><strong>{money(data.monthExpense)}</strong></div>
        <div><span>上月支出</span><strong>{money(data.previousMonthExpense)}</strong></div>
        <div className={monthChange !== null && monthChange > 0 ? 'change up' : 'change'}><span>較上月變化</span><strong>{monthChange === null ? '無前期資料' : `${monthChange > 0 ? '+' : ''}${monthChange.toFixed(1)}%`}</strong></div>
      </div>
      <div className="panel dashboard-wide-chart">
        <div className="panel-heading">
          <div>
            <span className="section-kicker">消費趨勢</span>
            <h2>最近 6 個月支出</h2>
          </div>
          <strong className="chart-total">{money(data.monthExpense)} <small>本月</small></strong>
        </div>
        <div className="rechart-box trend-chart"><ResponsiveContainer width="100%" height="100%"><LineChart data={monthlyRows} margin={{ top: 12, right: 12, left: 4, bottom: 4 }}><CartesianGrid stroke="#e9eef0" vertical={false} /><XAxis dataKey="month" tickLine={false} axisLine={false} /><YAxis tickFormatter={(value) => `${Math.round(value / 1000)}k`} tickLine={false} axisLine={false} width={38} /><Tooltip formatter={(value) => money(Number(value))} /><Line type="monotone" dataKey="amount" name="支出" stroke="#0f766e" strokeWidth={3} dot={{ r: 4, fill: '#0f766e' }} activeDot={{ r: 6 }} /></LineChart></ResponsiveContainer></div>
      </div>

      <div className="panel dashboard-chart">
        <div className="panel-heading">
          <div><span className="section-kicker">選定月份分析</span><h2>支出占比</h2></div>
        </div>
        <div className="rechart-box pie-chart"><ResponsiveContainer width="100%" height="100%"><PieChart><Pie data={categoryRows} dataKey="amount" nameKey="name" innerRadius={58} outerRadius={84} paddingAngle={2} onClick={(entry) => onOpenExpenses({ category: entry.name })}>{categoryRows.map((row, index) => <Cell key={row.name} fill={colors[index % colors.length]} />)}</Pie><Tooltip formatter={(value) => money(Number(value))} /><Legend /></PieChart></ResponsiveContainer></div>
      </div>

      <div className="panel dashboard-chart">
        <div className="panel-heading"><div><span className="section-kicker">消費結構</span><h2>分類支出排行</h2></div><span className="muted">共 {money(chartTotal)}</span></div>
        <div className="rechart-box category-chart"><ResponsiveContainer width="100%" height="100%"><BarChart data={categoryRows} layout="vertical" margin={{ top: 4, right: 18, left: 4, bottom: 4 }}><CartesianGrid stroke="#e9eef0" horizontal={false} /><XAxis type="number" hide /><YAxis dataKey="name" type="category" width={54} tickLine={false} axisLine={false} /><Tooltip formatter={(value) => money(Number(value))} /><Bar dataKey="amount" name="支出" fill="#2563eb" radius={[0, 6, 6, 0]} onClick={(_, index) => { if (typeof index === 'number') onOpenExpenses({ category: categoryRows[index].name }); }} /></BarChart></ResponsiveContainer></div>
      </div>

      <div className="panel dashboard-chart">
        <div className="panel-heading"><div><span className="section-kicker">付款工具</span><h2>信用卡支出比較</h2></div><span className="muted">本月</span></div>
        {cardRows.length === 0 ? <EmptyState label="本月尚無信用卡支出" /> : <div className="rechart-box card-chart"><ResponsiveContainer width="100%" height="100%"><BarChart data={cardRows} margin={{ top: 8, right: 12, left: 4, bottom: 28 }}><CartesianGrid stroke="#e9eef0" vertical={false} /><XAxis dataKey="name" tickLine={false} axisLine={false} angle={-20} textAnchor="end" interval={0} /><YAxis tickFormatter={(value) => `${Math.round(value / 1000)}k`} tickLine={false} axisLine={false} width={34} /><Tooltip formatter={(value) => money(Number(value))} /><Bar dataKey="amount" name="支出" fill="#f59e0b" radius={[6, 6, 0, 0]} /></BarChart></ResponsiveContainer></div>}
      </div>
    </section>
  );
}

function ExpensesView({ initialFilter = {} }: { initialFilter?: ExpenseFilterTarget }) {
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [cards, setCards] = useState<CreditCard[]>([]);
  const [editing, setEditing] = useState<Expense | null>(null);
  const [form, setForm] = useState(emptyExpense);
  const [filters, setFilters] = useState({ from: '', to: '', category: initialFilter.category ?? '', creditCardId: (initialFilter.creditCardId ?? '') as number | '' });
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);

  const load = () => api.expenses(filters).then(setExpenses).catch((err) => setError(err.message));

  useEffect(() => {
    api.creditCards().then(setCards);
  }, []);
  useEffect(() => {
    void load();
  }, [filters.category, filters.creditCardId]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    const payload = { ...form, amount: Number(form.amount), creditCardId: form.creditCardId || undefined };
    setSaving(true);
    setError('');
    try {
      if (editing) await api.updateExpense(editing.id, payload);
      else await api.createExpense(payload);
      setEditing(null);
      setForm(emptyExpense);
      setMessage(editing ? '支出已更新' : '支出已新增');
      load();
    } catch (err) {
      setError(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSaving(false);
    }
  }

  function edit(expense: Expense) {
    setEditing(expense);
    setForm({
      category: expense.category,
      merchant: expense.merchant,
      note: expense.note || '',
      amount: expense.amount,
      spentOn: expense.spentOn,
      creditCardId: expense.creditCardId
    });
  }

  return (
    <section className="split-layout">
      <div className="panel">
        <h2>{editing ? '編輯支出' : '新增支出'}</h2>
        <form onSubmit={submit} className="form-stack">
          <label>
            分類
            <select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
              {categories.map((category) => <option key={category}>{category}</option>)}
            </select>
          </label>
          <label>
            商店
            <input value={form.merchant} onChange={(e) => setForm({ ...form, merchant: e.target.value })} required />
          </label>
          <label>
            金額
            <input type="number" min="1" value={form.amount} onChange={(e) => setForm({ ...form, amount: Number(e.target.value) })} required />
          </label>
          <label>
            日期
            <input type="date" value={form.spentOn} onChange={(e) => setForm({ ...form, spentOn: e.target.value })} required />
          </label>
          <label>
            信用卡
            <select value={form.creditCardId ?? ''} onChange={(e) => setForm({ ...form, creditCardId: e.target.value ? Number(e.target.value) : undefined })}>
              <option value="">不指定</option>
              {cards.map((card) => <option value={card.id} key={card.id}>{card.bankName} {card.cardName} • {card.lastFourDigits}</option>)}
            </select>
          </label>
          <label>
            備註
            <textarea value={form.note} onChange={(e) => setForm({ ...form, note: e.target.value })} />
          </label>
          {error && <div className="error">{error}</div>}
          <div className="actions">
            {editing && <button type="button" className="secondary" onClick={() => { setEditing(null); setForm(emptyExpense); }}>取消</button>}
            <button className="primary" disabled={saving}><Save size={16} /> {saving ? '儲存中...' : '儲存'}</button>
          </div>
        </form>
      </div>
      <div className="panel wide-panel">
        {message && <div className="success">{message}</div>}
        <div className="toolbar">
          <select value={filters.category} onChange={(e) => setFilters({ ...filters, category: e.target.value })}>
            <option value="">全部分類</option>
            {categories.map((category) => <option key={category}>{category}</option>)}
          </select>
          <select value={filters.creditCardId} onChange={(e) => setFilters({ ...filters, creditCardId: e.target.value ? Number(e.target.value) : '' })}>
            <option value="">全部信用卡</option>
            {cards.map((card) => <option value={card.id} key={card.id}>{card.bankName} • {card.lastFourDigits}</option>)}
          </select>
          <button className="secondary" onClick={load}>查詢</button>
          <button className="text-button" onClick={() => setFilters({ from: '', to: '', category: '', creditCardId: '' })}>清除篩選</button>
        </div>
        <ExpenseTable expenses={expenses} onEdit={edit} onDelete={async (id) => { if (!window.confirm('確定要刪除此筆支出嗎？')) return; await api.deleteExpense(id); setMessage('支出已刪除'); load(); }} />
      </div>
    </section>
  );
}

function CardsView() {
  const [cards, setCards] = useState<CreditCard[]>([]);
  const [form, setForm] = useState(emptyCard);
  const [editing, setEditing] = useState<CreditCard | null>(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);
  const load = () => api.creditCards().then(setCards).catch((err) => setError(err.message));

  useEffect(() => {
    void load();
  }, []);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      if (editing) await api.updateCreditCard(editing.id, form);
      else await api.createCreditCard(form);
      setEditing(null);
      setForm(emptyCard);
      setMessage(editing ? '信用卡資料已更新' : '信用卡已新增');
      load();
    } catch (err) {
      setError(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSaving(false);
    }
  }

  function edit(card: CreditCard) {
    setEditing(card);
    setForm({
      bankName: card.bankName,
      cardName: card.cardName,
      network: card.network,
      lastFourDigits: card.lastFourDigits,
      creditLimit: card.creditLimit,
      statementDay: card.statementDay,
      paymentDueDay: card.paymentDueDay
    });
  }

  return (
    <section className="split-layout">
      <div className="panel">
        <h2>{editing ? '編輯信用卡' : '新增信用卡'}</h2>
        <p className="form-help">這裡管理您已持有的信用卡，並在記帳時選擇支出的付款卡片。</p>
        <form onSubmit={submit} className="form-stack">
          <label>銀行<input value={form.bankName} onChange={(e) => setForm({ ...form, bankName: e.target.value })} required /></label>
          <label>卡名<input value={form.cardName} onChange={(e) => setForm({ ...form, cardName: e.target.value })} required /></label>
          <label>
            組織
            <select value={form.network} onChange={(e) => setForm({ ...form, network: e.target.value as CardNetwork })}>
              {networks.map((network) => <option key={network}>{network}</option>)}
            </select>
          </label>
          <label>後四碼<input maxLength={4} value={form.lastFourDigits} onChange={(e) => setForm({ ...form, lastFourDigits: e.target.value.replace(/\D/g, '') })} required /></label>
          <label>額度<input type="number" min="1" value={form.creditLimit} onChange={(e) => setForm({ ...form, creditLimit: Number(e.target.value) })} required /></label>
          <label>帳單日<input type="number" min="1" max="31" value={form.statementDay} onChange={(e) => setForm({ ...form, statementDay: Number(e.target.value) })} required /></label>
          <label>繳款日<input type="number" min="1" max="31" value={form.paymentDueDay} onChange={(e) => setForm({ ...form, paymentDueDay: Number(e.target.value) })} required /></label>
          {error && <div className="error">{error}</div>}
          <div className="actions">
            {editing && <button type="button" className="secondary" onClick={() => { setEditing(null); setForm(emptyCard); }}>取消</button>}
            <button className="primary" disabled={saving}><Save size={16} /> {saving ? '儲存中...' : '儲存'}</button>
          </div>
        </form>
      </div>
      <div className="card-grid">
        {message && <div className="success card-grid-message">{message}</div>}
        {cards.length === 0 && <EmptyState label="尚未建立信用卡" />}
        {cards.map((card) => (
          <article className={`credit-card ${card.network.toLowerCase()} ${cardTheme(card.cardName)}`} key={card.id}>
            <div className="card-head">
              <span className="card-bank">{card.bankName}</span>
              <strong className="card-network">{card.network}</strong>
            </div>
            <div className="card-chip" aria-hidden="true" />
            <p className="card-number">••••  ••••  ••••  {card.lastFourDigits}</p>
            <h2>{card.cardName}</h2>
            <div className="card-stats">
              <span>本月 {money(card.currentMonthExpense)}</span>
              <span>額度 {money(card.creditLimit)}</span>
              <span>帳單 {card.statementDay} 日</span>
              <span>繳款 {card.paymentDueDay} 日</span>
            </div>
            <div className="actions">
              <button className="secondary" onClick={() => edit(card)}>編輯</button>
              <button className="danger" onClick={() => { if (window.confirm(`確定要刪除 ${card.cardName} 嗎？`)) api.deleteCreditCard(card.id).then(() => { setMessage('信用卡已刪除'); load(); }); }}><Trash2 size={16} /> 刪除</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function cardTheme(cardName: string) {
  if (cardName.includes('玫瑰') || cardName.toLowerCase().includes('giving')) return 'rose-giving';
  if (cardName.toLowerCase().includes('cube')) return 'cube';
  return '';
}

function SettingsView({ user, onUserChange }: { user: User; onUserChange: (user: User) => void }) {
  const [profile, setProfile] = useState({ fullName: user.fullName, email: user.email });
  const [password, setPassword] = useState({ currentPassword: '', newPassword: '' });
  const [message, setMessage] = useState('');

  async function saveProfile(e: FormEvent) {
    e.preventDefault();
    const updated = await api.updateProfile(profile);
    onUserChange(updated);
    setMessage('個人資料已更新');
  }

  async function savePassword(e: FormEvent) {
    e.preventDefault();
    await api.changePassword(password);
    setPassword({ currentPassword: '', newPassword: '' });
    setMessage('登入密碼已更新');
  }

  return (
    <section className="settings-page">
      <div className="settings-intro">
        <div className="settings-avatar"><UserRound size={24} /></div>
        <div><h2>{user.fullName}</h2><p>管理您的 Finx 登入資料與帳戶安全設定</p></div>
        <span className="role-badge">{user.role === 'ADMIN' ? '管理者' : '一般使用者'}</span>
      </div>
      {message && <div className="success">{message}</div>}
      <div className="settings-sections">
        <section className="panel settings-section">
          <div className="settings-section-title"><UserRound size={20} /><div><h2>個人資料</h2><p>更新您的基本聯絡資訊</p></div></div>
          <form className="form-grid" onSubmit={saveProfile}>
            <label>登入帳號<input value={user.username} disabled /></label>
            <label>使用者角色<input value={user.role === 'ADMIN' ? '管理者' : '一般使用者'} disabled /></label>
            <label>姓名<input value={profile.fullName} onChange={(e) => setProfile({ ...profile, fullName: e.target.value })} required /></label>
            <label>Email<input type="email" value={profile.email} onChange={(e) => setProfile({ ...profile, email: e.target.value })} required /></label>
            <div className="settings-actions"><button className="primary"><Save size={16} /> 儲存個人資料</button></div>
          </form>
        </section>
        <section className="panel settings-section">
          <div className="settings-section-title"><LockKeyhole size={20} /><div><h2>登入安全</h2><p>定期更新密碼，保護您的帳戶</p></div></div>
          <form className="form-grid" onSubmit={savePassword}>
            <label>目前密碼<input type="password" value={password.currentPassword} onChange={(e) => setPassword({ ...password, currentPassword: e.target.value })} required /></label>
            <label>新密碼<input type="password" minLength={8} value={password.newPassword} onChange={(e) => setPassword({ ...password, newPassword: e.target.value })} /></label>
            <div className="settings-actions"><button className="primary"><LockKeyhole size={16} /> 更新登入密碼</button></div>
          </form>
        </section>
      </div>
    </section>
  );
}

function AdminView() {
  const [summary, setSummary] = useState<AdminSummary | null>(null);
  const [users, setUsers] = useState<User[]>([]);
  const [form, setForm] = useState({ username: '', email: '', fullName: '', temporaryPassword: 'Temp1234', role: 'USER' as Role });
  const [error, setError] = useState('');
  const load = () => {
    api.adminSummary().then(setSummary);
    api.adminUsers().then(setUsers).catch((err) => setError(err.message));
  };

  useEffect(load, []);

  async function create(event: FormEvent) {
    event.preventDefault();
    try {
      await api.createUser(form);
      setForm({ username: '', email: '', fullName: '', temporaryPassword: 'Temp1234', role: 'USER' });
      load();
    } catch (err) {
      setError(err instanceof Error ? err.message : '新增失敗');
    }
  }

  return (
    <section className="content-grid">
      {summary && (
        <>
          <Metric label="使用者" value={`${summary.users}`} tone="blue" />
          <Metric label="啟用中" value={`${summary.activeUsers}`} tone="green" />
          <Metric label="支出筆數" value={`${summary.expenses}`} tone="amber" />
          <Metric label="本月總支出" value={money(summary.currentMonthExpense)} tone="red" />
        </>
      )}
      <div className="panel">
        <h2>新增使用者</h2>
        <form className="form-stack" onSubmit={create}>
          <label>帳號<input value={form.username} onChange={(e) => setForm({ ...form, username: e.target.value })} required /></label>
          <label>姓名<input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required /></label>
          <label>Email<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></label>
          <label>臨時密碼<input value={form.temporaryPassword} onChange={(e) => setForm({ ...form, temporaryPassword: e.target.value })} required /></label>
          <label>
            角色
            <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value as Role })}>
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </label>
          {error && <div className="error">{error}</div>}
          <button className="primary"><Plus size={16} /> 新增</button>
        </form>
      </div>
      <div className="panel span-2">
        <h2>使用者列表</h2>
        <div className="table">
          <div className="row head"><span>帳號</span><span>姓名</span><span>角色</span><span>狀態</span><span>首次設定</span><span>操作</span></div>
          {users.map((user) => (
            <div className="row" key={user.id}>
              <span>{user.username}</span>
              <span>{user.fullName}</span>
              <span>
                <select value={user.role} onChange={(e) => api.updateUserRole(user.id, e.target.value as Role).then(load)}>
                  <option value="USER">USER</option>
                  <option value="ADMIN">ADMIN</option>
                </select>
              </span>
              <span>{user.active ? '啟用' : '停用'}</span>
              <span>{user.mustChangePassword ? '未完成' : '完成'}</span>
              <span>
                <button className="secondary" onClick={() => api.updateUserStatus(user.id, !user.active).then(load)}>
                  {user.active ? '停用' : '啟用'}
                </button>
              </span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

function Metric({ label, value, tone }: { label: string; value: string; tone: 'blue' | 'red' | 'green' | 'amber' }) {
  return (
    <div className={`metric ${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function ExpenseList({ expenses }: { expenses: Expense[] }) {
  if (expenses.length === 0) return <EmptyState label="沒有支出紀錄" />;
  return (
    <div className="list">
      {expenses.map((expense) => (
        <div className="list-item" key={expense.id}>
          <div>
            <strong>{expense.merchant}</strong>
            <span>{expense.category} · {expense.spentOn}</span>
          </div>
          <b>{money(expense.amount)}</b>
        </div>
      ))}
    </div>
  );
}

function ExpenseTable({ expenses, onEdit, onDelete }: { expenses: Expense[]; onEdit: (expense: Expense) => void; onDelete: (id: number) => void }) {
  if (expenses.length === 0) return <EmptyState label="沒有符合條件的支出" />;
  return (
    <div className="table">
      <div className="row head"><span>日期</span><span>分類</span><span>商店</span><span>信用卡</span><span>金額</span><span>操作</span></div>
      {expenses.map((expense) => (
        <div className="row" key={expense.id}>
          <span>{expense.spentOn}</span>
          <span>{expense.category}</span>
          <span>{expense.merchant}</span>
          <span>{expense.creditCardLabel || '-'}</span>
          <span>{money(expense.amount)}</span>
          <span className="actions compact">
            <button className="secondary" onClick={() => onEdit(expense)}>編輯</button>
            <button className="danger" aria-label="刪除" onClick={() => onDelete(expense.id)}><Trash2 size={16} /></button>
          </span>
        </div>
      ))}
    </div>
  );
}

function EmptyState({ label }: { label: string }) {
  return (
    <div className="empty">
      <ShieldCheck size={28} />
      <span>{label}</span>
    </div>
  );
}
