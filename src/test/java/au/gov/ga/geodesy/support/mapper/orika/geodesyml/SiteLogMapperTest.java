package au.gov.ga.geodesy.support.mapper.orika.geodesyml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.gov.ga.geodesy.domain.model.sitelog.CollocationInformation;
import au.gov.ga.geodesy.domain.model.sitelog.FrequencyStandardLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.GnssAntennaLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.GnssReceiverLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.HumiditySensorLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.LocalEpisodicEventLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.LogItem;
import au.gov.ga.geodesy.domain.model.sitelog.MultipathSourceLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.OtherInstrumentationLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.PressureSensorLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.RadioInterference;
import au.gov.ga.geodesy.domain.model.sitelog.SignalObstructionLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.SiteLog;
import au.gov.ga.geodesy.domain.model.sitelog.SurveyedLocalTie;
import au.gov.ga.geodesy.domain.model.sitelog.TemperatureSensorLogItem;
import au.gov.ga.geodesy.domain.model.sitelog.WaterVaporSensorLogItem;
import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLUtils;
import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.ga.geodesy.support.TestResources;
import au.gov.ga.geodesy.support.gml.GMLPropertyType;
import au.gov.ga.geodesy.support.marshalling.moxy.GeodesyMLMoxy;
import au.gov.ga.geodesy.support.spring.IntegrationTest;
import au.gov.ga.geodesy.support.utils.GMLDateUtils;
import au.gov.xml.icsm.geodesyml.v_0_3.BasePossibleProblemSourcesType;
import au.gov.xml.icsm.geodesyml.v_0_3.CollocationInformationPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.CollocationInformationType;
import au.gov.xml.icsm.geodesyml.v_0_3.FrequencyStandardPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.FrequencyStandardType;
import au.gov.xml.icsm.geodesyml.v_0_3.GeodesyMLType;
import au.gov.xml.icsm.geodesyml.v_0_3.GnssAntennaPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.GnssAntennaType;
import au.gov.xml.icsm.geodesyml.v_0_3.GnssReceiverPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.GnssReceiverType;
import au.gov.xml.icsm.geodesyml.v_0_3.HumiditySensorPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.HumiditySensorType;
import au.gov.xml.icsm.geodesyml.v_0_3.LocalEpisodicEventsPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.LocalEpisodicEventsType;
import au.gov.xml.icsm.geodesyml.v_0_3.MultipathSourcesPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.OtherInstrumentationPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.OtherInstrumentationType;
import au.gov.xml.icsm.geodesyml.v_0_3.PressureSensorPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.PressureSensorType;
import au.gov.xml.icsm.geodesyml.v_0_3.RadioInterferencesPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.SignalObstructionsPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.SiteLogType;
import au.gov.xml.icsm.geodesyml.v_0_3.SurveyedLocalTiesPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.SurveyedLocalTiesType;
import au.gov.xml.icsm.geodesyml.v_0_3.TemperatureSensorPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.TemperatureSensorType;
import au.gov.xml.icsm.geodesyml.v_0_3.WaterVaporSensorPropertyType;
import au.gov.xml.icsm.geodesyml.v_0_3.WaterVaporSensorType;

import ma.glasnost.orika.metadata.TypeFactory;

import net.opengis.gml.v_3_2_1.TimePeriodType;
import net.opengis.gml.v_3_2_1.TimePositionType;

public class SiteLogMapperTest extends IntegrationTest {

    private SiteLogMapper mapper;
    private GeodesyMLMarshaller marshaller;

