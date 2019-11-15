package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement (name = "decisions")
public class DecisionListWrapper {
    @XmlElement(name = "decision")
    private List<Decision> decisions;

    public List<Decision> getDecisionList() {
        return decisions;
    }

    public void setDecisions(List<Decision> decisions) {
        this.decisions = decisions;
    }
}