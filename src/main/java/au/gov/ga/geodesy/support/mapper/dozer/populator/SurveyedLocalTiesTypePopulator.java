package au.gov.ga.geodesy.support.mapper.dozer.populator;

import au.gov.ga.geodesy.support.utils.GMLMiscTools;
import au.gov.xml.icsm.geodesyml.v_0_3.SurveyedLocalTiesType;

/**
 * The receivers have required elements that don't all exist in the SOPAC Sitelog xml. This fills them in.
 * Note that it isn't possible to do every element as they will have complex element hierarchies.
 * 
 * @author brookes
 *
 */
public class SurveyedLocalTiesTypePopulator
        extends GeodesyMLElementPopulator<SurveyedLocalTiesType> {
    /**
     * Consider all required elements for this type and add any missing ones with default values.
     * 
     * @param gnssReceiverType
     */
    void checkAllRequiredElementsPopulated(SurveyedLocalTiesType surveyedLocalTiesType) {
        checkElementPopulated(surveyedLocalTiesType, "tiedMarkerUsage", GMLMiscTools.getEmptyString());
        checkElementPopulated(surveyedLocalTiesType, "tiedMarkerCDPNumber", GMLMiscTools.getEmptyString());
        checkElementPopulated(surveyedLocalTiesType, "tiedMarkerDOMESNumber", GMLMiscTools.getEmptyString());
        checkElementPopulated(surveyedLocalTiesType, "surveyMethod", GMLMiscTools.getEmptyString());
    }
}