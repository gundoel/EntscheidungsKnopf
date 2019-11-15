package io;

import model.Decision;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.List;

public interface IOStrategy {
    void save(String filepath, List<Decision> decisions) throws Exception;

    FileChooser.ExtensionFilter getFileExtensionFilter();

    List<Decision> load(String filepath) throws IOException, ClassNotFoundException, Exception;
}
