package com.madmk.spring.jpa.repository;

import com.madmk.spring.jpa.repository.support.LogicRepositoryContent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author madmk
 * @date 2019/12/6 9:35
 * @description:
 */
public class LogicRepositoryFactoryBean<R extends LogicRepository<T,ID>, T,ID>
        extends JpaRepositoryFactoryBean<R, T, ID> implements ApplicationContextAware {

    private LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate;

    /**
     * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    public LogicRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new LogicRepositoryFactory(em,this.logicIntensifyProcessorFiltrate);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.logicIntensifyProcessorFiltrate= applicationContext.getBean(LogicIntensifyProcessorFiltrate.class);
    }

    private static class  LogicRepositoryFactory<T,ID>
            extends JpaRepositoryFactory {

        private final EntityManager em;
        private LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate;

        public LogicRepositoryFactory(EntityManager em,LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate) {
            super(em);
            this.em = em;
            this.logicIntensifyProcessorFiltrate=logicIntensifyProcessorFiltrate;
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
            Object repository = getTargetRepositoryViaReflection(information, entityInformation, entityManager,logicIntensifyProcessorFiltrate);
            Assert.isInstanceOf(JpaRepositoryImplementation.class, repository);
            return (JpaRepositoryImplementation<?, ?>) repository;
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return LogicRepositoryContent.class;
        }
    }
}
