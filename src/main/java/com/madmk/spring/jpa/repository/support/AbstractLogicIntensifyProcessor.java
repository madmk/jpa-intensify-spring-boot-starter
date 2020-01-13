package com.madmk.spring.jpa.repository.support;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author madmk
 * @date 2019/12/22 15:19
 * @description: 实体扩展信息
 * T 实体类型
 */
public abstract class AbstractLogicIntensifyProcessor<T> {

    private List<Field> fields;

    private Class<T> doMain;

    private EntityManager em;

    /**
     * 字段缓存
     */
    private volatile static Map<Class,List<Field>> classCache=new HashMap<>(16);

    public AbstractLogicIntensifyProcessor(Class<T> doMain,EntityManager em) {
        if(classCache.get(doMain)==null){
            synchronized (AbstractLogicIntensifyProcessor.class){
                if(classCache.get(doMain)==null){
                    classCache.put(doMain,allField(doMain));
                }
            }
        }

        this.doMain=doMain;
        this.fields=classCache.get(doMain);
        this.em=em;
    }



    /**
     * 获取全部非静态字段
     * @param doMain
     * @return
     */
    private List<Field> allField(Class doMain){
        List<Field> fields=new ArrayList<>();
        for(Field field:doMain.getDeclaredFields()){
            if(!Modifier.isStatic(field.getModifiers())){
                fields.add(field);
            }
        }
        Class supClass=doMain.getSuperclass();
        if(supClass!=null){
            fields.addAll(allField(supClass));
        }
        return fields;
    }

    public EntityManager getEm() {
        return em;
    }

    public Class<T> getDoMain() {
        return doMain;
    }

    /**
     * 获取全部被某注释 注释的字段
     * @param annotationClass
     * @return
     */
    protected <A extends Annotation> List<AnnotationField<A>> findAnnotationFields(Class<A> annotationClass){
        if(fields==null){
            return Collections.emptyList();
        }
        List<AnnotationField<A>> annotationFields=new ArrayList<>(8);
        for(Field field:fields){
            A annotation=field.getAnnotation(annotationClass);
            if(annotation!=null){
                annotationFields.add(new AnnotationField<>(field,annotation));
            }
        }
        return annotationFields;
    }

    /**
     * 被某注释 注释的字段 和注释本身
     */
    class AnnotationField<A extends Annotation>{
        private Field field;
        private A annotation;

        public AnnotationField(Field field, A annotation) {
            this.field = field;
            this.annotation = annotation;
        }

        public Field getField() {
            return field;
        }

        public A getAnnotation() {
            return annotation;
        }
    }


    /**
     * 实体是否支持此扩展
     * @return
     */
    public abstract boolean isSustain();

    /**
     * 新增时增强
     * @param entity
     * @param <S>
     * @return
     */
    public <S extends T> S persistEnhance(S entity){return entity;};

    /**
     * 修改时增强
     * @param entity
     * @param <S>
     * @return
     */
    public <S extends T> S mergeEnhance(S entity){return entity;};

    /**
     * 实体删除增强
     * @param spec
     * @param logicContents
     * @return
     */
    public OperationSuggest<T,Integer> delEnhance(Specification<T> spec, LogicContent... logicContents){return null;};

    /**
     * 实体查询增强
     * @param spec
     * @param logicContents
     * @return
     */
    public OperationSuggest<T,Integer> queryEnhance(Specification<T> spec, LogicContent... logicContents){return null;};



}
