package views;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

import controllers.*;

/**
 * Main loads and creates the interface for the user to interact with.
 *
 * @author Eric Yuan
 * @version 1.0
 */
public class Main extends Application {

    /**
     * The path for the config directory to contain log.txt and saved directory.
     */
    private static final String configDirectoryPath = System.getProperty("user.dir") + "/config";

    /**
     * The path for the saved directory to contain all the serialized files.
     */
    private static final String savedDirectoryPath = configDirectoryPath + "/saved";

    /**
     * The path for the log.txt file.
     */
    public static final String logPath = configDirectoryPath + "/log.txt";

    /**
     * The path for serialized tags file.
     */
    public static final String serializedTags = savedDirectoryPath + "/tags.ser";

    /**
     * The path for serialized directories file.
     */
    public static final String serializedDirectories = savedDirectoryPath + "/directories.ser";

    /**
     * The path for serialized favourites file.
     */
    public static final String serializedFavourites = savedDirectoryPath + "/favourites.ser";

    /**
     * All processes required to be executed every time the application starts.
     *
     * @param stage the stage to load the .fxml file
     * @throws IOException            if IOException occurs, it's then thrown
     * @throws ClassNotFoundException if ClassNotFoundException occurs, it's then thrown
     */
    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/gui.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        stage.setTitle("Super Duper Image Tagger");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        createConfigDirectory();
    }

    /**
     * All processes require to be executed every time the application stops.
     *
     * @throws IOException if IOException occurs, it's then thrown
     */
    @Override
    public void stop() throws IOException {
        createConfigDirectory();
        GUIController.getTagManager().writeSavedTags();
        GUIController.getDirectoryManager().writeSavedDirectories();
        GUIController.getFavourites().writeSavedFavourites();
    }

    /**
     * Creates the necessary configuration files. If the files have been deleted or moved,
     * new folders and text files will be created.
     *
     * @throws IOException if IOException occurs, it's then thrown
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createConfigDirectory() throws IOException {
        File configDirectory = new File(configDirectoryPath);
        File saved = new File(savedDirectoryPath);
        File log = new File(logPath);

        if (configDirectory.exists()) {
            saved.mkdir();
            log.createNewFile();
        } else {
            configDirectory.mkdir();
            saved.mkdir();
            log.createNewFile();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
