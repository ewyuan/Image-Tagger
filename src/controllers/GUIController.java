package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.util.*;
import java.io.IOException;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;

import models.*;

/**
 * The Controller class that interacts with our model classes (ImageFile, Log, Directory,
 * DirectoryManager, Tag, TagManager, and Log) to update our View (gui.fxml)
 */
public class GUIController implements Initializable {

    /**
     * The Label that displays the current name of the ImageFile.
     */
    @FXML
    private Label imageName;

    /**
     * The ListView containing the current list of available tags
     */
    @FXML
    private ListView<Tag> currentTags;

    /**
     * The ListView containing the favourite ImageFile objects
     */
    @FXML
    private ListView<ImageFile> favouriteImages;

    /**
     * The TextField that allows the user to add a tag to the current
     * list of available tags
     */
    @FXML
    private TextField addTag;

    /**
     * The TagManager the GUIController uses and updates
     */
    private static TagManager tagManager;

    /**
     * The DirectoryManager the GUIController uses and updates
     */
    private static DirectoryManager directoryManager;

    /**
     * The ImageFile object that the GUIController manipulates
     */
    private ImageFile currentImage;

    /**
     * The Directory that's currently loaded
     */
    private Directory currentDirectory;

    /**
     * The ListView that displays the tags onthe currently displayed
     * ImageFile object
     */
    @FXML
    private ListView<Tag> imageTags;

    /**
     * The Button that allows the user to load a directory
     */
    @FXML
    private Button loadDirectory;

    /**
     * The ListView showing all the ImageFiles in the Directory that a
     * user has loaded
     */
    @FXML
    private TreeView<File> directoryView;

    /**
     * This ImageView displays the ImageFile the user has selected
     */
    @FXML
    private ImageView imageDisplay;

    /**
     * This ListView displays all the previous names  an ImageFile has
     * had
     */
    @FXML
    private ListView<Log> imageHistory;

    /**
     * An index in the imageLog of and ImageFile object to revert to
     */
    private int indexToRevert;

    /**
     * This Button is what the user presses to delete a tag on an ImageFile
     */
    @FXML
    private Button processChanges;

    /**
     * The regex format followed by any file name that contains tags
     */
    private String regex_format = "^[\\W\\D\\w\\d\\s]+((\\s)(@[^.\\\\/:*?\"<>|]*[^\\s]))+\\.((jpg)|(png)|(gif)|(jpeg))";

    /**
     * ListView representing the ImageFile objects in the listOfFavourites in a Favourite object
     */
    @FXML
    private ListView<ImageFile> favouriteListView;

    /**
     * A new favourite object
     */
    private static Favourite favourite;

    /**
     * Constructs a new GUIController and instantiates the instance variables.
     */
    public GUIController() {
        tagManager = new TagManager();
        directoryManager = new DirectoryManager();
        directoryView = new TreeView<>();
        currentImage = null;
        indexToRevert = 0;
        favourite = new Favourite();
    }


    /**
     * Loads up the TagManager and DirectoryManager, and initializes.
     *
     * @param location  The location used to find a path for the root object, or null.
     * @param resources Resource used to localize root, or null.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            tagManager.loadSavedTags();
            directoryManager.loadSavedDirectories();
            favourite.loadSavedFavourites();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
        }
        ObservableList<ImageFile> observableList1 = FXCollections.observableArrayList(favourite.getListOfFavourites());
        favouriteListView.setItems(observableList1);
        ObservableList<Tag> observableList = FXCollections.observableArrayList(tagManager.getListOfTags());
        currentTags.setItems(observableList);
        currentTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        imageTags.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Listens for a key being and pressed and performs any of 3 different actions.
     * If the key that is pressed is BACKSPACE (or DELETE on MACOS) the method deletes the selected
     * keys from the listOfTags in the TagManager object and the currentTags ListView. If the key pressed
     * if ENTER, and there is an ImageFile currently being displayed, this method adds the selected tags from
     * currentTags onto the ImageFile object currentImage and onto the imageTags ListView. If sny other key
     * is pressed, this method does nothing
     *
     * @param e A KeyEvent representing a key being pressed
     */
    @FXML
    @SuppressWarnings("unchecked")
    public void handleKeyPress(KeyEvent e) {
        ObservableList<Tag> currentItems = currentTags.getSelectionModel().getSelectedItems();
        ArrayList<Tag> selectedTags = new ArrayList<>(currentItems);
        if ((currentTags != null) && (currentImage != null)) {
            if ((e.getCode() == KeyCode.BACK_SPACE) || (e.getCode() == KeyCode.DELETE))
                removeTagFromImageTags(selectedTags);
            if (e.getCode() == KeyCode.ENTER && currentImage != null) {
                currentImage.addTag(selectedTags);
                imageName.setText("Current Name: " + currentImage.imageFile.getName());
                updateCurrentTags(currentImage);
                updateImageHistory(currentImage);
                updateFavouritesListView(favourite);
                directoryView.refresh();
            }
        }
    }

