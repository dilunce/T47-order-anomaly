package com.t47.anomaly.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果，配合 MyBatis-Plus 的 IPage 使用。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 当前页码（从 1 开始） */
    private long current;

    /** 每页条数 */
    private long size;

    /** 当前页数据 */
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getTotal(), page.getPages(),
                page.getCurrent(), page.getSize(), page.getRecords());
    }
}
