package com.madmk.spring.jpa.repository.support;

import com.madmk.spring.jpa.annotation.DelSign;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author madmk
 * @date 2019/12/16 10:44
 * @description: 逻辑删除扩展器
 */
public class DelLogicIntensifyProcessor<T> extends AbstractLogicIntensifyProcessor<T> {

    /**
     * 逻辑删除字段名称
     */
    private String name;

    /**
     * 逻辑删除字段类型
     */
    private Class type;

    /**
     * 删除标记注解
     */
    private DelSign delSign;

    /**
     * 存在时的 字段值
     */
    private Object exist;

    /**
     * 删除时的字段值
     */
    private Object disappear;

    /**
     * 逻辑删除字段控制器
     */
    private PropertyDescriptor propertyDescriptor;


    private Specification<T> whereEnhance;

    /**
     * 是否支持逻辑删除扩展
     */
    private boolean sustain=false;

    public DelLogicIntensifyProcessor(Class<T> doMain, EntityManager em) {
        super(doMain,em);
        List<AnnotationField<DelSign>> delFields=findAnnotationFields(DelSign.class);
        if(CollectionUtils.isEmpty(delFields)){
            return;
        }
        if(delFields.size()!=1){
            throw new IllegalArgumentException("同一个类中只允许存在一个自定义删除标记");
        }
        this.sustain=true;
        AnnotationField<DelSign> field=delFields.get(0);
        this.name=field.getField().getName();
        this.type=field.getField().getType();
        this.delSign=field.getAnnotation();
        init();
        whereEnhance=(root,query,builder)->builder.equal(root.get(name),exist);
        try {
            propertyDescriptor=new PropertyDescriptor(name,doMain);
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 删除参数初始化
     */
    private void init(){
        if(this.type==int.class
                ||this.type==Integer.class){
            exist=1;
            disappear=0;
            return;
        }
        if(this.type==byte.class
                ||this.type==Byte.class){
            exist=(byte)1;
            disappear=(byte)0;
            return;
        }
        if(this.type==short.class
                ||this.type==Short.class){
            exist=(short)1;
            disappear=(short)0;
            return;
        }
        if(this.type==long.class
                ||this.type==Long.class){
            exist=(long)1;
            disappear=(long)0;
            return;
        }
        if(this.type==char.class
                ||this.type==Character.class){
            exist='1';
            disappear='0';
            return;
        }
        throw new IllegalArgumentException("DelSign.class 只支持 int，byte，short，long 及其包装类");
    }

    @Override
    public boolean isSustain() {
        return sustain;
    }

    @Override
    public <S extends T> S persistEnhance(S entity) {
        try {
            propertyDescriptor.getWriteMethod().invoke(entity,exist);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return entity;
    }


    @Override
    public <S extends T> S mergeEnhance(S entity) {
        return persistEnhance(entity);
    }

    @Override
    public OperationSuggest<T,Integer> delEnhance(Specification<T> spec, LogicContent... logicContents) {
        spec=spec.and(whereEnhance);
        CriteriaBuilder builder=getEm().getCriteriaBuilder();
        CriteriaUpdate<T> criteriaUpdate=builder.createCriteriaUpdate(getDoMain());
        Root<T> root=criteriaUpdate.from(getDoMain());
        criteriaUpdate.set(root.get(name),disappear);
        criteriaUpdate.where(spec.toPredicate(root,null,builder));
        return new OperationSuggest(false,spec,getEm().createQuery(criteriaUpdate).executeUpdate());
    }

    @Override
    public OperationSuggest queryEnhance(Specification<T> spec, LogicContent... logicContents) {
        if(spec==null){
            spec=whereEnhance;
        }else {
            spec= spec.and(whereEnhance);
        }
        return new OperationSuggest(spec);
    }

}
