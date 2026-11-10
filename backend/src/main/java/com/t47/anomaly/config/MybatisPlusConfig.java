package com.t47.anomaly.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置。
 *
 * <ul>
 *   <li>分页插件：让 {@code selectPage} 自动生成 {@code LIMIT} 和 {@code COUNT}；</li>
 *   <li>乐观锁插件：配合实体上的 {@code @Version}，UPDATE 自动带 version 条件，
 *       并发修改时后到者影响行数为 0，用于处理"同一订单被两人同时审批"。</li>
 * </ul>
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 分页：必须指定数据库类型，否则 COUNT 语句可能生成错误
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大 100 条，防止前端传 size=100000 拖垮数据库
        pagination.setMaxLimit(100L);
        interceptor.addInnerInterceptor(pagination);

        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
