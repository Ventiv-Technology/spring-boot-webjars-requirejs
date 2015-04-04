Spring Boot WebJars RequireJs
=============================

This is a small utility project that builds on top of WebJars Locator to properly generate the RequireJs Configuration.  It adds some goodies to automatically kick off an application entry point after the configuration is loaded, as well as hooks to automatically register an endpoint in Spring Boot.

Usage
-----

If using within Spring Boot, all you need to do is add `@EnableWebJarsRequireJs` to your main Application Java file.  This will automatically deploy a Servlet at a configurable location that will generate the RequireJs Configuration file.

Within your html, you should only need to do the following:

	<script data-main="scripts/config" data-base-url="/app/js" data-app="app.js" src="/webjars/requirejs/2.1.14-3/require.min.js"></script>

This is 'almost' standard syntax for RequireJs, with the addition of a new feature, data-app.  If this is found, the code will automatically load this file AFTER the configuration is properly loaded.  This way, you don't have to worry about a race condition described in [http://requirejs.org/docs/api.html#data-main](http://requirejs.org/docs/api.html#data-main "http://requirejs.org/docs/api.html#data-main").

Also, there is the addition of data-base-url in the script tag.  If this is present, it will configure RequireJs to use a baseUrl that is provided, saving you from entering explicit paths in your code.  If you do not provide it, RequireJs will default to the path in data-main (scripts in the case above).

Properties
----------

To configure the module, the following properties are available.  They may be set by standard Spring Boot methods (e.g. application.properties) or if configuring as a plain servlet, configure via the servlet configuration properties.

- `webjars.requirejs.config.endpoint`
	- Description: If using Spring Boot, this is where the endpoint will sit.  Drop off the initial / and trailing .js to use in data-main for RequireJs.
	- Default: `/scripts/config.js`
- `webjars.requirejs.config.rootUrl`
	- Description: Where the WebJars are mapped to in your environment
	- Default: `/webjars/`
- `webjars.requirejs.config.prettyPrint`
	- Description: Should the Configuration structure be pretty printed?
	- Default: `true`
- `webjars.requirejs.paths.<requireJsModule>`
	- Description: Add / Override the path that is defined in the webjar's POM requirejs section
	- Default: None
- `webjars.requirejs.newModules`
	- Description: New RequireJS moduels that are to be added to the requirejs configuration.  The actual location should be `webjars.requirejs.paths.<requireJsModule>`
	  with it's fully qualified URL path as it should appear in the configuration.  Use a comma separated list to add multiple.
	- Default: None
- `webjars.requirejs.dependencies.<requireJsModule>`
	- Description: Add new dependencies to the `<requireJsModule>`.  If the existing module does NOT have a shim value, you MUST add it to `webj
	- Default: None

Dependenciesars.requirejs.newModules`
------------

The intention was to keep dependencies to a minimum, so just the following are required at runtime (as well as their transitive dependencies):

- Spring Boot
- Servlet Api
- WebJars Locator
