package com.madmk.spring.jpa.repository;

import com.madmk.spring.jpa.repository.support.AbstractLogicIntensifyProcessor;
import com.madmk.spring.jpa.repository.support.DelLogicIntensifyProcessor;
import com.madmk.spring.jpa.repository.support.AbstractLogicIntensifyProcessor;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author madmk
 * @date 2019/12/9 14:27
 * @description: 逻辑增强处理器 筛选
 */
public class LogicIntensifyProcessorFiltrate{

    /**
     * 全部可生成扩展
     */
    private List<Function<Class,? extends AbstractLogicIntensifyProcessor>> functions=new ArrayList<>();

    /**
     * 初始化全部已存在扩展
     */
    public LogicIntensifyProcessorFiltrate(EntityManager em) {
        //逻辑删除扩展
        functions.add(e->new DelLogicIntensifyProcessor(e,em));
    }

    /**
     * 新增扩展
     */
    public <T extends AbstractLogicIntensifyProcessor> boolean addLogicIntensifyProcessor(Function<Class,T> function){
        return functions.add(function);
    }

    /**
     * 获取全部增强器
     * @return
     */
    public <T> List<? extends AbstractLogicIntensifyProcessor> yieldAll(Class<T> doMain){
        if(functions==null){
            return Collections.emptyList();
        }
        return functions.stream()
                .map(f->f.apply(doMain))
                .filter(AbstractLogicIntensifyProcessor::isSustain)
                .collect(Collectors.toList());
    }
}