    /**
     * When the button "Add Tag to Image" is pressed, it will add the selected Tag(s) to the current ImageFile.
     *
     * @param e an ActionEvent representing a button being pressed.
     */
    @FXML
    private void addTagToImage(ActionEvent e) {
        ObservableList<Tag> currentItems = currentTags.getSelectionModel().getSelectedItems();
        ArrayList<Tag> selectedTags = new ArrayList<>(currentItems);
        if ((currentTags != null) && (currentImage != null) && selectedTags.size() != 0) {
            currentImage.addTag(selectedTags);
            updateCurrentTags(currentImage);
            updateImageHistory(currentImage);
            updateFavouritesListView(favourite);
            directoryView.refresh();
        }
    }

    /**
     * When the button "Delete Tag" is pressed, it will remove the selected tag(s) from the ListView of all available
     * tags.
     *
     * @param e an ActionEvent representing a button being pressed.
     */
    @FXML
    private void deleteTag(ActionEvent e) {
        ObservableList<Tag> currentItems = currentTags.getSelectionModel().getSelectedItems();
        ArrayList<Tag> selectedTags = new ArrayList<>(currentItems);
        if ((currentTags != null)) {
            removeTagFromImageTags(selectedTags);
        }
    }

    /**
     * Deletes the selected tags from imageTags and the ImageFile object, if
     * the BACKSPACE (or DELETE on MACOS) keys is pressed while an item is
     * selected in the imageTags ListView. Does nothing otherwise.
     *
     * @param e A KeyEvent representing a key being pressed.
     */
    @FXML
    public void deleteTagFromImage(KeyEvent e) {
        ArrayList<Tag> tagsToDelete = new ArrayList<>();
        tagsToDelete.addAll(imageTags.getSelectionModel().getSelectedItems());
        if ((e.getCode() == KeyCode.BACK_SPACE) || (e.getCode() == KeyCode.DELETE)
                && currentImage != null && tagsToDelete.size() != 0) {
            currentImage.deleteTag(tagsToDelete);
            updateCurrentTags(currentImage);
            updateImageHistory(currentImage);
            updateFavouritesListView(favourite);
            imageName.setText("Current Name: " + currentImage.imageFile.getName());
            updateCurrentTags(currentImage);
            updateImageHistory(currentImage);
            directoryView.refresh();
        }

    }

    /**
     * Removes selected tags from the currentTags ListView and the TagManager object and
     * updates the currentTags ListView
     * <p>
     * //@param currentIndicies The selected indices presenting the items
     * from the currentTags ListView to delete
     */
    private void removeTagFromImageTags(ArrayList<Tag> currentTags) {
        tagManager.removeTag(currentTags);
        imageName.setText("Current Name: " + currentImage.imageFile.getName());
        updateObservableList(tagManager);
        directoryView.refresh();
    }

    /**
     * Listens for a key being pressed, if it is ENTER, it is added to the currentTags
     * ListView
     *
     * @param e An KeyEvent representing a key being pressed
     */
    @FXML
    public void handleAddTag(KeyEvent e) {
        if ((e.getCode() == KeyCode.ENTER) && (currentImage != null)) {
            addTagToImageTags();
        }
    }

    /**
     * Adds whatever String is currently in the TextField addTag to the
     * TagManager object and updates the currentTags ListView to reflect
     * the change
     */
    private void addTagToImageTags() {
        String tagName = addTag.getText();
        Tag tag = new Tag(tagName);
        if (tagManager.addTag(tag)) {
            updateObservableList(tagManager);
            imageName.setText("Current Name: " + currentImage.imageFile.getName());
        } else
            showInvalidAlert();
        addTag.setText("");
    }

