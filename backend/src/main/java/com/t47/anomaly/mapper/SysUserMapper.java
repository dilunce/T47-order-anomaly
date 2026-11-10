package com.t47.anomaly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t47.anomaly.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper。
 *
 * <p>继承 BaseMapper 后自动拥有单表增删改查能力，
 * 复杂多表 SQL 后续写在 XML 或注解里。</p>
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
