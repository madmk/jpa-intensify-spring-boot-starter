package com.madmk.spring.jpa.repository.support;

import com.madmk.spring.jpa.repository.LogicIntensifyProcessorFiltrate;
import com.madmk.spring.jpa.repository.LogicRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Madmk
 * @date 19-5-5 下午3:22
 * @description
 */
@Transactional(readOnly = true,rollbackFor = Exception.class)
@ConditionalOnClass(SimpleJpaRepository.class)
public class LogicRepositoryContent<T,ID> extends SimpleJpaRepository<T,ID> implements LogicRepository<T,ID> {

	private final JpaEntityInformation<T, ID> entityInformation;
	private final EntityManager em;
	private final PersistenceProvider provider;

	private List<? extends AbstractLogicIntensifyProcessor> logicIntensifyProcessors;


	public LogicRepositoryContent(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager,LogicIntensifyProcessorFiltrate logicIntensifyProcessorFactory) {
		super(entityInformation, entityManager);
		this.entityInformation = entityInformation;
		this.em = entityManager;
		this.provider = PersistenceProvider.fromEntityManager(entityManager);
		this.logicIntensifyProcessors=logicIntensifyProcessorFactory.yieldAll(entityInformation.getJavaType());

	}

//	public LogicRepositoryContent(Class<T> domainClass, EntityManager em) {
//		this((JpaEntityInformation<T, ID>)JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
//	}

	/**
	 * 开启查询增强
	 * @param spec
	 * @param logicContents
	 * @return
	 */
	public OperationSuggest<T,Object> queryWhereEnhance(Specification<T> spec, LogicContent... logicContents){
		for(AbstractLogicIntensifyProcessor abstractLogicIntensifyProcessor:logicIntensifyProcessors){
			OperationSuggest<T,Object> suggest=abstractLogicIntensifyProcessor.queryEnhance(spec,logicContents);
			if(!suggest.isGoOn()){
				return suggest;
			}
			spec=suggest.getWhere();
		}
		return new OperationSuggest(spec);
	}

	@Override
	public <S extends T> S save(S entity) {
		if (entityInformation.isNew(entity)) {
			//实体增强
			for(AbstractLogicIntensifyProcessor abstractLogicIntensifyProcessor:logicIntensifyProcessors){
				entity=(S)abstractLogicIntensifyProcessor.persistEnhance(entity);
			}
			em.persist(entity);
			return entity;
		} else {
			//实体增强
			for(AbstractLogicIntensifyProcessor abstractLogicIntensifyProcessor:logicIntensifyProcessors){
				entity=(S)abstractLogicIntensifyProcessor.mergeEnhance(entity);
			}
			return em.merge(entity);
		}
	}

	@Override
	public void logicDelete(ID id, LogicContent... logicContents) {
		Specification<T> delSpec=(root,query,cb)->cb.equal(root.get(entityInformation.getIdAttribute()),id);
		this.logicDeleteAll(delSpec,logicContents);
	}

	@Override
	public int logicDeleteAll(Specification<T> spec, LogicContent... logicContents) {
		//为防止意外全删，故必须给出显性的删除条件
		if(spec==null){
			return 0;
		}
		//开启删除实体增强
		for(AbstractLogicIntensifyProcessor abstractLogicIntensifyProcessor:logicIntensifyProcessors){
			OperationSuggest<T,Integer> suggest=abstractLogicIntensifyProcessor.delEnhance(spec,logicContents);
			if(!suggest.isGoOn()){
				return suggest.getR();
			}
			spec=suggest.getWhere();
		}
		CriteriaBuilder builder=em.getCriteriaBuilder();
		CriteriaDelete<T> criteriaDelete=builder.createCriteriaDelete(entityInformation.getJavaType());
		Root<T> root=criteriaDelete.from(entityInformation.getJavaType());
		criteriaDelete.where(spec.toPredicate(root,null,builder));
		return em.createQuery(criteriaDelete).executeUpdate();
	}

	@Override
	public Optional<T> logicById(ID id, LogicContent... logicContents) {
		return logicOne((root,query,cb)->cb.equal(root.get(entityInformation.getIdAttribute()),id));
	}

	@Override
	public Optional<T> logicOne(Specification<T> spec, LogicContent... logicContents) {
		OperationSuggest<T,Object> suggest=queryWhereEnhance(spec,logicContents);
		if(!suggest.isGoOn()){
			return (Optional)suggest.getR();
		}
		return super.findOne(suggest.getWhere());
	}

	/**
	 * 查询全部
	 * @return
	 */
	@Override
	public List<T> logicAll(LogicContent... logicContents){
		OperationSuggest<T,Object> suggest=queryWhereEnhance(null,logicContents);
		if(!suggest.isGoOn()){
			return (List)suggest.getR();
		}
		return super.findAll(suggest.getWhere());
	}

	@Override
	public List<T> logicAll(@Nullable Specification<T> spec, LogicContent... logicContents) {
		OperationSuggest<T,Object> suggest=queryWhereEnhance(spec,logicContents);
		if(!suggest.isGoOn()){
			return (List)suggest.getR();
		}
		return super.findAll(suggest.getWhere());
	}

	@Override
	public long logicCount(Specification<T> spec, LogicContent... logicContents) {
		OperationSuggest<T,Object> suggest=queryWhereEnhance(spec,logicContents);
		if(!suggest.isGoOn()){
			return (Long) suggest.getR();
		}
		return super.count(suggest.getWhere());
	}

	@Override
	public Page<T> logicAll(@Nullable Specification<T> spec, Pageable pageable, LogicContent... logicContents){
		OperationSuggest<T,Object> suggest=queryWhereEnhance(spec,logicContents);
		if(!suggest.isGoOn()){
			return (Page) suggest.getR();
		}
		return super.findAll(suggest.getWhere(),pageable);
	}

	@Override
	public Page<T> logicAll(Pageable pageable, LogicContent... logicContents){
		OperationSuggest<T,Object> suggest=queryWhereEnhance(null,logicContents);
		if(!suggest.isGoOn()){
			return (Page) suggest.getR();
		}
		return super.findAll(suggest.getWhere(),pageable);
	}

}
