package com.madmk.spring.jpa.configuration;

import com.madmk.spring.jpa.repository.LogicRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Locale;

/**
 * @author madmk
 * @date 2019/12/22 10:24
 * @description:
 */
class JpaRepositoryConfigurationSourceSupport extends AbstractRepositoryConfigurationSourceSupport {
	private BootstrapMode bootstrapMode = null;

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableJpaRepositories.class;
	}

	@Override
	protected Class<?> getConfiguration() {
		return EnableJpaRepositoriesConfiguration.class;
	}

	@Override
	protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
		return new JpaRepositoryConfigExtension(){
			@Override
			public String getRepositoryFactoryBeanClassName() {
				return LogicRepositoryFactoryBean.class.getName();
			}
		};
	}

	@Override
	protected BootstrapMode getBootstrapMode() {
		return (this.bootstrapMode == null) ? super.getBootstrapMode()
				: this.bootstrapMode;
	}

	@Override
	public void setEnvironment(Environment environment) {
		super.setEnvironment(environment);
		configureBootstrapMode(environment);
	}

	private void configureBootstrapMode(Environment environment) {
		String property = environment
				.getProperty("spring.data.jpa.repositories.bootstrap-mode");
		if (StringUtils.hasText(property)) {
			this.bootstrapMode = BootstrapMode
					.valueOf(property.toUpperCase(Locale.ENGLISH));
		}
	}

	@EnableJpaRepositories(repositoryFactoryBeanClass= LogicRepositoryFactoryBean.class)
	private static class EnableJpaRepositoriesConfiguration {

	}
}
