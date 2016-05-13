package au.gov.ga.geodesy.support.spring;

import java.io.FileNotFoundException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import au.gov.ga.geodesy.domain.model.SynchronousEventPublisher;
import au.gov.ga.geodesy.domain.model.event.EventPublisher;
import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLValidator;

@Configuration
public class GeodesyServiceTestConfig extends GeodesyServiceConfig {

    @Bean
    @Override
    public EventPublisher eventPublisher() {
        return new SynchronousEventPublisher();
    }

    @Bean
    public GeodesyMLValidator getGeodesyMLValidator() throws FileNotFoundException {
        String catalog = ResourceUtils.getFile("classpath:xsd/geodesyml-1.0.0-SNAPSHOT/catalog.xml").getAbsolutePath();
        return new GeodesyMLValidator(catalog);
    }
}
