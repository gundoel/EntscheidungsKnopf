package model;

import ekutil.EkDate;
import io.IOStrategy;
import io.XmlStrategy;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.UnmarshalException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppData {
    private ObservableList<Decision> decisionList = FXCollections.observableArrayList();
    private SimpleBooleanProperty decisionsSaved = new SimpleBooleanProperty(true);
    private SimpleStringProperty msg = new SimpleStringProperty();
    private String filepath;
    private IOStrategy ioStrategy;

    public AppData() {
        // Default strategy is XML
        this.ioStrategy = new XmlStrategy();
    }

    public SimpleBooleanProperty getDecisionsSaved() {
        return decisionsSaved;
    }

    public void setDecisionsSaved(boolean decisionsSaved) {
        this.decisionsSaved.setValue(decisionsSaved);
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public ObservableList<Decision> getDecisionList() {
        return decisionList;
    }

    public SimpleStringProperty getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg.setValue(msg);
    }

    public void fillDecisionList() throws Exception {
        decisionList.clear();
        decisionList.addAll(ioStrategy.load(getFilepath()));
    }

    public String getRandomDecision() {
        if (decisionList.size() > 0) {
            Random r = new Random();
            return decisionList.get(r.nextInt(decisionList.size())).getDecisionText();
        } else {
            msg.setValue("Keine Entscheidungen in Liste.");
            return null;
        }
    }

    public boolean handleAddAction(String decisionText) {
        Decision decision = new Decision(decisionText);
        if (decisionList.indexOf(decision) < 0) {
            decisionList.add(decision);
            msg.setValue("'" + decision.getDecisionText() + "' hinzugefügt.");
            return true;
        } else {
            msg.setValue("'" + decisionText + "' ist bereits in Liste.");
            return false;
        }
    }

    public boolean handleSaveAction(Stage parent) {
        if (filepath == null) {
            return handleSaveAsAction(parent);
        } else {
            try {
                ioStrategy.save(filepath, decisionList);
                decisionsSaved.setValue(true);
                msg.setValue("Entscheidungen in " + filepath + " gespeichert.");
                return true;
            } catch (Exception e) {
                msg.setValue("Daten konnten nicht gespeichert werden.");
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean handleSaveAsAction(Stage parent) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(getIoStrategy().getFileExtensionFilter());
        chooser.setTitle("Entscheidungen speichern unter");
        IOStrategy io = getIoStrategy();
        chooser.setInitialFileName("decisions_" + EkDate.getCurrentDateTimeString("yyyyMMdd_HHmm") +
                getIoStrategy().getFileExtensionFilter().getExtensions().get(0).replaceAll("\\*",""));
        File file = chooser.showSaveDialog(parent);
        if (file != null) {
            filepath = file.getAbsolutePath();
            try {
                List<Decision> decisionStandardList = new ArrayList<Decision>(decisionList);
                ioStrategy.save(filepath, decisionStandardList);
                decisionsSaved.setValue(true);
                msg.setValue("Entscheidungen in " + filepath + " gespeichert.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * deletes selected item from decisionList. table view is updated automatically
     */
    private void deleteItem(Decision decisionToDelete) {
        //update ObservableList to make table update
        //decisionTable.getItems().remove(decisionToDelete);
        decisionList.remove(decisionList.indexOf(decisionToDelete));
    }

    public boolean handleDeleteAction(Decision decisionToDelete) {
        try {
            deleteItem(decisionToDelete);
            msg.setValue("'" + decisionToDelete.getDecisionText() + "' (" + decisionToDelete.getUuid() + ") gelöscht.");
            return true;
        } catch (Exception e) {
            msg.setValue("Daten konnten nicht gelöscht werden.");
            e.printStackTrace();
            return false;
        }
    }

    public void setIOStrategy(IOStrategy ioStrategy) {
        this.ioStrategy = ioStrategy;
    }

    public IOStrategy getIoStrategy() {
        return ioStrategy;
    }
}
