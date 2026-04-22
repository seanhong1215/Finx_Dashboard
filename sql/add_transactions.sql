USE finx_db;

-- 補充更多測試交易資料
INSERT INTO transactions (account_id, type, category, description, amount, direction, balance_after, transacted_at) VALUES
(1, 'EXPENSE', 'Food & Drink',  'Uber Eats',        450.00, 'OUT', 151657.00, '2025-02-28 19:00:00'),
(1, 'INCOME',  'Transfer',      '儲蓄轉入',        5000.00, 'IN',  156657.00, '2025-02-27 11:00:00'),
(1, 'EXPENSE', 'Shopping',      'PChome 購物',     3200.00, 'OUT', 153457.00, '2025-02-26 15:30:00'),
(1, 'EXPENSE', 'Entertainment', 'YouTube Premium',  229.00, 'OUT', 153228.00, '2025-02-25 08:00:00'),
(1, 'EXPENSE', 'Health',        '藥局購買',         380.00, 'OUT', 152848.00, '2025-02-24 12:00:00'),
(1, 'INCOME',  'Salary',        '二月薪資入帳',   80000.00, 'IN',  232848.00, '2025-02-05 09:00:00');
