package models;

import views.Main;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;

/**
 * The ImageFile class implements Serializable. ImageFile constructs a new image and stores all the previous tags in
 * Log objects inside the ArrayList imageLog, and stores the new tags in an ArrayList called currentTags.
 *
 * @author Cici Zhang
 * @version 2.0
 */
public class ImageFile extends File implements Serializable {

    /**
     * A File for this ImageFile to manipulate.
     */
    public File imageFile;

    /**
     * ArrayList of Log objects that tracks the past and current Tags of this ImageFile object.
     */
    private ArrayList<Log> imageLog;

    /**
     * ArrayList of Tag objects that this ImageFile object is currently tagged with.
     */
    private ArrayList<Tag> currentTags;

    /**
     * Original name of this ImageFile without any Tag objects attached to it.
     */
    private String baseName;

    /**
     * Extension of this ImageFile (ie. '.jpg', '.png', '.gif', etc.)
     */
    private final String extension;

    /**
     * Constructs a new image with imageLog that keeps track of all past changes of Tags, currentTags that keeps
     * track of all the current Tags on the image. And takes in a parameter called path which is the path
     * of the image on the computer.
     *
     * @param path The path of the image on the computer. Calls to the super method in the File class.
     * @see Tag
     * @see Log
     */
    public ImageFile(String path) {
        super(path);
        this.imageFile = new File(path);
        this.imageLog = new ArrayList<>();
        this.currentTags = new ArrayList<>();
        this.baseName = this.imageFile.getName().substring(0, this.imageFile.getName().lastIndexOf('.'));
        this.extension = this.imageFile.getName().substring(this.imageFile.getName().lastIndexOf('.'));
    }

    /**
     * Adds an ArrayList of additional Tag objects to currentTags. It checks if there is already an existing tag
     * inside newTags, if so it will delete it from newTags. After checking all new tags, it will update the imageLog
     * and add the currentTags with newTags
     *
     * @param additionalTags An ArrayList of Tag objects that we want to add to the already existing currentTags
     * @see Tag
     * @see Log
     */
    public void addTag(ArrayList<Tag> additionalTags) {
        ArrayList<Tag> nonRepeatingInputs = new ArrayList<>();
        for (int i = 0; i < additionalTags.size(); i++) {
            boolean exists = true;
            if (nonRepeatingInputs.size() != 0) {
                for (int j = 0; j < nonRepeatingInputs.size(); j++)
                    if (!additionalTags.get(i).getName().equals(nonRepeatingInputs.get(j).getName()))
                        exists = false;
            } else nonRepeatingInputs.add(additionalTags.get(i));
            if (!exists) nonRepeatingInputs.add(additionalTags.get(i));
        }
        ArrayList<Tag> repeats = new ArrayList<>();
        for (Tag tag : nonRepeatingInputs) {
            if (this.currentTags.contains(tag)) {
                repeats.add(tag);
            }
        }
        nonRepeatingInputs.removeAll(repeats);

        ArrayList<Tag> newTags = new ArrayList<>(this.currentTags);
        if (nonRepeatingInputs.size() != 0) {
            newTags.addAll(nonRepeatingInputs);
            renameFile(newTags);
        }
    }

    /**
     * Deletes an ArrayList of Tags the user wants to delete from currentTags, and documents the change in the
     * imageLog.
     *
     * @param deletedTags An ArrayList of tags the user wants to delete from the currentTags
     * @see Log
     * @see Tag
     */
    public void deleteTag(ArrayList<Tag> deletedTags) {
        ArrayList<Tag> newTags = new ArrayList<>(this.currentTags);
        newTags.removeAll(deletedTags);
        renameFile(newTags);
    }

    /**
     * Renames this ImageFile to include the Tag Names in updatedTags.
     *
     * @param updatedTags ArrayList of Tag objects of which the names will be added to the current ImageFile name
     */
    private void renameFile(ArrayList<Tag> updatedTags) {
        ArrayList<Tag> oldTags = new ArrayList<>(currentTags);
        currentTags = updatedTags;
        String newName = generateNewName(updatedTags);
        ArrayList<Tag> newTags = new ArrayList<>(currentTags);
        Log newLog = new Log(this.imageFile.getName(), newName, oldTags, newTags);
        imageLog.add(newLog);
        newLog.writeToLogFile(new File(Main.logPath));
        File dest = new File(this.imageFile.getParent(), newName);
        boolean success = this.imageFile.renameTo(dest);
        if (success)
            this.imageFile = dest;
    }

    /**
     * Helper method for renameFile that generates what the new name of this ImageFile should be.
     *
     * @param updatedTags ArrayList of Tag objects of which the names will be added to current name of the ImageFile
     * @return String of the new name of this ImageFile
     */
    private String generateNewName(ArrayList<Tag> updatedTags) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < updatedTags.size()) {
            sb.append(" ").append(updatedTags.get(i).toString());
            i++;
        }
        return this.baseName + sb.toString() + this.extension;
    }

    /**
     * Returns a String representation of this ImageFile object using the file name.
     * This method overrides the toString method in the File class which creates a string
     * representation of an object using the file path instead.
     *
     * @return A String representation of the ImageFile object
     */
    @Override
    public String toString() {
        return this.imageFile.getName();
    }

    /**
     * Getter for the current Tags on this ImageFile.
     *
     * @return ArrayList of Tag objects that are currently associated with this ImageFile.
     */
    public ArrayList<Tag> getCurrentTags() {
        return currentTags;
    }

    /**
     * Reverts the set of tags on this ImageFile object to an older set
     * of tags and renames the file to represents these changes
     */
    public void revert(Log revertLog) {
        if (!revertLog.getOldTags().equals(this.currentTags)) {
            renameFile(revertLog.getOldTags());
        }
    }

    /**
     * Returns the imageLog for this ImageFile object
     *
     * @return the imageLog for this ImageFile object
     */
    public ArrayList<Log> getImageLog() {
        return this.imageLog;
    }

    /**
     * Returns the imageFile of this ImageFile object i.e. the actual
     * file this ImageFile object manipulates
     *
     * @return the imageFile for this ImageFile object
     */
    public File getImageFile() {
        return this.imageFile;
    }

    /**
     * Sets the imageFile for this ImageFile object i.e. this method
     * sets the actual file that this ImageFile object manipulates
     *
     * @param imageFile A new file for this ImageFile to manipulate
     */
    void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Setter for the ArrayList of current Tag objects.
     *
     * @param tagsToAdd new Tags we want to replace old tags with.
     */
    public void setCurrentTags(ArrayList<Tag> tagsToAdd) {
        this.currentTags.clear();
        this.currentTags.addAll(tagsToAdd);
    }

    /**
     * Returns the base name of the ImageFile object without Tags or extensions attached to it.
     *
     * @return base name of ImageFile
     */
    public String getBaseName() {
        return baseName;
    }

    /**
     * Setter for the base name of this ImageFile.
     *
     * @param newName new base name for this ImageFile that will replace the current one.
     */
    public void setBaseName(String newName) {
        this.baseName = newName;
    }

    /**
     * Returns the extension of the ImageFile object.
     *
     * @return extension of ImageFile
     */
    public String getExtension() {
        return this.extension;
    }
}



