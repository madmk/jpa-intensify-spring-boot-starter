package com.madmk.spring.jpa.repository;

import com.madmk.spring.jpa.repository.support.LogicContent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author Madmk
 * @date 19-5-5 下午3:21
 * @description
 */
@NoRepositoryBean
@ConditionalOnClass(JpaRepositoryImplementation.class)
public interface LogicRepository<T,ID> extends JpaRepositoryImplementation<T, ID> {


	/**
	 * 逻辑删除
	 */
	void logicDelete(ID id, LogicContent... logicContents);

	/**
	 * 逻辑删除
	 */
	int logicDeleteAll(@Nullable Specification<T> spec, LogicContent... logicContents);

	/**
	 * 逻辑查找
	 *
	 * @param id
	 */
	Optional<T> logicById(ID id, LogicContent... logicContents);

	/**
	 * 逻辑查找
	 *
	 * @param spec
	 */
	Optional<T> logicOne(@Nullable Specification<T> spec, LogicContent... logicContents);

	/**
	 * 查询全部
	 * @return
	 */
	List<T> logicAll(LogicContent... logicContents);

	/**
	 * 查询全部
	 * @param spec
	 * @return
	 */
	List<T> logicAll(@Nullable Specification<T> spec, LogicContent... logicContents);

	/**
	 * 查询总数
	 * @param spec
	 * @return
	 */
	long logicCount(@Nullable Specification<T> spec, LogicContent... logicContents);

	/**
	 * 分页逻辑查询
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<T> logicAll(@Nullable Specification<T> spec, Pageable pageable, LogicContent... logicContents);

	/**
	 * 分页逻辑查询
	 * @param pageable
	 * @return
	 */
	Page<T> logicAll(Pageable pageable, LogicContent... logicContents);
}
