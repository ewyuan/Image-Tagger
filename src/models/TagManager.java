package models;

import java.util.ArrayList;
import java.io.*;
import views.*;

/**
 * TagManager is the class which stores the central repository of tags which is referred to by other
 * classes in the program.
 * <p>
 * If this class's list of tags contains a particular Tag, that Tag can be added to an Image.
 *
 * @author Art Zhuoer Xia
 * @version 1.2
 * @since 1.8
 */
public class TagManager implements Serializable {

    /**
     * ArrayList of the current tags that exist that could be added or deleted from images.
     */
    private ArrayList<Tag> listOfTags;

    /**
     * Constructs a new TagManager with a new ArrayList listOfTags.
     */
    public TagManager() {
        this.listOfTags = new ArrayList<>();
    }


    /**
     * Creates and adds a Tag object to TagManager's list of tags. The maximum length of a tag's name may not exceed
     * 20 characters, may not be blank, contain illegal filename characters, trailing or leading whitespaces or
     * multiple spaces. This function will automatically detect and remove whitespaces; in all other invalid cases,
     * the tag will not be added.
     * Regex adapted from StackOverflow users polygenelubricants, Alex_M
     * Regular Expressions found here:
     * https://stackoverflow.com/questions/2932392/java-how-to-replace-2-or-more-spaces-with-single-space-in-string-and-delete-lead
     * https://stackoverflow.com/questions/754307/regex-to-replace-characters-that-windows-doesnt-accept-in-a-filename
     *
     * @param tag Tag that's to be added to the listOfTags
     * @see Tag
     */
    public boolean addTag(Tag tag) {
        String tagName = tag.getName();
        if ((tagName.length() > 0) &&
                (tagName.length() <= 20) &&
                (tagName.matches("^[^.\\\\/:*?\"<>|]?[^\\\\/:*?\"@<>|]*")) &&
                (!listOfTags.contains(tag))) {
            listOfTags.add(tag);
            return true;
        } else return false;
    }

    /**
     * Checks for a Tag at index i and removes it from the list of tags if the size of the ArrayList is greater than i.
     *
     * @param tagsToRemove ArrayList of the tags to be removed from listOfTags.
     * @see Tag
     */
    public void removeTag(ArrayList<Tag> tagsToRemove) {
        for (Tag tag : tagsToRemove) {
            if (listOfTags.contains(tag)) {
                listOfTags.remove(tag);
            }
        }
    }

    /**
     * Returns TagManager's list of tags.
     *
     * @return listOfTags
     * @see Tag
     */
    public ArrayList<Tag> getListOfTags() {
        return listOfTags;
    }

    /**
     * Loads the serialized tag file, and restores the last state of TagManager.
     *
     * @throws IOException            if IOException occurs, it's then thrown
     * @throws ClassNotFoundException if ClassNotFoundException occurs, it's then thrown
     */
    @SuppressWarnings("unchecked")
    public void loadSavedTags() throws IOException, ClassNotFoundException {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        File fileExists = new File(Main.serializedTags);
        if (fileExists.isFile()) {
            FileInputStream fileIn = new FileInputStream(fileExists.getPath());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            listOfTags = ((ArrayList<Tag>) in.readObject());
            in.close();
            fileIn.close();
        }
    }

    /**
     * Writes to the serialized directory file, saving the current state of TagManager.
     */
    public void writeSavedTags() throws IOException {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        FileOutputStream fileOut = new FileOutputStream(Main.serializedTags);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(listOfTags);
        out.close();
        fileOut.close();
    }
}
