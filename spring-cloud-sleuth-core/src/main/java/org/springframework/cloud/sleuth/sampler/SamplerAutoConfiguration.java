/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.sampler;

import brave.sampler.Sampler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@linkplain Configuration configuration} for {@link Sampler}.
 *
 * @author Marcin Grzejszczak
 * @since 2.1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SamplerProperties.class)
// This is not auto-configuration, but it was in the past. Leaving the name as
// SamplerAutoConfiguration because those not using Zipkin formerly had to
// import this directly. A less precise name is better than rev-locking code.
public class SamplerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	Sampler sleuthTraceSampler() {
		return Sampler.NEVER_SAMPLE;
	}

	static Sampler samplerFromProps(SamplerProperties config) {
		if (config.getProbability() != null) {
			return new ProbabilityBasedSampler(config);
		}
		return new RateLimitingSampler(config);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnBean(
			type = "org.springframework.cloud.context.scope.refresh.RefreshScope")
	protected static class RefreshScopedSamplerConfiguration {

		@Bean
		@RefreshScope
		@ConditionalOnMissingBean
		public Sampler defaultTraceSampler(SamplerProperties config) {
			return samplerFromProps(config);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(
			type = "org.springframework.cloud.context.scope.refresh.RefreshScope")
	protected static class NonRefreshScopeSamplerConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public Sampler defaultTraceSampler(SamplerProperties config) {
			return samplerFromProps(config);
		}

	}

}
