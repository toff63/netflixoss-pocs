package com.github.diegopacheco.sandbox.java.netflixoss.karyon.poc.jersey;

import com.github.diegopacheco.sandbox.java.netflixoss.karyon.poc.common.*;
import netflix.adminresources.resources.KaryonWebAdminModule;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.ShutdownModule;
import netflix.karyon.archaius.ArchaiusBootstrap;
import netflix.karyon.eureka.KaryonEurekaModule;
import netflix.karyon.jersey.blocking.KaryonJerseyModule;
import netflix.karyon.servo.KaryonServoModule;

import com.github.diegopacheco.sandbox.java.netflixoss.karyon.poc.jersey.KaryonJerseyServerApp.KaryonJerseyModuleImpl;
import com.netflix.governator.annotations.Modules;

@ArchaiusBootstrap
@KaryonBootstrap(name = "simplemath-netflix-oss", healthcheck = HealthcheckResource.class)
@Modules(include = {
        ShutdownModule.class,
        KaryonWebAdminModule.class,
        KaryonServoModule.class,
        KaryonJerseyModuleImpl.class
        ,KaryonEurekaModule.class 
})
public interface KaryonJerseyServerApp {
	 class KaryonJerseyModuleImpl extends KaryonJerseyModule {
	        @Override
	        protected void configureServer() {
	            bind(AuthenticationService.class).to(AuthenticationServiceImpl.class);
	            interceptorSupport().forUri("/*").intercept(LoggingInterceptor.class);
				interceptorSupport().forUri("/*").interceptOut(HeadersOutInterceptor.class);
	            interceptorSupport().forUri("/math").interceptIn(AuthInterceptor.class);
	            server().port(8888).threadPoolSize(100);
	        }
	 }
}
