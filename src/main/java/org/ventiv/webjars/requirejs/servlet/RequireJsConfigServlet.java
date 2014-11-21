/**
 * Copyright (c) 2014 Ventiv Technology
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
package org.ventiv.webjars.requirejs.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.ventiv.webjars.requirejs.config.RequireJsConfigBuilder;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author John Crygier
 */
public class RequireJsConfigServlet implements Servlet {

    private String webJarRootUrl = "/webjars/";
    private boolean prettyPrint = true;
    private ObjectMapper mapper = new ObjectMapper();
    private RequireJsConfigBuilder requireJsConfigBuilder;

    /**
     * Used when created explicitly within a Spring Boot context
     *
     * @param env
     */
    public RequireJsConfigServlet(Environment env) {
        webJarRootUrl = env.getProperty("webjars.requirejs.config.rootUrl", String.class, webJarRootUrl);
        prettyPrint = env.getProperty("webjars.requirejs.config.prettyPrint", Boolean.class, prettyPrint);

        requireJsConfigBuilder = new RequireJsConfigBuilder(webJarRootUrl, env);
    }

    /**
     * This should only be used when using with web.xml.  Environment will come from init(ServletConfig)
     */
    public RequireJsConfigServlet() {    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        if (requireJsConfigBuilder == null) {       // Only initialize this if we haven't already (e.g. Not in Spring-Boot)
            webJarRootUrl = getWithDefault(config.getInitParameter("webjars.requirejs.config.rootUrl"), webJarRootUrl);
            prettyPrint = getWithDefault(config.getInitParameter("webjars.requirejs.config.prettyPrint"), prettyPrint);

            requireJsConfigBuilder = new RequireJsConfigBuilder(webJarRootUrl, null);
        }
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        if (res instanceof HttpServletResponse) {
            ((HttpServletResponse) res).setHeader("Content-Type", "application/javascript");
        }

        res.getOutputStream().print("require.config(");
        if (prettyPrint)
            res.getOutputStream().print(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requireJsConfigBuilder.buildConfig()));
        else
            res.getOutputStream().print(mapper.writeValueAsString(requireJsConfigBuilder.buildConfig()));
        res.getOutputStream().print(");\n\n");

        // Append our special loader
        res.getOutputStream().println("var scripts = document.getElementsByTagName('script');");
        res.getOutputStream().println("for (var i = 0; i < scripts.length; i++) {");
        res.getOutputStream().println("    var script = scripts[i];");
        res.getOutputStream().println("    var src = script.getAttribute('src');");
        res.getOutputStream().println("    var dataMain = script.getAttribute('data-main');");
        res.getOutputStream().println("    var dataApp = script.getAttribute('data-app');");
        res.getOutputStream().println("    var baseUrl = script.getAttribute('data-base-url');");
        res.getOutputStream().println("    if (baseUrl)");
        res.getOutputStream().println("        require.config({ baseUrl: baseUrl });\n");
        res.getOutputStream().println("    if (dataMain && dataApp && src.indexOf(\"require\") > -1) {");
        res.getOutputStream().println("        require([dataApp], function() {});");
        res.getOutputStream().println("    }");
        res.getOutputStream().println("}");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    private <T> T getWithDefault(String value, T def) {
        if (value == null)
            return def;
        else if (def.getClass().equals(Boolean.class))
            return (T) Boolean.valueOf(value);
        else if (def.getClass().equals(Long.class))
            return (T) Long.valueOf(value);
        else if (def.getClass().equals(Integer.class))
            return (T) Integer.valueOf(value);
        else
            return def;
    }
}