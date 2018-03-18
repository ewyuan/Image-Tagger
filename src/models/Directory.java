package models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Directory object is an object that contains all ImageFile available in the directory path.
 *
 * @author Khori Watson, Eric Yuan
 * @version 1.0
 */
public class Directory extends File implements Serializable {

    /**
     * ArrayList of ImageFile objects in this Directory.
     */
    private ArrayList<File> files;

    /**
     * Constructs a Directory object from the path given in the parameter by calling the super constructor
     * in the File class.
     *
     * @param pathName path of the Directory
     */
    public Directory(String pathName) {
        super(pathName);
        files = new ArrayList<>();
    }

    /**
     * Getter for the ArrayList of ImageFile objects called files.
     *
     * @return the ArrayList files
     */
    public ArrayList<File> getFiles() {
        return files;
    }

    /**
     * Setter for the ArrayList of ImageFile object called files.
     *
     * @param files replaces existing ArrayList of ImageFile objects called files with the new files in parameter.
     */
    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    /**
     * Adds a file to Directory's ArrayList of ImageFiles, files.
     *
     * @param file an ImageFile that needs to be added
     */
    void addFile(File file) {
        files.add(file);
    }

    /**
     * Removes an file from Directory's ArrayList of ImageFiles, files.
     *
     * @param file an ImageFile that needs to be removed
     */
    void removeFile(File file) {
        files.remove(file);
    }

    /**
     * Overrides the existing equals method. Compares the paths of this DirectoryFile and other Object (if it is a
     * Directory) and returns true or false based on whether they're the same or not.
     *
     * @param other Object that we compare the current one to.
     * @return true if other is a Directory object and the paths are the same and false if not.
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Directory) && (getPath().equals(((Directory) other).getPath()));
    }

    /**
     * Overrides the existing toString method. Returns a String representation of the Directory, as the path name.
     *
     * @return A String representation of this Directory object.
     */
    @Override
    public String toString() {
        return super.getName();
    }
}
