package com.madmk.spring.jpa.configuration;

import com.madmk.spring.jpa.repository.LogicIntensifyProcessorFiltrate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

/**
 * @author madmk
 * @date 2019/12/22 9:52
 * @description:
 */
@ConditionalOnClass({SimpleJpaRepository.class, EnableJpaRepositories.class})
@Import({JpaRepositoryConfigurationSourceSupport.class})
public class JpaEnhanceConfig {

    @Bean
    @ConditionalOnMissingBean(LogicIntensifyProcessorFiltrate.class)
    public LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate(EntityManager em){
        return new LogicIntensifyProcessorFiltrate(em);
    }

}
