-- ============================================================
-- T47 AI 订单异常识别助手 - 初始化数据（全部为模拟数据，不含真实个人信息）
-- 版本: v1.0
-- 前置: 已执行 schema.sql
-- 执行: mysql -u root -p t47_anomaly < database/seed.sql
-- 说明: 所有账号密码明文均为 123456（仅用于课程演示），库中存 BCrypt 哈希
-- ============================================================

USE t47_anomaly;
SET NAMES utf8mb4;

START TRANSACTION;

-- ------------------------------------------------------------
-- 1. 角色（3 条）
-- ------------------------------------------------------------
INSERT INTO sys_role (id, code, name, description) VALUES
(1, 'ADMIN',    '管理员',   '系统管理：用户与角色管理，可执行全部操作'),
(2, 'AUDITOR',  '审核员',   '异常核实、订单处理、关闭与统计查看'),
(3, 'CUSTOMER', '下单客户', '创建订单、查看和取消自己的订单');

-- ------------------------------------------------------------
-- 2. 用户（10 条）
--    BCrypt 哈希对应明文 123456，由 BCryptPasswordEncoder 校验
-- ------------------------------------------------------------
INSERT INTO sys_user (id, username, password, real_name, phone, role_id, status, created_at) VALUES
(1,  'admin',      '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '系统管理员', '13800000001', 1, 1, '2026-01-01 09:00:00'),
(2,  'auditor01',  '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '张审核',     '13800000002', 2, 1, '2026-01-01 09:05:00'),
(3,  'auditor02',  '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '李审核',     '13800000003', 2, 1, '2026-01-01 09:10:00'),
(4,  'auditor03',  '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '王审核',     '13800000004', 2, 1, '2026-01-01 09:15:00'),
(5,  'customer01', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户01', '13800000005', 3, 1, '2026-01-02 10:00:00'),
(6,  'customer02', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户02', '13800000006', 3, 1, '2026-01-02 10:05:00'),
(7,  'customer03', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户03', '13800000007', 3, 1, '2026-01-02 10:10:00'),
(8,  'customer04', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户04', '13800000008', 3, 1, '2026-01-02 10:15:00'),
(9,  'customer05', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户05', '13800000009', 3, 1, '2026-01-02 10:20:00'),
(10, 'customer06', '$2b$10$8yq7YBkCOZyGjg/NqJ0CB.87MSpzPyqlrkOIIAWzhXpWuxstCwJ/6', '模拟用户06', '13800000010', 3, 1, '2026-01-02 10:25:00');

-- ------------------------------------------------------------
-- 3. 订单（15 条，覆盖全部 7 种状态）
-- ------------------------------------------------------------
INSERT INTO biz_order (id, order_no, customer_id, item_name, amount, receiver_name, receiver_phone, receiver_address,
                       status, anomaly_flag, risk_score, version, remark, created_at) VALUES
(1,  'T4720260105001', 5,  '模拟商品-蓝牙耳机',   299.00,  '模拟用户05', '13800000005', '模拟省A市B区模拟路1号', 'CREATED',   0, 5,  0, '正常订单，等待 AI 扫描', '2026-01-05 09:10:00'),
(2,  'T4720260105002', 6,  '模拟商品-机械键盘',   899.00,  '模拟用户06', '13800000006', '模拟省A市C区模拟路2号', 'CREATED',   0, 12, 0, '正常订单，等待 AI 扫描', '2026-01-05 10:20:00'),
(3,  'T4720260106001', 7,  '模拟商品-智能手表',  6800.00,  '模拟用户07', '13800000007', '模拟省A市D区模拟路3号', 'FLAGGED',   1, 92, 0, 'AI 标注为异常，待核实',   '2026-01-06 11:00:00'),
(4,  'T4720260106002', 8,  '模拟商品-笔记本电脑', 9999.00,  '模拟用户08', '13800000008', '模拟省B市A区模拟路4号', 'FLAGGED',   1, 95, 0, 'AI 标注为异常，待核实',   '2026-01-06 14:30:00'),
(5,  'T4720260107001', 9,  '模拟商品-家用投影仪', 5200.00,  '模拟用户09', '13800000009', '模拟省B市B区模拟路5号', 'CONFIRMED', 1, 88, 0, '人工核实确认异常',       '2026-01-07 09:15:00'),
(6,  'T4720260107002', 10, '模拟商品-空气净化器', 4300.00,  '模拟用户10', '13800000010', '模拟省B市C区模拟路6号', 'CONFIRMED', 1, 76, 0, '人工核实确认异常',       '2026-01-07 10:45:00'),
(7,  'T4720260108001', 5,  '模拟商品-智能手机',  8888.00,  '模拟用户05', '13800000005', '模拟省A市B区模拟路1号', 'PROCESSED', 1, 90, 0, '已退款处理',             '2026-01-08 13:20:00'),
(8,  'T4720260108002', 6,  '模拟商品-显示器',    7600.00,  '模拟用户06', '13800000006', '模拟省A市C区模拟路2号', 'PROCESSED', 1, 85, 0, '已退款处理',             '2026-01-08 15:00:00'),
(9,  'T4720260109001', 7,  '模拟商品-平板电脑',  3100.00,  '模拟用户07', '13800000007', '模拟省A市D区模拟路3号', 'PROCESSED', 1, 72, 0, '拦截后放行',             '2026-01-09 09:30:00'),
(10, 'T4720260109002', 8,  '模拟商品-数码相机', 12000.00,  '模拟用户08', '13800000008', '模拟省B市A区模拟路4号', 'PROCESSED', 1, 97, 0, '已退款处理',             '2026-01-09 11:40:00'),
(11, 'T4720260110001', 9,  '模拟商品-头戴耳机',  6200.00,  '模拟用户09', '13800000009', '模拟省B市B区模拟路5号', 'DISMISSED', 1, 64, 0, '人工判定误报',           '2026-01-10 10:00:00'),
(12, 'T4720260110002', 10, '模拟商品-无线键盘',   880.00,  '模拟用户10', '13800000010', '模拟省B市C区模拟路6号', 'DISMISSED', 1, 61, 0, '人工判定误报',           '2026-01-10 16:20:00'),
(13, 'T4720260111001', 5,  '模拟商品-运动手表',  4500.00,  '模拟用户05', '13800000005', '模拟省A市B区模拟路1号', 'CLOSED',    1, 70, 0, '处理完成已关闭',         '2026-01-11 09:50:00'),
(14, 'T4720260111002', 6,  '模拟商品-蓝牙音箱',  2300.00,  '模拟用户06', '13800000006', '模拟省A市C区模拟路2号', 'CLOSED',    1, 66, 0, '处理完成已关闭',         '2026-01-11 14:10:00'),
(15, 'T4720260112001', 7,  '模拟商品-显示器',   15000.00,  '模拟用户07', '13800000007', '模拟省A市D区模拟路3号', 'CANCELED',  0, 0,  0, '用户主动取消',           '2026-01-12 10:30:00');

-- ------------------------------------------------------------
-- 4. AI 异常标注结果（15 条，覆盖 R1-R4 规则）
-- ------------------------------------------------------------
INSERT INTO ai_anomaly_result (order_id, rule_code, rule_name, score, level, reason, created_at) VALUES
(3,  'R1_AMOUNT',    '金额异常',     92, 'HIGH',   '订单金额 6800.00 元，超过该用户历史均值 3 倍',   '2026-01-06 11:05:00'),
(3,  'R3_FREQUENCY', '下单频率异常', 70, 'MEDIUM', '该用户 10 分钟内连续下单 6 笔',                  '2026-01-06 11:05:00'),
(4,  'R1_AMOUNT',    '金额异常',     95, 'HIGH',   '订单金额 9999.00 元，超过风险阈值 5000 元',       '2026-01-06 14:35:00'),
(4,  'R4_DEVICE',    '风险设备',     88, 'HIGH',   '下单设备命中模拟风险设备库',                      '2026-01-06 14:35:00'),
(5,  'R1_AMOUNT',    '金额异常',     88, 'HIGH',   '订单金额 5200.00 元，超过风险阈值 5000 元',       '2026-01-07 09:20:00'),
(6,  'R2_ADDRESS',   '收货地址异常', 76, 'MEDIUM', '收货地址与用户常用地址差异较大',                  '2026-01-07 10:50:00'),
(7,  'R1_AMOUNT',    '金额异常',     90, 'HIGH',   '订单金额 8888.00 元，超过风险阈值 5000 元',       '2026-01-08 13:25:00'),
(8,  'R1_AMOUNT',    '金额异常',     85, 'HIGH',   '订单金额 7600.00 元，超过风险阈值 5000 元',       '2026-01-08 15:05:00'),
(9,  'R3_FREQUENCY', '下单频率异常', 72, 'MEDIUM', '该用户 10 分钟内连续下单 7 笔',                  '2026-01-09 09:35:00'),
(10, 'R1_AMOUNT',    '金额异常',     97, 'HIGH',   '订单金额 12000.00 元，超过风险阈值 5000 元',      '2026-01-09 11:45:00'),
(10, 'R4_DEVICE',    '风险设备',     90, 'HIGH',   '下单设备命中模拟风险设备库',                      '2026-01-09 11:45:00'),
(11, 'R1_AMOUNT',    '金额异常',     64, 'MEDIUM', '订单金额 6200.00 元，超过风险阈值（人工核实为误报）', '2026-01-10 10:05:00'),
(12, 'R2_ADDRESS',   '收货地址异常', 61, 'MEDIUM', '收货地址命中模拟地址库（人工核实为误报）',        '2026-01-10 16:25:00'),
(13, 'R1_AMOUNT',    '金额异常',     70, 'MEDIUM', '订单金额 4500.00 元，接近风险阈值',               '2026-01-11 09:55:00'),
(14, 'R2_ADDRESS',   '收货地址异常', 66, 'MEDIUM', '收货地址命中模拟地址库',                          '2026-01-11 14:15:00');

-- ------------------------------------------------------------
-- 5. 人工核实记录（10 条：订单 5-14，其中 11/12 为误报）
-- ------------------------------------------------------------
INSERT INTO order_verify_record (order_id, auditor_id, result, remark, created_at) VALUES
(5,  2, 'CONFIRMED',      '核实为刷单异常，转处理',           '2026-01-07 10:00:00'),
(6,  3, 'CONFIRMED',      '地址异常属实，转处理',             '2026-01-07 11:30:00'),
(7,  2, 'CONFIRMED',      '大额异常属实，转退款处理',         '2026-01-08 14:10:00'),
(8,  3, 'CONFIRMED',      '大额异常属实，转退款处理',         '2026-01-08 15:50:00'),
(9,  4, 'CONFIRMED',      '高频下单属实，先拦截',             '2026-01-09 10:20:00'),
(10, 2, 'CONFIRMED',      '大额且设备异常，转退款处理',       '2026-01-09 12:30:00'),
(11, 3, 'FALSE_POSITIVE', '用户为老客户正常大额消费，判定误报', '2026-01-10 11:00:00'),
(12, 4, 'FALSE_POSITIVE', '地址为临时收货地址，判定误报',     '2026-01-10 17:00:00'),
(13, 2, 'CONFIRMED',      '金额接近阈值但确属异常',           '2026-01-11 10:40:00'),
(14, 3, 'CONFIRMED',      '地址异常属实',                     '2026-01-11 15:00:00');

-- ------------------------------------------------------------
-- 6. 处理记录（10 条：退款 / 拦截 / 放行，允许同一订单多次处理）
-- ------------------------------------------------------------
INSERT INTO order_handle_record (order_id, handler_id, action, remark, created_at) VALUES
(7,  2, 'REFUND',  '已原路退款',                     '2026-01-08 14:30:00'),
(8,  3, 'REFUND',  '已原路退款',                     '2026-01-08 16:10:00'),
(9,  4, 'BLOCK',   '已拦截发货',                     '2026-01-09 10:40:00'),
(9,  4, 'RELEASE', '用户申诉通过，放行',             '2026-01-09 15:00:00'),
(10, 2, 'REFUND',  '已原路退款并冻结账号',           '2026-01-09 12:50:00'),
(10, 2, 'RELEASE', '退款完成后释放库存',             '2026-01-09 13:20:00'),
(13, 2, 'BLOCK',   '先拦截发货',                     '2026-01-11 10:50:00'),
(13, 2, 'RELEASE', '核实后放行',                     '2026-01-11 11:30:00'),
(14, 3, 'BLOCK',   '先拦截发货',                     '2026-01-11 15:10:00'),
(14, 3, 'RELEASE', '核实后放行',                     '2026-01-11 15:50:00');

-- ------------------------------------------------------------
-- 7. 状态变更日志（46 条，用 INSERT ... SELECT 按订单状态批量生成）
-- ------------------------------------------------------------
-- 创建
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, NULL, 'CREATED', customer_id, 'CUSTOMER', '用户下单', created_at FROM biz_order;

-- CREATED -> FLAGGED（AI 自动标注）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'CREATED', 'FLAGGED', NULL, 'SYSTEM', 'AI 规则引擎命中异常，待人工核实', DATE_ADD(created_at, INTERVAL 5 MINUTE)
FROM biz_order WHERE status IN ('FLAGGED', 'CONFIRMED', 'PROCESSED', 'DISMISSED', 'CLOSED');

-- FLAGGED -> CONFIRMED（人工核实确认）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'FLAGGED', 'CONFIRMED', 2, 'AUDITOR', '人工核实确认异常', DATE_ADD(created_at, INTERVAL 40 MINUTE)
FROM biz_order WHERE status IN ('CONFIRMED', 'PROCESSED', 'CLOSED');

-- CONFIRMED -> PROCESSED（处理）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'CONFIRMED', 'PROCESSED', 3, 'AUDITOR', '已处理（退款/拦截/放行）', DATE_ADD(created_at, INTERVAL 120 MINUTE)
FROM biz_order WHERE status IN ('PROCESSED', 'CLOSED');

-- PROCESSED -> CLOSED（关闭）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'PROCESSED', 'CLOSED', 2, 'AUDITOR', '处理完成，关闭订单', DATE_ADD(created_at, INTERVAL 180 MINUTE)
FROM biz_order WHERE status = 'CLOSED';

-- FLAGGED -> DISMISSED（误报驳回）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'FLAGGED', 'DISMISSED', 3, 'AUDITOR', '人工判定为误报，驳回', DATE_ADD(created_at, INTERVAL 40 MINUTE)
FROM biz_order WHERE status = 'DISMISSED';

-- CREATED -> CANCELED（用户取消）
INSERT INTO order_status_log (order_id, from_status, to_status, operator_id, operator_role, remark, created_at)
SELECT id, 'CREATED', 'CANCELED', customer_id, 'CUSTOMER', '用户取消订单', DATE_ADD(created_at, INTERVAL 15 MINUTE)
FROM biz_order WHERE status = 'CANCELED';

-- ------------------------------------------------------------
-- 8. 系统操作日志（10 条示例）
-- ------------------------------------------------------------
INSERT INTO sys_operation_log (user_id, module, action, method, uri, ip, cost_ms, success, error_msg, created_at) VALUES
(1,  'AUTH',   '管理员登录',       'POST', '/api/auth/login',            '127.0.0.1', 35,  1, NULL,                              '2026-01-12 09:00:00'),
(2,  'AUTH',   '审核员登录',       'POST', '/api/auth/login',            '127.0.0.1', 28,  1, NULL,                              '2026-01-12 09:10:00'),
(5,  'AUTH',   '客户登录',         'POST', '/api/auth/login',            '127.0.0.1', 31,  1, NULL,                              '2026-01-12 09:20:00'),
(5,  'ORDER',  '创建订单',         'POST', '/api/orders',                '127.0.0.1', 56,  1, NULL,                              '2026-01-12 09:30:00'),
(7,  'ORDER',  '查询我的订单',     'GET',  '/api/orders/my',             '127.0.0.1', 22,  1, NULL,                              '2026-01-12 09:40:00'),
(2,  'VERIFY', '核实订单',         'POST', '/api/orders/5/verify',       '127.0.0.1', 88,  1, NULL,                              '2026-01-12 10:00:00'),
(3,  'VERIFY', '核实订单',         'POST', '/api/orders/6/verify',       '127.0.0.1', 76,  1, NULL,                              '2026-01-12 10:10:00'),
(2,  'HANDLE', '处理订单',         'POST', '/api/orders/7/handle',       '127.0.0.1', 64,  1, NULL,                              '2026-01-12 10:20:00'),
(6,  'ORDER',  '查看他人订单被拒', 'GET',  '/api/orders/1',              '127.0.0.1', 18,  0, '403 无权访问该资源',               '2026-01-12 10:30:00'),
(2,  'STATS',  '查看统计概览',     'GET',  '/api/stats/overview',        '127.0.0.1', 45,  1, NULL,                              '2026-01-12 10:40:00');

COMMIT;

-- 自检：各表行数（期末提交前可再跑一次确认）
-- SELECT 'sys_role' AS t, COUNT(*) FROM sys_role
-- UNION ALL SELECT 'sys_user', COUNT(*) FROM sys_user
-- UNION ALL SELECT 'biz_order', COUNT(*) FROM biz_order
-- UNION ALL SELECT 'ai_anomaly_result', COUNT(*) FROM ai_anomaly_result
-- UNION ALL SELECT 'order_verify_record', COUNT(*) FROM order_verify_record
-- UNION ALL SELECT 'order_handle_record', COUNT(*) FROM order_handle_record
-- UNION ALL SELECT 'order_status_log', COUNT(*) FROM order_status_log
-- UNION ALL SELECT 'sys_operation_log', COUNT(*) FROM sys_operation_log;
