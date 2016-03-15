package au.gov.ga.geodesy.support.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.interfaces.geodesyml.MarshallingException;
import au.gov.ga.geodesy.support.marshalling.moxy.GeodesyMLMoxy;

@Configuration
@EnableSpringConfigured
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"au.gov.ga.geodesy.support.moxy"})
public class GeodesySupportConfig {
    
    @Bean
    public GeodesyMLMarshaller getGeodesyMLMoxy() throws MarshallingException {
        return new GeodesyMLMoxy();
    }
}
