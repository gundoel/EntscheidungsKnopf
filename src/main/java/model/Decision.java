package model;

import ekutil.EkDate;
import ekutil.ZonedDateTimeAdapter;

import java.time.ZonedDateTime;
import java.util.UUID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Decision {
    private String uuid;
    private ZonedDateTime created;
    private ZonedDateTime replaced;
    private String createdUser;
    private String decisionText;

    public Decision() {
        this(null, null, null, null, null);
    }

    public Decision(String decisionText) {
        this(null,null,null,null, decisionText);
    }

    public Decision(String uuid, ZonedDateTime created, ZonedDateTime replaced, String createdUser, String decisionText) {
        this.uuid = (uuid == null) ? UUID.randomUUID().toString() : uuid;
        this.created = (created == null) ? ZonedDateTime.now() : created;
        this.replaced = (replaced == null) ? EkDate.getHighDate() : replaced;
        this.createdUser = (createdUser == null) ? System.getProperty("user.name") : createdUser;
        this.decisionText = decisionText;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public void setReplaced(ZonedDateTime replaced) {
        this.replaced = replaced;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public void setDecisionText(String decisionText) {
        this.decisionText = decisionText;
    }

    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    public ZonedDateTime getCreated() {
        return created;
    }

    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    public ZonedDateTime getReplaced() {
        return replaced;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public String getDecisionText() {
        return decisionText;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Decision) {
            //id comparison
            Decision decision = (Decision) o;
            return (decision.uuid.equals(uuid) || decision.decisionText.equals(decisionText));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(uuid);
    }
}
