package io;

import com.sun.glass.ui.CommonDialogs;
import model.Decision;
import model.DecisionListWrapper;
import javafx.stage.FileChooser;

import javax.xml.bind.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class XmlStrategy implements IOStrategy {
    private FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML-Dateien (*.xml)", "*.xml");

    public XmlStrategy() {
    }

    @Override
    public void save(String filepath, List<Decision> decisions) throws Exception {
        try
        {
            JAXBContext context = JAXBContext
                    .newInstance(DecisionListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            DecisionListWrapper wrapper = new DecisionListWrapper();
            wrapper.setDecisions(decisions);
            m.marshal(wrapper, new File(filepath));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new Exception("XML konnte nicht gespeichert werden.");
        }
    }

    @Override
    public FileChooser.ExtensionFilter getFileExtensionFilter() {
        return extFilter;
    }

    @Override
    public List<Decision> load(String filepath) throws IOException, ClassNotFoundException, Exception {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(DecisionListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            DecisionListWrapper wrapper = (DecisionListWrapper) um.unmarshal(new File(filepath));
            return wrapper.getDecisionList();
        } catch (UnmarshalException e) {
            throw new Exception("Ungültige XML-Datei. Datei prüfen.");
        }
    }
}
