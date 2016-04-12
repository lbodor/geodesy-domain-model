package au.gov.ga.geodesy.domain.model.event;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SITE_LOG_RECEIVED")
@DiscriminatorValue("site log received")
public class SiteLogReceived extends Event {

    @Column(name = "FOUR_CHAR_ID", nullable = false)
    private String fourCharacterId;

    @SuppressWarnings("unused") // used by hibernate
    private SiteLogReceived() {
    }

    public SiteLogReceived(String fourCharacterId) {
        this.fourCharacterId = fourCharacterId;
    }

    public String getFourCharacterId() {
        return fourCharacterId;
    }
}

