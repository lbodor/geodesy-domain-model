package au.gov.ga.geodesy.support.mapper.dozer.converter;

import java.time.Instant;
import java.time.ZoneId;

import org.junit.Assert;
import org.junit.Test;

import au.gov.ga.geodesy.support.utils.GMLDateUtils;
import net.opengis.gml.v_3_2_1.TimePositionType;

public class TimePostitionTypeStringConverterTest {
    private Instant instantDate = Instant.now();

    TimePostitionTypeStringConverter conv = new TimePostitionTypeStringConverter();

    /**
     * source: String
     * dest: TimePositionType (TimeInstantType)
     */
    @Test
    public void test1() {
        String in = "2011-07-20"; // correct format
        TimePositionType out = null;
        String expected = "20 Jul 2011";
        out = (TimePositionType) conv.convert(out, in, TimePositionType.class, String.class);
        Assert.assertEquals(expected, out.getValue().get(0));
    }

    @Test
    public void test2() {
        String in = "2011-20-07"; // in-correct format
        TimePositionType out = null;
        String expected = "20 Jul 2011";
        out = (TimePositionType) conv.convert(out, in, TimePositionType.class, String.class);
        Assert.assertEquals(expected, out.getValue().get(0));
    }

    @Test
    public void test3() {
        String in = "2011-07-20T16:00Z";
        TimePositionType out = null;
        String expected = "20 Jul 2011 16:00 GMT";
        out = (TimePositionType) conv.convert(out, in, TimePositionType.class, String.class);
        Assert.assertEquals(expected, out.getValue().get(0));
    }

    /*
     * source: TimePositionType (TimeInstantType)
     * dest: String
     */
    @Test
    public void test2_1() {
        TimePositionType in = getGoedMLTimePrimitive();
        String out = null;
        String expected = GMLDateUtils.dateToString(instantDate, GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_OUTPUT);
        out = (String) conv.convert(out, in, TimePositionType.class, String.class);
        Assert.assertEquals(expected, out);
    }

    /* ****************************************** */
    private TimePositionType getGoedMLTimePrimitive() {
        TimePositionType tpt = new TimePositionType();
        tpt.getValue().add(GMLDateUtils.dateToString(instantDate, GMLDateUtils.GEODESYML_DATE_FORMAT_TIME_SEC));
        return tpt;
    }

}
