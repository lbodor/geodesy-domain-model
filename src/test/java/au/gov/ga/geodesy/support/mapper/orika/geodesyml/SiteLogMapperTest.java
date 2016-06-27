package au.gov.ga.geodesy.support.mapper.orika.geodesyml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.testng.annotations.Test;

import au.gov.ga.geodesy.domain.model.sitelog.*;
import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLUtils;
import au.gov.ga.geodesy.support.TestResources;
import au.gov.ga.geodesy.support.gml.GMLPropertyType;
import au.gov.ga.geodesy.support.marshalling.moxy.GeodesyMLMoxy;
import au.gov.xml.icsm.geodesyml.v_0_3.*;
import ma.glasnost.orika.metadata.TypeFactory;
import net.opengis.gml.v_3_2_1.TimePositionType;

public class SiteLogMapperTest {

    private SiteLogMapper mapper = new SiteLogMapper();
    private GeodesyMLMarshaller marshaller = new GeodesyMLMoxy();

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType.
     **/
    @Test
    public void testMapping() throws Exception {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.geodesyMLTestDataSiteLogReader("MOBS-everything"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        testMappingValues(siteLogType, siteLog);
        System.out.println("MoreInformation after: "+siteLog.getMoreInformation());
        System.out.println();

        // Assert if some fields are being set
        assertThat(siteLog.getMoreInformation().getDoi(), equalTo("DOI-1234"));

        // TODO: test the from mapping when it is implemented
        // SiteLogType mappedSiteLogType = mapper.from(siteLog);
        // testMappingValues(mappedSiteLogType, siteLog);

    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the MOBS site with added sensors.
     **/
    @Test
    public void testSensorsMapping() throws Exception {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.geodesyMLSiteLogReader("MOBS-sensors"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        testMappingValues(siteLogType, siteLog);

        List<HumiditySensorPropertyType> humiditySensors = siteLogType.getHumiditySensors();
        assertThat(siteLog.getHumiditySensors().size(), equalTo(2));
        assertThat(humiditySensors.size(), equalTo(2));

        {
            int i = 0;
            for (HumiditySensorLogItem logItem : sortLogItems(siteLog.getHumiditySensors())) {
                HumiditySensorType xmlType = humiditySensors.get(i++).getHumiditySensor();
                assertThat(logItem.getSerialNumber(), equalTo(xmlType.getSerialNumber()));
            }
        }

        List<PressureSensorPropertyType> pressureSensors = siteLogType.getPressureSensors();
        assertThat(siteLog.getPressureSensors().size(), equalTo(2));
        assertThat(pressureSensors.size(), equalTo(2));

        {
            int i = 0;
            for (PressureSensorLogItem logItem : sortLogItems(siteLog.getPressureSensors())) {
                PressureSensorType xmlType = pressureSensors.get(i++).getPressureSensor();
                assertThat(logItem.getSerialNumber(), equalTo(xmlType.getSerialNumber()));
            }
        }

        List<TemperatureSensorPropertyType> temperatureSensors = siteLogType.getTemperatureSensors();
        assertThat(siteLog.getTemperatureSensors().size(), equalTo(2));
        assertThat(temperatureSensors.size(), equalTo(2));

        {
            int i = 0;
            for (TemperatureSensorLogItem logItem : sortLogItems(siteLog.getTemperatureSensors())) {
                TemperatureSensorType xmlType = temperatureSensors.get(i++).getTemperatureSensor();
                assertThat(logItem.getSerialNumber(), equalTo(xmlType.getSerialNumber()));
            }
        }

        List<WaterVaporSensorPropertyType> waterVaporSensors = siteLogType.getWaterVaporSensors();
        assertThat(siteLog.getWaterVaporSensors().size(), equalTo(2));
        assertThat(waterVaporSensors.size(), equalTo(2));

        {
            int i = 0;
            for (WaterVaporSensorLogItem logItem : sortLogItems(siteLog.getWaterVaporSensors())) {
                WaterVaporSensorType xmlType = waterVaporSensors.get(i++).getWaterVaporSensor();
                assertThat(logItem.getSerialNumber(), equalTo(xmlType.getSerialNumber()));
            }
        }

        // TODO: test the from mapping when it is implemented
        // SiteLogType mappedSiteLogType = mapper.from(siteLog);
        // testMappingValues(mappedSiteLogType, siteLog);

    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the QIKI site with added otherInstrumentations.
     **/
    @Test
    public void testOtherInstrumentationsMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.geodesyMLTestDataSiteLogReader("QIKI-otherInstrumentation"),
                        GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<OtherInstrumentationPropertyType> otherInstrumentationPropertyTypes = siteLogType.getOtherInstrumentations();
        sortGMLPropertyTypes(otherInstrumentationPropertyTypes);

        assertThat(siteLogType.getOtherInstrumentations().size(), equalTo(3));
        assertThat(otherInstrumentationPropertyTypes.size(), equalTo(3));

        {
            int i = 0;
            for (OtherInstrumentationLogItem logItem : sortLogItems(siteLog.getOtherInstrumentationLogItem())) {
                OtherInstrumentationType xmlType = otherInstrumentationPropertyTypes.get(i++).getOtherInstrumentation();
                assertThat(logItem.getInstrumentation(), equalTo(xmlType.getInstrumentation()));
            }
        }
    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the METZ site log with added signal obstructions.
     **/
    @Test
    public void testSignalObstructionsMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.geodesyMLTestDataSiteLogReader("METZ-signalObstructionSet"),
                        GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<SignalObstructionsPropertyType> signalObstructionsPropertyTypes = siteLogType.getSignalObstructionsSet();
        sortGMLPropertyTypes(signalObstructionsPropertyTypes);

        assertThat(siteLogType.getSignalObstructionsSet().size(), equalTo(2));
        assertThat(signalObstructionsPropertyTypes.size(), equalTo(2));

        {
            int i = 0;
            for (SignalObstructionLogItem logItem : sortLogItems(siteLog.getSignalObstructionLogItems())) {
                BasePossibleProblemSourcesType xmlType = signalObstructionsPropertyTypes.get(i++).getSignalObstructions();
                assertThat(logItem.getPossibleProblemSource(), equalTo(xmlType.getPossibleProblemSources()));
            }
        }
    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the METZ site log with added multipath sources.
     **/
    @Test
    public void testMultipathSourcesMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.geodesyMLTestDataSiteLogReader("METZ-multipathSources"),
                        GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<MultipathSourcesPropertyType> multipathSourcesPropertyTypes = siteLogType.getMultipathSourcesSet();
        sortGMLPropertyTypes(multipathSourcesPropertyTypes);

        assertThat(siteLogType.getMultipathSourcesSet().size(), equalTo(2));
        assertThat(multipathSourcesPropertyTypes.size(), equalTo(2));

        {
            int i = 0;
            for (MultipathSourceLogItem logItem : sortLogItems(siteLog.getMultipathSourceLogItems())) {
                BasePossibleProblemSourcesType xmlType = multipathSourcesPropertyTypes.get(i++).getMultipathSources();
                assertThat(logItem.getPossibleProblemSource(), equalTo(xmlType.getPossibleProblemSources()));
            }
        }
    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the WGTN site log with added local episodic events.
     **/
    @Test
    public void testLocalEpisodicEventsMapping() throws Exception {
        GeodesyMLType mobs = marshaller
            .unmarshal(TestResources.geodesyMLTestDataSiteLogReader("WGTN-localEpisodicEvents"),
                GeodesyMLType.class)
            .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
            .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<LocalEpisodicEventsPropertyType> localEpisodicEventsPropertyTypes = siteLogType.getLocalEpisodicEventsSet();
        sortGMLPropertyTypes(localEpisodicEventsPropertyTypes);

        assertThat(siteLogType.getLocalEpisodicEventsSet().size(), equalTo(4));
        assertThat(localEpisodicEventsPropertyTypes.size(), equalTo(4));

        {
            int i = 0;
            for (LocalEpisodicEventLogItem logItem : sortLogItems(siteLog.getLocalEpisodicEventLogItems())) {
                LocalEpisodicEventsType xmlType = localEpisodicEventsPropertyTypes.get(i++).getLocalEpisodicEvents();
                assertThat(logItem.getEvent(), equalTo(xmlType.getEvent()));
            }
        }
    }

    private void testMappingValues(SiteLogType siteLogType, SiteLog siteLog) {
        assertThat(siteLog.getSiteIdentification().getSiteName(), equalTo(siteLogType.getSiteIdentification().getSiteName()));
        assertThat(siteLog.getSiteLocation().getTectonicPlate(), equalTo(siteLogType.getSiteLocation().getTectonicPlate().getValue()));

        List<GnssReceiverPropertyType> receiverProperties = siteLogType.getGnssReceivers();
        sortGMLPropertyTypes(receiverProperties);
        assertThat(siteLog.getGnssReceivers().size(), equalTo(9));
        assertThat(receiverProperties.size(), equalTo(9));

        {
            int i = 0;
            for (GnssReceiverLogItem receiverLogItem : sortLogItems(siteLog.getGnssReceivers())) {
                GnssReceiverType receiverType = receiverProperties.get(i++).getGnssReceiver();
                assertThat(receiverLogItem.getFirmwareVersion(), equalTo(receiverType.getFirmwareVersion()));
            }
        }
    }

    /**
     * Sort set of log items by installation date.
     */
    private <T extends LogItem> SortedSet<T> sortLogItems(Set<T> logItems) {
        SortedSet<T> sorted = new TreeSet<>(new Comparator<T>() {
            public int compare(T e, T f) {
                int c = e.getEffectiveDates().compareTo(f.getEffectiveDates());
                // keep duplicates
                return c != 0 ? c : 1;
            }
        });
        sorted.addAll(logItems);
        return sorted;
    }

    /**
     * Sort list of GMLPropertyType objects by installation date.
     */
    private <P extends GMLPropertyType> void sortGMLPropertyTypes(List<P> list) {
        Collections.sort(list, new Comparator<P>() {
            public int compare(P p, P q) {
                int dateComparison = dateInstalled(p).compareTo(dateInstalled(q));
                if (dateComparison == 0) {
                    dateComparison = dateRemoved(p).compareTo(dateRemoved(q));
                }
                return dateComparison;
            }

            private Instant dateInstalled(P p) {

                TimePositionType time = null;

                try {
                    time = (TimePositionType) PropertyUtils.getProperty(p.getTargetElement(),"dateInstalled");
                    return new InstantToTimePositionConverter().convertFrom(time, TypeFactory.valueOf(Instant.class), null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    // try a different version of the "installation date"
                    try {
                        time = (TimePositionType)PropertyUtils.getProperty(p.getTargetElement(),
                                "validTime.abstractTimePrimitive.value.beginPosition");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e2) {
                        throw new RuntimeException(e2);
                    }
                }
                return new InstantToTimePositionConverter().convertFrom(time, TypeFactory.valueOf(Instant.class), null);
            }

            private Instant dateRemoved(P p) {

                TimePositionType time = null;

                try {
                    time = (TimePositionType) PropertyUtils.getProperty(p.getTargetElement(),"dateRemoved");
                    return new InstantToTimePositionConverter().convertFrom(time, TypeFactory.valueOf(Instant.class), null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    // try a different version of the "installation date"
                    try {
                        time = (TimePositionType)PropertyUtils.getProperty(p.getTargetElement(),
                            "validTime.abstractTimePrimitive.value.endPosition");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e2) {
                        throw new RuntimeException(e2);
                    }
                }
                return new InstantToTimePositionConverter().convertFrom(time, TypeFactory.valueOf(Instant.class), null);
            }
            
        });
    }


}