    @BeforeClass
    private void init() {
        mapper = new SiteLogMapper();
        marshaller = new GeodesyMLMoxy();
    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType.
     **/
    @Test
    public void testMapping() throws Exception {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        testMappingValues(siteLogType, siteLog);

        checkSiteContacts(siteLogType, siteLog);

        // TODO: complete tests
        siteLogType = mapper.from(siteLog);
        checkSiteContacts(siteLog, siteLogType);
    }

    private void checkSiteContacts(SiteLogType siteLogType, SiteLog siteLog) {
        assertThat(
            siteLogType.getSiteContact().get(0).getCIResponsibleParty().getIndividualName().getCharacterString().getValue(),
            is(siteLog.getSiteContacts().get(0).getParty().getIndividualName())
        );
    }

    private void checkSiteContacts(SiteLog siteLog, SiteLogType siteLogType) {
        assertThat(
            siteLog.getSiteContacts().get(0).getParty().getIndividualName(),
            is(siteLogType.getSiteContact().get(0).getCIResponsibleParty().getIndividualName().getCharacterString().getValue())
        );
    }

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the MOBS site with added sensors.
     **/
    @Test
    public void testSensorsMapping() throws Exception {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS-sensors"), GeodesyMLType.class)
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
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("QIKI-otherInstrumentation"),
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
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("METZ-signalObstructionSet"),
                        GeodesyMLType.class).getValue();

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
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("METZ-multipathSources"),
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
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.customGeodesyMLSiteLogReader("WGTN-localEpisodicEvents"), GeodesyMLType.class).getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class).findFirst().get();

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

    /**
     * Test mapping from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the METZ site log for multipath sources that also has RadioInterference data.
     **/
    @Test
    public void testRadioInterferenceMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("METZ-multipathSources"),
                        GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<RadioInterferencesPropertyType> radioInterferencesPropertyTypes = siteLogType.getRadioInterferencesSet();
        sortGMLPropertyTypes(radioInterferencesPropertyTypes);

        assertThat(siteLogType.getRadioInterferencesSet().size(), equalTo(1));
        assertThat(radioInterferencesPropertyTypes.size(), equalTo(1));

        {
            int i = 0;
            for (RadioInterference logItem : sortLogItems(siteLog.getRadioInterferences())) {
                BasePossibleProblemSourcesType xmlType = radioInterferencesPropertyTypes.get(i++).getRadioInterferences();
                assertThat(logItem.getPossibleProblemSource(), equalTo(xmlType.getPossibleProblemSources()));
            }
        }
    }
    /**
     * Test the mapping of MoreInformation from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the MOBS-moreInfo site with added sensors.
     **/
    @Test
    public void testMoreInformationMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS-moreInfo"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        assertThat(siteLog.getMoreInformation().getPrimaryDataCenter(), is(siteLogType.getMoreInformation().getDataCenter().get(0)));
        assertThat(siteLog.getMoreInformation().getNotes(), is(siteLogType.getMoreInformation().getNotes()));
        assertThat(siteLog.getMoreInformation().getDoi(), is(siteLogType.getMoreInformation().getDOI().getValue()));
    }


    /**
     * Test the mapping of FormInformation from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the ALIC site with added sensors.
     **/
    @Test
    public void testFormInformationMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("ALIC"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        assertThat(siteLog.getFormInformation().getReportType(), is(siteLogType.getFormInformation().getReportType()));
        assertThat(siteLog.getFormInformation().getPreparedBy(), is(siteLogType.getFormInformation().getPreparedBy()));
    }

    /**
     * Test the mapping of CollocationInformation from SiteLogType to SiteLog and back
     * to SiteLogType. Based on the AIRA-collocationInfo site with added sensors.
     **/
    @Test
    public void testCollocationInformationMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("AIRA-collocationInfo"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        List<CollocationInformationPropertyType> collocationInfoProperties = siteLogType.getCollocationInformations();
        sortGMLPropertyTypes(collocationInfoProperties);

        assertThat(siteLog.getCollocationInformation(), hasSize(1));
        assertThat(collocationInfoProperties, hasSize(1));

        {
            int i = 0;
            for (CollocationInformation collocationInfo : sortCollocationInformations(siteLog.getCollocationInformation())) {
                CollocationInformationType collocationInfoType = collocationInfoProperties.get(i++).getCollocationInformation();
                assertThat(collocationInfo.getInstrumentType(), is(collocationInfoType.getInstrumentationType().getValue()));

                TimePeriodType timePeriodType = (TimePeriodType) collocationInfoType.getValidTime().getAbstractTimePrimitive().getValue();
                String beginTime = timePeriodType.getBeginPosition().getValue().get(0).toString();

                assertThat(collocationInfo.getEffectiveDates().getFrom().toString(), is(beginTime));
                assertThat(collocationInfo.getStatus(), is(collocationInfoType.getStatus().getValue()));
            }
        }
    }

    /**
     * Test the mapping of SurveyedLocalTie from SiteLogType to SiteLog and back to SiteLogType.
     **/
    @Test
    public void testSurveyedLocalTieMapping() throws Exception {
        GeodesyMLType mobs = marshaller
                .unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);
        List<SurveyedLocalTiesPropertyType> surveyedLocalTiesProperties = siteLogType.getSurveyedLocalTies();
        sortSurveyedLocalTiesPropertyTypes(surveyedLocalTiesProperties);

        assertThat(siteLog.getSurveyedLocalTies(), hasSize(4));
        assertThat(surveyedLocalTiesProperties, hasSize(4));

        {
            int i = 0;
            for (SurveyedLocalTie surveyedLocalTie : sortSurveyedLocalTies(siteLog.getSurveyedLocalTies())) {
                SurveyedLocalTiesType surveyedLocalTiesType = surveyedLocalTiesProperties.get(i++).getSurveyedLocalTies();
                assertThat(surveyedLocalTie.getTiedMarkerName(), is(surveyedLocalTiesType.getTiedMarkerName()));
                assertThat(surveyedLocalTie.getTiedMarkerUsage(), is(surveyedLocalTiesType.getTiedMarkerUsage()));
                assertThat(surveyedLocalTie.getTiedMarkerCdpNumber(), Matchers.is(surveyedLocalTiesType.getTiedMarkerCDPNumber()));
                assertThat(surveyedLocalTie.getTiedMarkerDomesNumber(), Matchers.is(surveyedLocalTiesType.getTiedMarkerDOMESNumber()));
                assertThat(surveyedLocalTie.getDifferentialFromMarker().getDx().doubleValue(), Matchers.is(surveyedLocalTiesType.getDifferentialComponentsGNSSMarkerToTiedMonumentITRS().getDx()));
                assertThat(surveyedLocalTie.getDifferentialFromMarker().getDy().doubleValue(), Matchers.is(surveyedLocalTiesType.getDifferentialComponentsGNSSMarkerToTiedMonumentITRS().getDy()));
                assertThat(surveyedLocalTie.getDifferentialFromMarker().getDz().doubleValue(), Matchers.is(surveyedLocalTiesType.getDifferentialComponentsGNSSMarkerToTiedMonumentITRS().getDz()));
                assertThat(Double.parseDouble(surveyedLocalTie.getLocalSiteTieAccuracy()), Matchers.is(surveyedLocalTiesType.getLocalSiteTiesAccuracy()));
                assertThat(surveyedLocalTie.getSurveyMethod(), is(surveyedLocalTiesType.getSurveyMethod()));
                assertThat(surveyedLocalTie.getDateMeasured(), Matchers.is(GMLDateUtils.stringToDateMultiParsers(surveyedLocalTiesType.getDateMeasured().getValue().get(0))));
                assertThat(surveyedLocalTie.getNotes(), Matchers.is(surveyedLocalTiesType.getNotes()));
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

    @Test
    public void testGnssAntennaMapping() throws IOException, MarshallingException {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);


        List<GnssAntennaPropertyType> gnssAntennaPropertyTypes = siteLogType.getGnssAntennas();
        sortGMLPropertyTypes(gnssAntennaPropertyTypes);
        assertThat(siteLog.getGnssAntennas().size(), equalTo(1));
        assertThat(gnssAntennaPropertyTypes.size(), equalTo(1));

        {
            int i = 0;
            for (GnssAntennaLogItem antennaLogItem : sortLogItems(siteLog.getGnssAntennas())) {
                GnssAntennaType antennaType = gnssAntennaPropertyTypes.get(i++).getGnssAntenna();
                assertThat(antennaLogItem.getSerialNumber(), equalTo(antennaType.getSerialNumber()));
            }
        }
    }

    @Test
    public void testFrequencyMapping() throws IOException, MarshallingException {
        GeodesyMLType mobs = marshaller.unmarshal(TestResources.customGeodesyMLSiteLogReader("MOBS"), GeodesyMLType.class)
                .getValue();

        SiteLogType siteLogType = GeodesyMLUtils.getElementFromJAXBElements(mobs.getElements(), SiteLogType.class)
                .findFirst().get();

        SiteLog siteLog = mapper.to(siteLogType);

        List<FrequencyStandardPropertyType> frequencyStandardPropertyTypes = siteLogType.getFrequencyStandards();
        sortGMLPropertyTypes(frequencyStandardPropertyTypes);
        assertThat(siteLog.getFrequencyStandards().size(), equalTo(1));
        assertThat(frequencyStandardPropertyTypes.size(), equalTo(1));

        int i = 0;
        for (FrequencyStandardLogItem frequencyStandardLogItem : sortLogItems(siteLog.getFrequencyStandards())) {
            FrequencyStandardType frequencyStandardType = frequencyStandardPropertyTypes.get(i++).getFrequencyStandard();
            assertThat(frequencyStandardLogItem.getType(), equalTo(frequencyStandardType.getStandardType().getValue()));
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
                    time = (TimePositionType) PropertyUtils.getProperty(p.getTargetElement(), "dateInstalled");
                    return new InstantToTimePositionConverter().convertFrom(time, TypeFactory.valueOf(Instant.class), null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    // try a different version of the "installation date"
                    try {
                        time = (TimePositionType) PropertyUtils.getProperty(p.getTargetElement(),
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

    /**
     * Sort a set of CollocationInformations by effective dates.
     */
    private <T extends CollocationInformation> SortedSet<T> sortCollocationInformations(Set<T> info) {
        SortedSet<T> sorted = new TreeSet<>(new Comparator<T>() {
            public int compare(T e, T f) {
                int c = e.getEffectiveDates().compareTo(f.getEffectiveDates());
                // keep duplicates
                return c != 0 ? c : 1;
            }
        });
        sorted.addAll(info);
        return sorted;
    }

    /**
     * Sort a list of SurveyedLocalTiesPropertyType objects by tied marker names.
     */
    private <P extends SurveyedLocalTiesPropertyType> void sortSurveyedLocalTiesPropertyTypes(List<P> list) {
        Collections.sort(list, new Comparator<P>() {
            public int compare(P p, P q) {
                return tiedMarkerName(p).compareTo(tiedMarkerName(q));
            }

            private String tiedMarkerName(P p) {
                String name = null;
                try {
                    name = (String) PropertyUtils.getProperty(p.getTargetElement(), "tiedMarkerName");
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                return name;
            }
        });
    }

    /**
     * Sort a set of SurveyedLocalTies by tied marker names.
     */
    private <T extends SurveyedLocalTie> SortedSet<T> sortSurveyedLocalTies(Set<T> info) {
        SortedSet<T> sorted = new TreeSet<>(new Comparator<T>() {
            public int compare(T e, T f) {
                int c = e.getTiedMarkerName().compareTo(f.getTiedMarkerName());
                // keep duplicates
                return c != 0 ? c : 1;
            }
        });
        sorted.addAll(info);
        return sorted;
    }
}
