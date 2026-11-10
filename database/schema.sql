-- ============================================================
-- T47 AI 订单异常识别助手 - 数据库结构（MySQL 8）
-- 版本: v1.0
-- 说明: 空库执行本文件即可完成建库建表，数据用 seed.sql 初始化
-- 执行: mysql -u root -p < database/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS t47_anomaly
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE t47_anomaly;

-- 先删后建，保证脚本可重复执行（注意顺序：先删子表再删主表）
DROP TABLE IF EXISTS sys_operation_log;
DROP TABLE IF EXISTS order_status_log;
DROP TABLE IF EXISTS order_handle_record;
DROP TABLE IF EXISTS order_verify_record;
DROP TABLE IF EXISTS ai_anomaly_result;
DROP TABLE IF EXISTS biz_order;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_role;

-- ------------------------------------------------------------
-- 1. 角色表（3 种角色：管理员 / 审核员 / 下单客户）
-- ------------------------------------------------------------
CREATE TABLE sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    code        VARCHAR(32)  NOT NULL COMMENT '角色编码：ADMIN/AUDITOR/CUSTOMER',
    name        VARCHAR(64)  NOT NULL COMMENT '角色名称',
    description VARCHAR(255) NULL     COMMENT '角色说明',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '角色表';

-- ------------------------------------------------------------
-- 2. 用户表（密码 BCrypt 存储）
-- ------------------------------------------------------------
CREATE TABLE sys_user (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username   VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password   VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密后的密码，禁止明文',
    real_name  VARCHAR(64)  NOT NULL COMMENT '姓名（模拟数据）',
    phone      VARCHAR(20)  NULL     COMMENT '手机号（模拟数据）',
    role_id    BIGINT       NOT NULL COMMENT '角色ID',
    status     TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1 正常，0 禁用',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    KEY idx_user_role (role_id),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES sys_role (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- ------------------------------------------------------------
-- 3. 订单表（业务单据）
-- ------------------------------------------------------------
CREATE TABLE biz_order (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no         VARCHAR(32)   NOT NULL COMMENT '订单号（唯一，防重复提交）',
    customer_id      BIGINT        NOT NULL COMMENT '下单客户ID',
    item_name        VARCHAR(128)  NOT NULL COMMENT '商品名称',
    amount           DECIMAL(12, 2) NOT NULL COMMENT '订单金额（元）',
    receiver_name    VARCHAR(64)   NOT NULL COMMENT '收货人',
    receiver_phone   VARCHAR(20)   NOT NULL COMMENT '收货电话',
    receiver_address VARCHAR(255)  NOT NULL COMMENT '收货地址',
    status           VARCHAR(16)   NOT NULL DEFAULT 'CREATED' COMMENT '状态：CREATED/FLAGGED/CONFIRMED/PROCESSED/DISMISSED/CLOSED/CANCELED',
    anomaly_flag     TINYINT       NOT NULL DEFAULT 0 COMMENT '是否被 AI 标注异常：1 是，0 否',
    risk_score       INT           NOT NULL DEFAULT 0 COMMENT '综合风险分 0-100',
    version          INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    remark           VARCHAR(255)  NULL     COMMENT '备注',
    deleted          TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除：1 已删除',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_status_created (status, created_at),
    KEY idx_order_customer (customer_id),
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES sys_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';

-- ------------------------------------------------------------
-- 4. AI 异常标注结果表（可解释：每条规则命中的理由都落库）
-- ------------------------------------------------------------
CREATE TABLE ai_anomaly_result (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id      BIGINT       NOT NULL COMMENT '订单ID',
    rule_code     VARCHAR(32)  NOT NULL COMMENT '规则编码：R1_AMOUNT 等',
    rule_name     VARCHAR(64)  NOT NULL COMMENT '规则名称',
    score         INT          NOT NULL COMMENT '该规则评分 0-100',
    level         VARCHAR(16)  NOT NULL COMMENT '风险等级：HIGH/MEDIUM/LOW',
    reason        VARCHAR(255) NOT NULL COMMENT '命中理由，用于解释和展示',
    model_version VARCHAR(32)  NOT NULL DEFAULT 'rule-engine-v1' COMMENT '检测引擎版本',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_anomaly_order_rule (order_id, rule_code),
    KEY idx_anomaly_rule (rule_code),
    CONSTRAINT fk_anomaly_order FOREIGN KEY (order_id) REFERENCES biz_order (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = 'AI异常标注结果表';

-- ------------------------------------------------------------
-- 5. 人工核实记录表（一瓶订单只核实一次，唯一索引兜底）
-- ------------------------------------------------------------
CREATE TABLE order_verify_record (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id   BIGINT       NOT NULL COMMENT '订单ID',
    auditor_id BIGINT       NOT NULL COMMENT '核实人（审核员）ID',
    result     VARCHAR(16)  NOT NULL COMMENT '核实结论：CONFIRMED 确认异常 / FALSE_POSITIVE 误报',
    remark     VARCHAR(255) NULL     COMMENT '核实备注',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '核实时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_verify_order (order_id),
    KEY idx_verify_auditor (auditor_id),
    CONSTRAINT fk_verify_order FOREIGN KEY (order_id) REFERENCES biz_order (id),
    CONSTRAINT fk_verify_auditor FOREIGN KEY (auditor_id) REFERENCES sys_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '人工核实记录表';

-- ------------------------------------------------------------
-- 6. 处理记录表（退款 / 拦截 / 放行，可多次处理）
-- ------------------------------------------------------------
CREATE TABLE order_handle_record (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id   BIGINT       NOT NULL COMMENT '订单ID',
    handler_id BIGINT       NOT NULL COMMENT '处理人ID',
    action     VARCHAR(16)  NOT NULL COMMENT '处理动作：REFUND/BLOCK/RELEASE',
    remark     VARCHAR(255) NULL     COMMENT '处理备注',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '处理时间',
    PRIMARY KEY (id),
    KEY idx_handle_order (order_id),
    KEY idx_handle_handler (handler_id),
    CONSTRAINT fk_handle_order FOREIGN KEY (order_id) REFERENCES biz_order (id),
    CONSTRAINT fk_handle_handler FOREIGN KEY (handler_id) REFERENCES sys_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '处理记录表';

-- ------------------------------------------------------------
-- 7. 状态变更日志表（每次流转都记录处理人、时间、备注）
-- ------------------------------------------------------------
CREATE TABLE order_status_log (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id      BIGINT       NOT NULL COMMENT '订单ID',
    from_status   VARCHAR(16)  NULL     COMMENT '原状态，NULL 表示创建',
    to_status     VARCHAR(16)  NOT NULL COMMENT '新状态',
    operator_id   BIGINT       NULL     COMMENT '操作人ID，NULL 表示系统自动',
    operator_role VARCHAR(16)  NULL     COMMENT '操作人角色：ADMIN/AUDITOR/CUSTOMER/SYSTEM',
    remark        VARCHAR(255) NULL     COMMENT '备注',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
    PRIMARY KEY (id),
    KEY idx_status_log_order (order_id),
    KEY idx_status_log_time (created_at),
    CONSTRAINT fk_log_order FOREIGN KEY (order_id) REFERENCES biz_order (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单状态变更日志表';

-- ------------------------------------------------------------
-- 8. 系统操作日志表（关键接口调用记录，加分项）
-- ------------------------------------------------------------
CREATE TABLE sys_operation_log (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id    BIGINT       NULL     COMMENT '操作人ID，未登录为 NULL',
    module     VARCHAR(32)  NOT NULL COMMENT '模块：AUTH/ORDER/VERIFY/HANDLE/STATS',
    action     VARCHAR(64)  NOT NULL COMMENT '操作描述',
    method     VARCHAR(10)  NOT NULL COMMENT 'HTTP 方法',
    uri        VARCHAR(255) NOT NULL COMMENT '请求路径',
    ip         VARCHAR(64)  NULL     COMMENT '客户端 IP',
    cost_ms    INT          NOT NULL DEFAULT 0 COMMENT '耗时（毫秒）',
    success    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否成功：1/0',
    error_msg  VARCHAR(500) NULL     COMMENT '失败原因（脱敏后）',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    PRIMARY KEY (id),
    KEY idx_op_user (user_id),
    KEY idx_op_time (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统操作日志表';
