-- ═══════════════════════════════════════════════════════
--  FinX Dashboard — 完整 Schema
-- ═══════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS finx_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE finx_db;

CREATE USER IF NOT EXISTS 'finx_user'@'localhost' IDENTIFIED BY 'finx_password';
GRANT ALL PRIVILEGES ON finx_db.* TO 'finx_user'@'localhost';
FLUSH PRIVILEGES;

-- ── users ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255) DEFAULT NULL,
    role       ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── accounts ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS accounts (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    account_number VARCHAR(20)   NOT NULL UNIQUE,
    account_type   ENUM('CHECKING','SAVINGS','INVESTMENT') NOT NULL DEFAULT 'CHECKING',
    balance        DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency       VARCHAR(3)    NOT NULL DEFAULT 'TWD',
    is_active      TINYINT(1)    NOT NULL DEFAULT 1,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── cards ─────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cards (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    account_id  BIGINT       NOT NULL,
    card_number VARCHAR(19)  NOT NULL UNIQUE,
    card_type   ENUM('VISA','MASTERCARD','AMEX') NOT NULL,
    card_holder VARCHAR(100) NOT NULL,
    expiry_date DATE         NOT NULL,
    credit_limit DECIMAL(15,2) DEFAULT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── transactions ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS transactions (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    account_id    BIGINT        NOT NULL,
    type          ENUM('INCOME','EXPENSE','TRANSFER') NOT NULL,
    category      VARCHAR(50)   NOT NULL,
    description   VARCHAR(200)  NOT NULL,
    amount        DECIMAL(15,2) NOT NULL,
    direction     ENUM('IN','OUT') NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    ref_number    VARCHAR(50)   DEFAULT NULL,
    transacted_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_tx_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── crypto_assets ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crypto_assets (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    user_id    BIGINT        NOT NULL,
    symbol     VARCHAR(10)   NOT NULL,
    name       VARCHAR(50)   NOT NULL,
    amount     DECIMAL(20,8) NOT NULL DEFAULT 0.00000000,
    avg_cost   DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    updated_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_crypto_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── stock_holdings ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS stock_holdings (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    user_id       BIGINT        NOT NULL,
    ticker        VARCHAR(10)   NOT NULL,
    company       VARCHAR(100)  NOT NULL,
    shares        DECIMAL(10,4) NOT NULL DEFAULT 0.0000,
    avg_cost      DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    current_price DECIMAL(15,2) DEFAULT NULL,
    change_pct    DECIMAL(6,2)  DEFAULT NULL,
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_stock_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ═══════════════════════════════════════════════════════
--  Seed Data
-- ═══════════════════════════════════════════════════════

-- 使用者（密碼：password123）
INSERT INTO users (username, email, password, full_name, role) VALUES
('james', 'james.wilson@example.com',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOe1cdZ5pRFNRa/Nh3hpJ1V2P8sMJBK9a',
 'James Wilson', 'USER');

-- 帳戶
INSERT INTO accounts (user_id, account_number, account_type, balance, currency) VALUES
(1, 'TW001-00000001', 'CHECKING', 156500.00, 'TWD');

-- 卡片
INSERT INTO cards (account_id, card_number, card_type, card_holder, expiry_date, credit_limit) VALUES
(1, '**** **** **** 4521', 'VISA',       'JAMES WILSON', '2027-08-31', 300000.00),
(1, '**** **** **** 8832', 'MASTERCARD', 'JAMES WILSON', '2026-12-31', 150000.00);

-- 交易紀錄
INSERT INTO transactions (account_id, type, category, description, amount, direction, balance_after, transacted_at) VALUES
(1, 'INCOME',  'Salary',          '三月薪資入帳',        80000.00, 'IN',  156500.00, '2025-03-05 09:00:00'),
(1, 'EXPENSE', 'Food & Drink',    'Starbucks',              195.00, 'OUT', 156305.00, '2025-03-04 10:30:00'),
(1, 'EXPENSE', 'Entertainment',   'Netflix 月費',           449.00, 'OUT', 155856.00, '2025-03-03 08:00:00'),
(1, 'EXPENSE', 'Shopping',        'Amazon 購物',           2699.00, 'OUT', 153157.00, '2025-03-02 14:20:00'),
(1, 'EXPENSE', 'Health',          '健身房月費',            1050.00, 'OUT', 152107.00, '2025-03-01 07:00:00'),
(1, 'EXPENSE', 'Food & Drink',    'Uber Eats',               450.00, 'OUT', 151657.00, '2025-02-28 19:00:00'),
(1, 'INCOME',  'Transfer',        '儲蓄轉入',             5000.00, 'IN',  156657.00, '2025-02-27 11:00:00'),
(1, 'EXPENSE', 'Shopping',        'PChome 購物',           3200.00, 'OUT', 153457.00, '2025-02-26 15:30:00'),
(1, 'EXPENSE', 'Entertainment',   'YouTube Premium',         229.00, 'OUT', 153228.00, '2025-02-25 08:00:00'),
(1, 'EXPENSE', 'Health',          '藥局購買',               380.00, 'OUT', 152848.00, '2025-02-24 12:00:00'),
(1, 'INCOME',  'Salary',          '二月薪資入帳',         80000.00, 'IN',  232848.00, '2025-02-05 09:00:00');

-- 加密貨幣
INSERT INTO crypto_assets (user_id, symbol, name, amount, avg_cost) VALUES
(1, 'BTC', 'Bitcoin',  0.14200000, 52000.00),
(1, 'ETH', 'Ethereum', 2.50000000, 2000.00),
(1, 'SOL', 'Solana',   15.0000000, 95.00),
(1, 'BNB', 'BNB',      3.80000000, 320.00);

-- 股票
INSERT INTO stock_holdings (user_id, ticker, company, shares, avg_cost, current_price, change_pct) VALUES
(1, 'AAPL', 'Apple Inc.',          10.0000, 170.00, 178.50,  1.20),
(1, 'NVDA', 'NVIDIA Corporation',   5.0000, 480.00, 875.40,  4.70),
(1, 'MSFT', 'Microsoft Corp.',      8.0000, 360.00, 415.30,  0.85),
(1, 'TSLA', 'Tesla Inc.',          12.0000, 220.00, 185.60, -3.10);
