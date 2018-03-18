package models;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * A Log object is an object containing the previous and current tags
 * for an Image object. The information for each Log object created is written to a file
 * that stores a record of every tag change for every image ever done.
 *
 * @author Khori Watson
 * @version 1.0
 */
public class Log implements Serializable {

    /**
     * ArrayList of Tag objects that represent all the past Tags that have been on an Image object.
     */
    private final ArrayList<Tag> oldTags;

    /**
     * An ArrayList of Tag objects representing the new tags on an Image object after the
     * tags were changed
     */
    private final ArrayList<Tag> newTags;

    /**
     * A String representation of the file name from an ImageFile object before the
     * changes that this Log objects tracks were applied
     */
    private final String oldName;

    /**
     * A String representation of the file name from an ImageFile object after the changes
     * that this Log object tracks were applied
     */
    private final String newName;

    /**
     * Constructs a new Log object with the given oldName, newName, oldTags, and newTags on an ImageFile.
     *
     * @param oldName String of the previous name of the ImageFile
     * @param newName String of the new/current name of the ImageFile
     * @param oldTags ArrayList of the previous/old Tag objects on the ImageFile
     * @param newTags ArrayList of the new/current Tag objects on the ImageFile
     */
    public Log(String oldName, String newName, ArrayList<Tag> oldTags, ArrayList<Tag> newTags) {
        this.oldName = oldName;
        this.newName = newName;
        this.newTags = newTags;
        this.oldTags = oldTags;
    }

    /**
     * Returns an ArrayList of the last tags changes for an ImageFile object
     *
     * @return the oldTags ArrayList of this Log object
     */
    public ArrayList<Tag> getOldTags() {
        return this.oldTags;
    }

    /**
     * Returns a String representing the file name of an ImageFile before the changes
     * this Log object tracks were applied
     *
     * @return the String of the file name before the tag changes were made
     */
    public String getOldName() {
        return this.oldName;
    }

    /**
     * Returns an ArrayList of the most recent tags for an ImageFile object
     *
     * @return the newTags ArrayList of this Log object
     */
    public ArrayList<Tag> getNewTags() {
        return this.newTags;
    }

    /**
     * Returns a String representing the file name of an ImageFile after the changes
     * this Log object tracks were applied
     *
     * @return the String of the file name after the tag changes were made
     */
    public String getNewName() {
        return this.newName;
    }

    /**
     * Writes a copy of a String representation of a Log object to a file specified
     * by the private static final variable logFilePath. Throws an IOException if the
     * file specified by logFilePath cannot be found or does not exist
     */
    void writeToLogFile(File file) {
        String logString = this.generateLogString();
        /*
        *Code Adapted from a post by Chaitanya Singh
        *https://beginnersbook.com/2014/01/how-to-write-to-file-in-java-using-bufferedwriter/
        * 2017/11/13
         */
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(logString);
            bw.newLine();
            bw.newLine();
        } catch (IOException ex) {
            ex.printStackTrace();

        }

        /* Handles an IOException if the BufferedWriter fails to close*/
        try {
            assert bw != null;
            bw.close();
        } catch (IOException ex) {
            System.out.println("Error: cannot close BufferedWriter.");
        }
    }

    /**
     * Returns a String representation of this Log object
     *
     * @return A String representation of this Log object
     */
    @Override
    public String toString() {
        return this.oldName;
    }

    /**
     * Essentially a toString method for this Log object that returns the old name, new name and date it was modified.
     *
     * @return String representation of this Log.
     */
    private String generateLogString() {
        Date d = new Date();
        return "(" + oldName + "|" + newName + "," + d.toString() + ")";
    }
}
