package au.gov.ga.geodesy.support.mapper.dozer.populator;

import au.gov.ga.geodesy.support.mapper.dozer.converter.TimePrimitivePropertyTypeUtils;
import au.gov.ga.geodesy.support.utils.GMLDateUtils;
import au.gov.ga.geodesy.support.utils.GMLMiscTools;
import au.gov.xml.icsm.geodesyml.v_0_4.OtherInstrumentationType;

public class OtherInstrumentationTypePopulator extends GeodesyMLElementPopulator<OtherInstrumentationType> {
    /**
     * Consider all required elements for this type and add any missing ones with default values.
     * 
     * @param gnssReceiverType
     */
    @Override
    void checkAllRequiredElementsPopulated(OtherInstrumentationType otherInstrumentationType) {
        checkElementPopulated(otherInstrumentationType, "instrumentation", GMLMiscTools.getEmptyString());
        checkElementPopulated(otherInstrumentationType, "validTime",
                TimePrimitivePropertyTypeUtils.buildTimePrimitivePropertyType(GMLDateUtils.buildStartOfTime()));
    }
}
