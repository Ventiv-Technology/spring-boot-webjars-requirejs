/**
 * Copyright (c) 2015 Ventiv Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ventiv.webjars.requirejs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.ventiv.webjars.requirejs.servlet.RequireJsConfigServlet;

import javax.annotation.Resource;

/**
 * @author John Crygier
 */
@Configuration
public class WebJarsRequireJsConfig {

    @Value("${webjars.requirejs.config.endpoint:/scripts/config.js}")
    private String endpoint;

    @Resource private Environment env;

    @Bean
    public ServletRegistrationBean getRequireJsServlet() {
        return new ServletRegistrationBean(new RequireJsConfigServlet(env), endpoint);
    }

}
