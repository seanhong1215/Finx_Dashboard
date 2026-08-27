INSERT INTO users (username, email, password, full_name, role, is_active, must_change_password, created_at, updated_at) VALUES
('admin', 'admin@finx.local',
 '$2a$10$SCFHDnw29DBerfAeWiMb5ev53EG2Y9s6FqXEmkyS3paVb6LKoJnXK',
 'Finx Administrator', 'ADMIN', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    full_name = VALUES(full_name),
    role = VALUES(role),
    is_active = VALUES(is_active),
    must_change_password = VALUES(must_change_password),
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO users (username, email, password, full_name, role, is_active, must_change_password, created_at, updated_at) VALUES
('james', 'james.wilson@example.com',
 '$2a$10$SCFHDnw29DBerfAeWiMb5ev53EG2Y9s6FqXEmkyS3paVb6LKoJnXK',
 'James Wilson', 'USER', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    email = VALUES(email),
    password = VALUES(password),
    full_name = VALUES(full_name),
    role = VALUES(role),
    is_active = VALUES(is_active),
    must_change_password = VALUES(must_change_password),
    updated_at = CURRENT_TIMESTAMP;

DELETE FROM expenses WHERE user_id = (SELECT id FROM users WHERE username = 'james');
DELETE FROM credit_cards WHERE user_id = (SELECT id FROM users WHERE username = 'james');

INSERT INTO credit_cards
(id, user_id, bank_name, card_name, network, last_four_digits, credit_limit, statement_day, payment_due_day, current_cycle_paid, is_active, created_at, updated_at) VALUES
(1, (SELECT id FROM users WHERE username = 'james'), '台新銀行', '玫瑰Giving卡', 'VISA', '4521', 180000.00, 12, 27, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, (SELECT id FROM users WHERE username = 'james'), '國泰世華', 'CUBE卡', 'MASTERCARD', '8832', 120000.00, 18, 3, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    bank_name = VALUES(bank_name),
    card_name = VALUES(card_name),
    network = VALUES(network),
    last_four_digits = VALUES(last_four_digits),
    credit_limit = VALUES(credit_limit),
    statement_day = VALUES(statement_day),
    payment_due_day = VALUES(payment_due_day),
    current_cycle_paid = VALUES(current_cycle_paid),
    is_active = VALUES(is_active),
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO expenses
(id, user_id, credit_card_id, category, merchant, note, amount, spent_on, created_at, updated_at) VALUES
(1, (SELECT id FROM users WHERE username = 'james'), 1, '餐飲', '星巴克', '工作日咖啡', 195.00, '2026-08-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, (SELECT id FROM users WHERE username = 'james'), 1, '交通', '台灣高鐵', '台北到台中', 700.00, '2026-08-06', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, (SELECT id FROM users WHERE username = 'james'), 1, '訂閱', 'Netflix', '月費', 449.00, '2026-08-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, (SELECT id FROM users WHERE username = 'james'), 2, '購物', 'PChome', '鍵盤與滑鼠', 2680.00, '2026-08-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, (SELECT id FROM users WHERE username = 'james'), NULL, '生活', '全聯福利中心', '日用品', 1260.00, '2026-08-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, (SELECT id FROM users WHERE username = 'james'), 1, '醫療', '藥局', '感冒藥', 380.00, '2026-08-16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, (SELECT id FROM users WHERE username = 'james'), 2, '娛樂', '威秀影城', '電影票', 640.00, '2026-08-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, (SELECT id FROM users WHERE username = 'james'), 1, '餐飲', 'Uber Eats', '晚餐', 520.00, '2026-08-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, (SELECT id FROM users WHERE username = 'james'), NULL, '教育', '線上課程', 'React 課程', 1800.00, '2026-08-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, (SELECT id FROM users WHERE username = 'james'), 1, '交通', 'Uber', '市區移動', 310.00, '2026-08-25', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, (SELECT id FROM users WHERE username = 'james'), 2, '購物', 'momo 購物', '居家用品', 1590.00, '2026-07-03', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, (SELECT id FROM users WHERE username = 'james'), NULL, '生活', '家樂福', '生活採買', 2380.00, '2026-07-08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, (SELECT id FROM users WHERE username = 'james'), 1, '餐飲', '鼎泰豐', '聚餐', 1850.00, '2026-07-12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, (SELECT id FROM users WHERE username = 'james'), 1, '訂閱', 'Spotify', '音樂訂閱', 149.00, '2026-07-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, (SELECT id FROM users WHERE username = 'james'), 2, '娛樂', '威秀影城', '電影票', 760.00, '2026-07-21', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, (SELECT id FROM users WHERE username = 'james'), 1, '交通', '台灣高鐵', '出差交通', 1490.00, '2026-06-05', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, (SELECT id FROM users WHERE username = 'james'), NULL, '醫療', '診所', '門診掛號', 850.00, '2026-06-09', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, (SELECT id FROM users WHERE username = 'james'), 2, '購物', '蝦皮購物', '辦公用品', 980.00, '2026-06-14', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, (SELECT id FROM users WHERE username = 'james'), 1, '餐飲', '路易莎咖啡', '日常咖啡', 180.00, '2026-06-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, (SELECT id FROM users WHERE username = 'james'), NULL, '教育', 'Udemy', '線上課程', 620.00, '2026-06-24', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, (SELECT id FROM users WHERE username = 'james'), 2, '生活', '全聯福利中心', '日用品', 1760.00, '2026-05-04', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, (SELECT id FROM users WHERE username = 'james'), 1, '交通', '加油站', '汽車加油', 1200.00, '2026-05-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, (SELECT id FROM users WHERE username = 'james'), 2, '娛樂', 'KKBOX', '影音訂閱', 149.00, '2026-05-16', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, (SELECT id FROM users WHERE username = 'james'), NULL, '餐飲', '燒肉店', '朋友聚餐', 2240.00, '2026-05-23', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, (SELECT id FROM users WHERE username = 'james'), 1, '購物', '無印良品', '居家用品', 1290.00, '2026-04-07', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, (SELECT id FROM users WHERE username = 'james'), 2, '生活', '家樂福', '生活採買', 1980.00, '2026-04-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, (SELECT id FROM users WHERE username = 'james'), NULL, '醫療', '藥局', '日常備藥', 460.00, '2026-04-19', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, (SELECT id FROM users WHERE username = 'james'), 1, '餐飲', '外送平台', '週末晚餐', 680.00, '2026-04-25', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, (SELECT id FROM users WHERE username = 'james'), 2, '交通', '台灣高鐵', '返鄉交通', 1320.00, '2026-03-06', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, (SELECT id FROM users WHERE username = 'james'), 1, '訂閱', 'Netflix', '影音訂閱', 390.00, '2026-03-11', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, (SELECT id FROM users WHERE username = 'james'), NULL, '購物', 'PChome', '3C 配件', 2150.00, '2026-03-17', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(32, (SELECT id FROM users WHERE username = 'james'), 2, '餐飲', '日式料理', '晚餐', 1280.00, '2026-03-22', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    credit_card_id = VALUES(credit_card_id),
    category = VALUES(category),
    merchant = VALUES(merchant),
    note = VALUES(note),
    amount = VALUES(amount),
    spent_on = VALUES(spent_on),
    updated_at = CURRENT_TIMESTAMP;
