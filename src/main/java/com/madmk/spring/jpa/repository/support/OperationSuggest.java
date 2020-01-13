package com.madmk.spring.jpa.repository.support;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author madmk
 * @date 2019/12/22 13:42
 * @description: 操作建议
 */
public class OperationSuggest<T,R> {

    /**
     * 是否继续执行 如果增强器中其中一个返回 false 则 剩余增强器不会执行
     */
    private boolean goOn=true;

    /**
     * 被替换的where
     */
    private Specification<T> where;

    /**
     * 如果不继续执行则替换返回的结果
     */
    private R r;

    public OperationSuggest(boolean goOn, Specification<T> where, R r) {
        this.goOn = goOn;
        this.where = where;
        this.r = r;
    }

    public OperationSuggest(Specification<T> where) {
        this.where = where;
    }

    public boolean isGoOn() {
        return goOn;
    }

    public Specification<T> getWhere() {
        return where;
    }

    public R getR() {
        return r;
    }
}
