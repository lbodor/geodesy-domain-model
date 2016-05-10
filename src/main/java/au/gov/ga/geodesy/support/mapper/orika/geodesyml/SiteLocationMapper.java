package au.gov.ga.geodesy.support.mapper.orika.geodesyml;

import java.util.function.Function;

import au.gov.ga.geodesy.domain.model.sitelog.ApproximatePosition;
import au.gov.ga.geodesy.domain.model.sitelog.SiteLocation;
import au.gov.ga.geodesy.support.java.util.Isomorphism;
import au.gov.xml.icsm.geodesyml.v_0_3.SiteLocationType;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Reversible mapping between GeodesyML SiteLocationType DTO and
 * SiteLocation site log entity.
 */
public class SiteLocationMapper implements Isomorphism<SiteLocationType, SiteLocation> {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private MapperFacade mapper;

    public SiteLocationMapper() {
        mapperFactory.classMap(SiteLocationType.class, SiteLocation.class)
            .fieldMap("countryCodeISO", "country").add()
            .fieldMap("tectonicPlate", "tectonicPlate").converter("tectonicPlate").add()
            .fieldMap("approximatePositionITRF", "approximatePosition").add()
            .byDefault()
            .register();

        mapperFactory.classMap(SiteLocationType.ApproximatePositionITRF.class, ApproximatePosition.class)
            .field("XCoordinateInMeters", "itrfX")
            .field("YCoordinateInMeters", "itrfY")
            .field("ZCoordinateInMeters", "itrfZ")
            .field("elevationMEllips", "elevationGrs80")
            .byDefault()
            .register();

        ConverterFactory converters = mapperFactory.getConverterFactory();
        converters.registerConverter("tectonicPlate", new StringToCodeTypeConverter("eGeodesy/tectonicPlate"));
        mapper = mapperFactory.getMapperFacade();
    }

    /**
     * {@inheritDoc}
     */
    public Function<SiteLocationType, SiteLocation> to() {
        return siteLocationType -> mapper.map(siteLocationType, SiteLocation.class);
    }

    /**
     * {@inheritDoc}
     */
    public Function<SiteLocation, SiteLocationType> from() {
        return siteLocation -> mapper.map(siteLocation, SiteLocationType.class);
    }
}
