package au.gov.ga.geodesy.domain.model.sitelog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * http://sopac.ucsd.edu/ns/geodesy/doc/igsSiteLog/2004/igsSiteLog.xsd:igsSiteLogType
 */
@Configurable
@Entity
@Table(name = "SITELOG_SITE")
public class IgsSiteLog {
    private static final Log log = LogFactory.getLog(IgsSiteLog.class);

    @Id
    @GeneratedValue(generator = "surrogateKeyGenerator")
    @SequenceGenerator(name = "surrogateKeyGenerator", sequenceName = "SEQ_SITELOGSITE")
    private Integer id;

    private Date entryDate;

    @Valid
    @Embedded
    protected FormInformation formInformation;

    @Valid
    @NotNull
    @Embedded
    protected SiteIdentification siteIdentification;

    @Valid
    @Embedded
    protected SiteLocation siteLocation;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<GnssReceiverLogItem> gnssReceivers = new HashSet<GnssReceiverLogItem>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<GnssAntennaLogItem> gnssAntennas = new HashSet<GnssAntennaLogItem>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<SurveyedLocalTie> surveyedLocalTies;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<FrequencyStandardLogItem> frequencyStandards = new HashSet<FrequencyStandardLogItem>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<CollocationInformation> collocationInformation;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<HumiditySensorLogItem> humiditySensors = new HashSet<HumiditySensorLogItem>();

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<PressureSensorLogItem> pressureSensors;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<TemperatureSensorLogItem> temperatureSensors;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<WaterVaporSensorLogItem> waterVaporSensors;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<OtherInstrumentation> otherInstrumentation;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<RadioInterference> radioInterferences;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<MultipathSource> multipathSources;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<SignalObstruction> signalObstructions;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "SITE_ID", referencedColumnName = "ID")
    protected Set<LocalEpisodicEvent> localEpisodicEvents;

    @Valid
    @Embedded
    protected MoreInformation moreInformation;

    @SuppressWarnings("unused") // used by hibernate
    private Integer getId() {
        return id;
    }

    @SuppressWarnings("unused")
    private void setId(Integer id) {
        this.id = id;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    protected void setEntryDate(Date d) {
        this.entryDate = d;
    }

    @SuppressWarnings("unused")
    private void setSiteLogXml(String xml) {
    }

    /**
     * Return form information.
     */
    public FormInformation getFormInformation() {
        return formInformation;
    }

    /**
     * Set form information.
     */
    public void setFormInformation(FormInformation value) {
        this.formInformation = value;
    }

    /**
     * Return site identification.
     */
    public SiteIdentification getSiteIdentification() {
        return siteIdentification;
    }

    /**
     * Set site identification.
     */
    public void setSiteIdentification(SiteIdentification value) {
        this.siteIdentification = value;
    }

    /**
     * Return site location.
     */
    public SiteLocation getSiteLocation() {
        return siteLocation;
    }

    /**
     * Set site location.
     */
    public void setSiteLocation(SiteLocation value) {
        this.siteLocation = value;
    }

    /**
     * Return GNSS receivers.
     */
    public Set<GnssReceiverLogItem> getGnssReceivers() {
        return gnssReceivers;
    }

    /**
     * Set GNSS receivers.
     */
    public void setGnssReceivers(Set<GnssReceiverLogItem> rs) {
        gnssReceivers = rs;
    }

    /**
     * Return GNSS antennas.
     */
    public Set<GnssAntennaLogItem> getGnssAntennas() {
        return gnssAntennas;
    }

    /**
     * Set GNSS antennas.
     */
    public void setGnssAntennas(Set<GnssAntennaLogItem> as) {
        gnssAntennas = as;
    }

    /**
     * Return surveyed local ties.
     */
    public Set<SurveyedLocalTie> getSurveyedLocalTies() {
        return this.surveyedLocalTies;
    }

    /**
     * Set surveyed local ties.
     */
    public void setSurveyedLocalTies(Set<SurveyedLocalTie> ties) {
        surveyedLocalTies = ties;
    }

    /**
     * Return frequency standards.
     */
    public Set<FrequencyStandardLogItem> getFrequencyStandards() {
        return frequencyStandards;
    }

    /**
     * Set frequency standards.
     */
    public void setFrequencyStandards(Set<FrequencyStandardLogItem> fs) {
        frequencyStandards = fs;
    }

    /**
     * Return collocation information.
     */
    public Set<CollocationInformation> getCollocationInformation() {
        return collocationInformation;
    }

    /**
     * Set collocation information.
     */
    public void setCollocationInformation(Set<CollocationInformation> c) {
        collocationInformation = c;
    }

    /**
     * Return humidity sensor.
     */
    public Set<HumiditySensorLogItem> getHumiditySensors() {
        return this.humiditySensors;
    }

    /**
     * Set humidity sensor.
     */
    public void setHumiditySensors(Set<HumiditySensorLogItem> hs) {
        humiditySensors = hs;
    }

    /**
     * Return pressure sensors.
     */
    public Set<PressureSensorLogItem> getPressureSensors() {
        return this.pressureSensors;
    }

    /**
     * Set pressure sensors.
     */
    public void setPressureSensors(Set<PressureSensorLogItem> ps) {
        pressureSensors = ps;
    }

    /**
     * Return temperature sensors.
     */
    public Set<TemperatureSensorLogItem> getTemperatureSensors() {
        return this.temperatureSensors;
    }

    /**
     * Set temperature sensors.
     */
    public void setTemperatureSensors(Set<TemperatureSensorLogItem> ts) {
        temperatureSensors = ts;
    }

    /**
     * Return water vapor sensors.
     */
    public Set<WaterVaporSensorLogItem> getWaterVaporSensors() {
        return this.waterVaporSensors;
    }

    /**
     * Set water vapor sensors.
     */
    public void setWaterVaporSensors(Set<WaterVaporSensorLogItem> vs) {
        waterVaporSensors = vs;
    }

    /**
     * Return other instrumentation.
     */
    public Set<OtherInstrumentation> getOtherInstrumentation() {
        return this.otherInstrumentation;
    }

    /**
     * Set other instrumentation.
     */
    public void setOtherInstrumentation(Set<OtherInstrumentation> i) {
        otherInstrumentation = i;
    }

    /**
     * Return radio interferences.
     */
    public Set<RadioInterference> getRadioInterferences() {
        return this.radioInterferences;
    }

    /**
     * Set radio interferences.
     */
    public void setRadioInterferences(Set<RadioInterference> rs) {
        radioInterferences = rs;
        /* CollectionUtils.filter(radioInterferences, NotNullPredicate.INSTANCE); */
    }

    /**
     * Return multipath sources.
     */
    public Set<MultipathSource> getMultipathSources() {
        return this.multipathSources;
    }

    /**
     * Set multipath sources.
     */
    public void setMultipathSources(Set<MultipathSource> ms) {
        multipathSources = ms;
    }

    /**
     * Return signal obstructions.
     */
    public Set<SignalObstruction> getSignalObstructions() {
        return this.signalObstructions;
    }

    /**
     * Set signal obstructions.
     */
    public void setSignalObstructions(Set<SignalObstruction> s) {
        signalObstructions = s;
    }

    /**
     * Return local episodic events.
     */
    public Set<LocalEpisodicEvent> getLocalEpisodicEvents() {
        return this.localEpisodicEvents;
    }

    /**
     * Set local episodic events.
     */
    public void setLocalEpisodicEvents(Set<LocalEpisodicEvent> es) {
        localEpisodicEvents = es;
    }

    /**
     * Return more information.
     */
    public MoreInformation getMoreInformation() {
        return moreInformation;
    }

    /**
     * Set more information.
     */
    public void setMoreInformation(MoreInformation value) {
        this.moreInformation = value;
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        for (Field f : getClass().getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(f.getType())) {
                try {
                    CollectionUtils.filter((Collection<?>) f.get(this), NotNullPredicate.getInstance());
                } catch (IllegalAccessException e) {
                    log.warn(e);
                }
            }
        }
    }

    public List<EquipmentLogItem> getEquipmentLogItems() {
        List<EquipmentLogItem> equipment = new ArrayList<EquipmentLogItem>();
        equipment.addAll(getGnssReceivers());
        equipment.addAll(getGnssAntennas());
        equipment.addAll(getHumiditySensors());
        equipment.addAll(getFrequencyStandards());
        return equipment;
    }
}