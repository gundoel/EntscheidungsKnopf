package io;

import model.Decision;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvStrategy implements IOStrategy {
    private String splitter = ";";
    private FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Textdateien (*.csv, *.txt)", "*.csv", "*.txt");

    public CsvStrategy() {

    }

    @Override
    public void save(String filepath, List<Decision> decisionList) throws IOException {
        try (BufferedWriter buffer = new BufferedWriter(new FileWriter(filepath))) {
            for (Decision decision : decisionList) {
                buffer.append(decision.getUuid() + splitter);
                buffer.append(decision.getCreated().toString() + splitter);
                buffer.append(decision.getReplaced().toString() + splitter);
                buffer.append(decision.getCreatedUser() + splitter);
                buffer.append(decision.getDecisionText().toString() + splitter);
                buffer.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public FileChooser.ExtensionFilter getFileExtensionFilter() {
        return extFilter;
    }

    public List<Decision> load(String filepath) throws Exception {
        ArrayList<Decision> decisionList = new ArrayList<Decision>();
        String line = "";
        int lineNr = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath, StandardCharsets.UTF_8))) {
            while ((line = reader.readLine()) != null) {
                lineNr++;
                String[] lineArray = line.split(splitter);
                decisionList.add(new Decision(
                        lineArray[0],
                        ZonedDateTime.parse(lineArray[1]),
                        ZonedDateTime.parse(lineArray[2]),
                        lineArray[3],
                        lineArray[4])
                );
            }
        } catch (DateTimeParseException e) {
            throw new Exception("Ungültiges Datumsformat in Zeile " + lineNr + ".");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Zeile " + lineNr + " hat ungültiges Format. Erwartet: uuid (String), created(ZonedDateTime), replaced (ZonedDateTime), createdUser (String), decision (String)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decisionList;
    }

}
