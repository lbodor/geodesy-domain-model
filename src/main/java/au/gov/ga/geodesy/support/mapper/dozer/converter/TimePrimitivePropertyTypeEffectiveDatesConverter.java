package au.gov.ga.geodesy.support.mapper.dozer.converter;

import java.text.ParseException;

import org.dozer.CustomConverter;
import org.dozer.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.ga.geodesy.igssitelog.domain.model.EffectiveDates;
import au.gov.ga.geodesy.support.utils.GMLDateUtils;
import net.opengis.gml.v_3_2_1.TimePeriodType;
import net.opengis.gml.v_3_2_1.TimePrimitivePropertyType;

/**
 * Convert: au.gov.ga.geodesy.igssitelog.domain.model.EffectiveDates <--> net.opengis.gml.v_3_2_1.TimePrimitivePropertyType
 */
public class TimePrimitivePropertyTypeEffectiveDatesConverter implements CustomConverter {
    Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("rawtypes")
    public Object convert(Object destination, Object source, Class destClass, Class sourceClass) {
        if (source == null) {
            return null;
        }

        if (source instanceof EffectiveDates) {
            return buildTimePrimitivePropertyType(destination, source);
        } else if (source instanceof TimePrimitivePropertyType) {
            try {
                return buildEffectiveDate(source);
            } catch (ParseException e) {
                throw new MappingException("Error parsing TimePeriodType - Converter " + getClass().getName()
                        + "used incorrectly. Arguments passed in were:" + destination + " and " + source + ".");
            }
        } else {
            throw new MappingException("Converter " + getClass().getName()
                    + "used incorrectly. Arguments passed in were:" + destination + " and " + source);
        }
    }

    /**
     * Create a new EffectiveDates and populate with the begin and end from the TimePeriodType that the given TimePrimitivePropertyType is
     * expected to have as a payload. A runtime ClassCastException will occur if it has no such payload.
     * 
     * @param timePrimitivePropertyTypeArg
     *            to extract Begin and optional End from
     * @return igsSiteLog EffectiveDates
     * @throws ParseException
     */
    private EffectiveDates buildEffectiveDate(Object timePrimitivePropertyTypeArg) throws ParseException {
        EffectiveDates ed = new EffectiveDates();
        if (timePrimitivePropertyTypeArg == null
                || !(timePrimitivePropertyTypeArg instanceof TimePrimitivePropertyType)) {
            return ed;
        }

        TimePrimitivePropertyType timePrimitivePropertyType = (TimePrimitivePropertyType) timePrimitivePropertyTypeArg;

        TimePeriodType timePeriodType = TimePrimitivePropertyTypeUtils.getTheTimePeriodType(timePrimitivePropertyType);

        ed.setFrom(GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC.parse(timePeriodType.getBeginPosition().getValue().get(0)));
        if (timePeriodType.getEndPosition() == null) {
            // To date wasn't included in data
            ed.setTo(null);
        } else {
            ed.setTo(GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC.parse(timePeriodType.getEndPosition().getValue().get(0)));
        }
        return ed;
    }

    /**
     * Build a TimePrimitivePropertyType using the existing such object if it isn't null, and populating a TimePeriodType (begin, end)
     * payload from given source which is first confirmed to be a au.gov.ga.geodesy.igssitelog.domain.model.EffectiveDates;
     * 
     * @param destination
     * @param source
     *            is object as returned by Dozer's CustomConverter.
     * @return newly created TimePrimitivePropertyType.
     */
    private Object buildTimePrimitivePropertyType(Object destination, Object source) {
        if (!(source instanceof EffectiveDates)) {
            return null;
        }
        // new TimePrimitivePropertyType or use existing one if exists
        TimePrimitivePropertyType dest = TimePrimitivePropertyTypeUtils.addTimePeriodType(
                TimePrimitivePropertyTypeUtils.newOrUsingExistingTimePrimitivePropertyType(destination));

        // Populate begin and end from the EffectiveDates
        TimePrimitivePropertyTypeUtils.getTheTimePeriodType(dest).setBeginPosition(
                TimePrimitivePropertyTypeUtils.buildTimePositionType(((EffectiveDates) source).getFrom()));
        TimePrimitivePropertyTypeUtils.getTheTimePeriodType(dest)
                .setEndPosition(TimePrimitivePropertyTypeUtils.buildTimePositionType(((EffectiveDates) source).getTo()));
        return dest;
    }

}