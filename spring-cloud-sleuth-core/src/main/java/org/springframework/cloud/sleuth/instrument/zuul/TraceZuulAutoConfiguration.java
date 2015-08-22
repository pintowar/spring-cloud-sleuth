/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.sleuth.instrument.zuul;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.zuul.ZuulFilter;

/**
 * Registers beans that add tracing to requests
 *
 * @author Dave Syer
 */
@Configuration
@ConditionalOnProperty(value = "spring.sleuth.zuul.enabled", matchIfMissing = true)
@ConditionalOnWebApplication
@ConditionalOnClass(ZuulFilter.class)
@ConditionalOnBean(Trace.class)
@AutoConfigureAfter(TraceAutoConfiguration.class)
public class TraceZuulAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public TracePreZuulFilter tracePreZuulFilter() {
		return new TracePreZuulFilter();
	}

	@Bean
	public TraceRestClientRibbonCommandFactory traceRestClientRibbonCommandFactory(SpringClientFactory factory) {
		return new TraceRestClientRibbonCommandFactory(factory);
	}

	@Bean
	@ConditionalOnMissingBean
	public TracePostZuulFilter tracePostZuulFilter() {
		return new TracePostZuulFilter();
	}

}