    /**
     * Opens a pop up window, when the user enters an invalid tag, that alerts the user of acceptable and
     * unacceptable tags.
     */
    private void showInvalidAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText("Input must follow filename conventions (the characters <, >, :, \", /, \\, |, ?, * " +
                "are forbidden) \nBlank inputs, duplicates, names that include @ or inputs that exceed 20 characters" +
                " are not allowed.");

        alert.showAndWait();
    }

    /**
     * Opens the window to select directories when the button "Load Directory" is pressed.
     *
     * @param e An ActionEvent representing the Load Directory button being pressed
     */
    @FXML
    public void loadDirectoryAction(ActionEvent e) {
        /*
         * Adapted from a YouTube video by ProgrammingKnowledge on 20171115
         * video found here: https://www.youtube.com/watch?v=hNz8Xf4tMI4
         */
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDirectory = dc.showDialog(null);
        if (selectedDirectory != null) {
            Directory directory = buildDirectory(selectedDirectory.getAbsolutePath());
            TreeItem<File> root = createTreeItem(directory, 1);
            directoryView.setRoot(root);
            directoryView.setEditable(true);
            ;
            directoryView.setCellFactory(param -> new TreeCell<File>() {
                @Override
                public void updateItem(File file, boolean empty) {
                    // Images from https://www.flaticon.com/authors/smashicons
                    Image folderIcon = new Image(getClass().getResourceAsStream("/images/icons/folder.png"));
                    Image pictureIcon = new Image(getClass().getResourceAsStream("/images/icons/picture.png"));
                    ImageView imgView = null;
                    super.updateItem(file, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (file instanceof ImageFile) {
                            imgView = new ImageView(pictureIcon);
                            setText(((ImageFile) file).getImageFile().getName());
                        } else if (file instanceof Directory) {
                            imgView = new ImageView(folderIcon);
                            setText(file.getName());
                        }
                        setDisclosureNode(null);
                        setGraphic(imgView);
                    }
                }
            });
            currentDirectory = directory;
        }
    }

    private TreeItem<File> createTreeItem(Directory directory, int depth) {
        // Sorts files alphabetically so files appear in the directory in order.
        ArrayList<File> files = directory.getFiles();
        // Code adapted from https://stackoverflow.com/questions/1814095/sorting-an-arraylist-of-objects-using-a-custom-sorting-order
        Comparator<File> fileComparator = new Comparator<File>() {
            @Override
            public int compare(final File object1, final File object2) {
                return object1.getName().compareTo(object2.getName());
            }
        };
        files.sort(fileComparator);

        if (files.isEmpty()) {
            return new TreeItem<>(directory);
        } else {
            while (depth >= 0) {
                TreeItem<File> treeItem = new TreeItem<>(directory);
                for (File file : files) {
                    if (file instanceof ImageFile) {
                        TreeItem imageFile = new TreeItem<>(file);
                        treeItem.getChildren().add(imageFile);
                    } else {
                        Directory dir = buildDirectory(file.getAbsolutePath());
                        TreeItem<File> fileTreeItem = createTreeItem(dir, depth - 1);
                        treeItem.getChildren().add(fileTreeItem);
                    }
                }
                return treeItem;
            }
        }
        return new TreeItem<>(directory);
    }

    /**
     * Helper method that takes in a path given as a String. Checks if there is already an existing Directory with
     * the given path, and returns it if there is. If there isn't, it will create a new Directory object, add
     * it to the directoryManager and returns it.
     *
     * @param selectedPath String of the path we're looking for/building.
     * @return a Directory object with the given path.
     */
    private Directory buildDirectory(String selectedPath) {
        if (directoryManager.directoryManagerContains(selectedPath)) {
            return directoryManager.getDirectory(selectedPath);
        } else {
            Directory directory = new Directory(selectedPath);
            directory.setFiles(getFiles(directory));
            directoryManager.addDirectory(directory);
            return directory;
        }
    }

    /**
     * Returns an ArrayList of File objects that have the correct extensions and are in the given selectedDirectory
     *
     * @param selectedDirectory the directory we're finding the Files
     * @return File objects with the given accepted extensions.
     */
    private ArrayList<File> getFiles(File selectedDirectory) {
        /*
         *Code adapted from a post by Deron Eriksson
         *accessed on 20171116
         * http://www.avajava.com/tutorials/lessons/how-do-i-use-a-
         * filenamefilter-to-display-a-subset-of-files-in-a-directory.html
         */
        File[] allFiles = selectedDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(".")) {
                    return false;
                } else if (new File(dir, name).isDirectory()) {
                    return true;
                } else if (name.toLowerCase().endsWith(".jpg")) {
                    return true;
                } else if (name.toLowerCase().endsWith(".jpeg")) {
                    return true;
                } else if (name.toLowerCase().endsWith(".png")) {
                    return true;
                } else if (name.toLowerCase().endsWith(".gif")) {
                    return true;
                }
                return false;
            }
        });
        ArrayList<File> files = new ArrayList<>();
        if (allFiles != null) {
            for (File file : allFiles) {
                if (!file.isDirectory()) {
                    File imgFile = new ImageFile(file.getPath());
                    /*
                    Checks if a file name matches the regular expression for a file name
                    that contains tags and attempts to auto-tag it
                     */
                    if (((ImageFile) imgFile).getImageFile().getName().toLowerCase().matches(regex_format)) {
                        autoTag((ImageFile) imgFile, tagManager);
                    }
                    files.add(imgFile);
                } else {
                    File directory;
                    String selectedPath = file.getAbsolutePath();
                    if (directoryManager.directoryManagerContains(selectedPath))
                        directory = directoryManager.getDirectory(selectedPath);
                    else
                        directory = new Directory(selectedPath);
                    files.add(directory);
                }
            }
        }
        return files;
    }

    /**
     * Takes in an ImageFile object and TagManager object. Extracts Strings that represent Tags in the ImageFile name
     * and converts them into Tag objects, adds them to the TagManager and actually tags the ImageFile object with
     * the newly created Tag objects.
     *
     * @param imgFil     ImageFile we're tagging and extracting tags from.
     * @param tagManager TagManager object that we're storing the new Tag objects in.
     */
    private void autoTag(ImageFile imgFil, TagManager tagManager) {
        int indexOfPeriod = imgFil.getImageFile().getName().lastIndexOf('.');
        String[] extractedTags = imgFil.getImageFile().getName().substring(0, indexOfPeriod).split(" @");
        String newBaseName = extractedTags[0].trim();
        imgFil.setBaseName(newBaseName);
        int i = 1;
        ArrayList<Tag> tagToAdd = new ArrayList<>();
        while (i < extractedTags.length) {
            Tag newTag = new Tag(extractedTags[i].trim());
            tagManager.addTag(newTag);
            if (tagManager.getListOfTags().contains(newTag)) {
                tagToAdd.add(newTag);
            }
            imgFil.setCurrentTags(tagToAdd);
            i++;
        }
        updateObservableList(tagManager);
    }

    /**
     * Displays the image file selected from the ListView directoryView on the ImageView imageDisplay.
     * Throws a MalformedURLException if the URL from the URI of the ImageFile imgFile is malformed.
     *
     * @param e A event of the mouse being clicked
     * @throws MalformedURLException If the URL form of the URI is malformed
     */
    public void selectDirectoryView(MouseEvent e) throws MalformedURLException {
        directoryView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                TreeItem<File> selectedTreeItem = directoryView.getSelectionModel().getSelectedItem();
                File selectedFile = selectedTreeItem.getValue();
                boolean isSelected = false;
                if (directoryView.getSelectionModel().getSelectedItem() != null) {
                    isSelected = true;
                }
                if (isSelected) {
                    if (selectedFile instanceof ImageFile) {
                        currentImage = (ImageFile) selectedFile;
                        imageName.setText("Current Name: " + currentImage.imageFile.getName());
                        imageTags.getItems().clear();
                        updateCurrentTags(currentImage);
                        updateImageHistory(currentImage);
                        /*
                         * Code adapted from Ken Alger on 20151017 from a blog post titled
                         * "Setting selected image file to Imageview in JavaFX"
                         * URL: https://teamtreehouse.com/community/setting-selected-image-file-to-imageview-in-javafx
                         */
                        String imgPath = null;
                        try {
                            imgPath = currentImage.getImageFile().toURI().toURL().toString();
                        } catch (MalformedURLException e) {
                            System.out.println(e.getMessage());
                        }
                        Image img = new Image(imgPath, 550, 320, true, true);
                        centerImage(img);
                        imageDisplay.setImage(img);
                    } else {
                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                            if (mouseEvent.getClickCount() == 2) {
                                currentDirectory = (Directory) selectedFile;
                                for (TreeItem<File> treeItem : selectedTreeItem.getChildren()) {
                                    File treeItemFile = treeItem.getValue();
                                    if (treeItem.getChildren().isEmpty()) {
                                        Directory directory = buildDirectory(treeItemFile.getAbsolutePath());
                                        TreeItem<File> newItem = createTreeItem(directory, 1);
                                        treeItem.getChildren().setAll(newItem.getChildren());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Centers an Image in an ImageView
     *
     * @param img The Image to be centered
     */
    private void centerImage(Image img) {
        /*
        Code from a Stackoverflow post by user Trichetriche 20150930
        URL:https://stackoverflow.com/questions/32781362/centering-an-image-in-an-imageview
         */
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageDisplay.getFitWidth() / img.getWidth();
            double ratioY = imageDisplay.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageDisplay.setX((imageDisplay.getFitWidth() - w) / 2);
            imageDisplay.setY((imageDisplay.getFitHeight() - h) / 2);

        }

    }

    /**
     * Gets the index of the Log object the user wants to revert to for a
     * given ImageFile. Does nothing if no ImageFile is currrently displayed
     *
     * @param e MouseEvent representing an item in the imageHistory ListView
     *          being clicked
     */
    @FXML
    public void indexTagChanges(MouseEvent e) {
        if (currentImage != null) {
            if (currentImage.getImageLog().size() > 0) {
                indexToRevert = imageHistory.getSelectionModel().getSelectedIndex();
            }
        }
    }

    /**
     * Reverts the tags on an ImageFile based on the index in ImageLog
     * representing a Log object that kept track of the change the user wants
     * to revert to.
     *
     * @param e ActionEvent representing the revertChange button being clicked
     */
    @FXML
    public void revertTags(ActionEvent e) {
        if (currentImage != null) {
            if (currentImage.getImageLog().size() != 0) {
                currentImage.revert(imageHistory.getSelectionModel().getSelectedItem());
                updateFavouritesListView(favourite);
                imageName.setText("Current Name: " + currentImage.imageFile.getName());
                updateImageHistory(currentImage);
                updateCurrentTags(currentImage);
            }
        }
    }

    /**
     * Returns the TagManager object
     *
     * @return The TagManager object
     */
    public static TagManager getTagManager() {
        return tagManager;
    }

    /**
     * Returns the TagManager object
     *
     * @return The TagManager object
     */
    public static Favourite getFavourites() {
        return favourite;
    }

    /**
     * Returns the DirectoryManager object
     *
     * @return The DirectoryManager object
     */
    public static DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    /**
     * Deletes a tag from the ImageFile currentImage and updates the
     * ListView imageTags
     *
     * @param e ActionEvent representing the Delete Selected Tag(s) button being
     *          clicked
     */
    @FXML
    private void deleteCurrentTags(ActionEvent e) {
        ArrayList<Tag> tagsToDelete = new ArrayList<>();
        tagsToDelete.addAll(imageTags.getSelectionModel().getSelectedItems());
        if (currentImage != null && tagsToDelete.size() != 0) {
            currentImage.deleteTag(tagsToDelete);
            imageName.setText("Current Name: " + currentImage.imageFile.getName());
            updateCurrentTags(currentImage);
            updateImageHistory(currentImage);
            updateFavouritesListView(favourite);
        }
    }

    /**
     * When the button "Add" is pressed, the tag entered in the text box will be added to the ListView of all
     * available tags
     *
     * @param e an ActionEvent representing a button being pressed.
     */
    @FXML
    private void addTagToList(ActionEvent e) {
        if (currentImage != null) {
            addTagToImageTags();
        }
    }

    /**
     * Moves an ImageFile from one Directory to another, updating the Directory object's
     * imageFiles as well as updating the directoryView ListView
     *
     * @param e ActionEvent representing the Move Image button being pressed
     */
    @FXML
    private void moveImageButton(ActionEvent e) {
        if (currentImage != null) {
            DirectoryChooser dc = new DirectoryChooser();
            File selectedDirectory = dc.showDialog(null);
            if (selectedDirectory != null) {
                Directory targetDirectory;
                if (directoryManager.directoryManagerContains(selectedDirectory.getPath()))
                    targetDirectory = directoryManager.getDirectory(selectedDirectory.getPath());
                else {
                    targetDirectory = new Directory(selectedDirectory.getPath());
                    targetDirectory.setFiles(getFiles(selectedDirectory));
                    directoryManager.addDirectory(targetDirectory);
                }
                File dest = new File(targetDirectory, currentImage.imageFile.getName());
                directoryManager.moveImageFile(currentImage, currentDirectory, targetDirectory, dest);
            }
        }
    }

    /**
     * Updates the imageTags ListView based on an ImageFile currImage
     *
     * @param currImage ImageFile to display the tags for
     */
    private void updateCurrentTags(ImageFile currImage) {
        ObservableList<Tag> observableList = FXCollections.observableArrayList(currImage.getCurrentTags());
        imageTags.setItems(observableList);
        imageTags.refresh();
    }

    /**
     * Updates the imageHistory ListView based on an ImageFile currImage
     *
     * @param currImage ImageFile to display the imageLog history for
     */
    private void updateImageHistory(ImageFile currImage) {
        ObservableList<Log> observableList = FXCollections.observableArrayList();
        observableList.addAll(currImage.getImageLog());
        imageHistory.setItems(observableList);
        imageHistory.refresh();
    }

    /**
     * Updates the currentTags ListView based on an TagManager tagManager
     *
     * @param tagManager TagManager holding the tags to display
     */
    private void updateObservableList(TagManager tagManager) {
        ObservableList<Tag> observableList = FXCollections.observableArrayList(tagManager.getListOfTags());
        currentTags.setItems(observableList);
        currentTags.refresh();
    }

    /**
     * When the button "View Log" is pressed, it will open up the Master Log, with all the Tag changes to all the
     * ImageFiles altered, in the user's default text editor.
     *
     * @param e an ActionEvent representing a button being pressed.
     * @throws IOException when there's an output error with the text editor.
     */
    @FXML
    private void openMasterLog(ActionEvent e) throws IOException {
        File log = new File("../phase2/config/log.txt");
        java.awt.Desktop.getDesktop().edit(log);
    }

    /**
     * Updates the ListView favouriteListView based on the Favourite favourite
     */
    @FXML
    private void updateFavouritesListView(Favourite favourite) {
        ObservableList<ImageFile> observableList = FXCollections.observableArrayList();
        observableList.addAll(favourite.getListOfFavourites());
        assert favouriteListView != null;
        favouriteListView.setItems(observableList);
        favouriteListView.refresh();
    }

    /**
     * Adds a ImageFile object to the ListView favouriteListView and the listOfFavourites ArrayList
     * for the Favourite object favourite
     *
     * @param e An ActionEvent representing the "Favourite This Image" button being pressed
     */
    @FXML
    public void addFavouriteButton(ActionEvent e) {
        if (currentImage != null) {
            favourite.addFavourite(currentImage);
            updateFavouritesListView(favourite);
        }
    }

    /**
     * Removes a ImageFile object from the ListView favouriteListView and the listOfFavourites ArrayList
     * for the Favourite object favourite
     *
     * @param e An ActionEvent representing the "Remove from Favourites" button being pressed
     */
    @FXML
    public void deleteFavouriteButton(ActionEvent e) {
        ImageFile favouriteImage = favouriteListView.getSelectionModel().getSelectedItem();
        if ((favouriteImage != null)) {
            favourite.removeFavourite(favouriteImage);
            updateFavouritesListView(favourite);
        }
    }

    /**
     * Displays the selected ImageFile object from the favouriteListView ListView
     *
     * @param e MouseEvent representing an ImageFile from the favouriteListView being selected
     * @throws MalformedURLException if the URL of the ImageFile is malformed
     */
    @FXML
    public void selectFavouriteView(MouseEvent e) throws MalformedURLException {
        /*
        * Code adapted from Ken Alger on 20151017 from a blog post titled
        * "Setting selected image file to Imageview in JavaFX"
        * URL: https://teamtreehouse.com/community/setting-selected-image-file-to-imageview-in-javafx
         */
        ImageFile imgFile = favouriteListView.getSelectionModel().getSelectedItem();
        if (imgFile != null) {
            currentImage = imgFile;
            imageTags.getItems().clear();
            imageTags.getItems().addAll(currentImage.getCurrentTags());
            imageHistory.getItems().clear();
            updateImageHistory(currentImage);
            String imgPath = currentImage.getImageFile().toURI().toURL().toString();
            Image img = new Image(imgPath, 550, 320, true, true);
            centerImage(img);
            imageDisplay.setImage(img);
        }
    }
}

