package models;

import java.io.*;
import java.util.ArrayList;

import views.*;

/**
 * A manager class to manage all Directory objects.
 *
 * @author Khori Watson, Eric Yuan
 * @version 1.0
 */
public class DirectoryManager implements Serializable {

    /**
     * ArrayList of all the existing Directory objects.
     */
    private ArrayList<Directory> listOfDirectories;

    /**
     * Constructs a new DirectoryManager by creating an empty ArrayList to store Directory objects in.
     */
    public DirectoryManager() {
        listOfDirectories = new ArrayList<>();
    }

    /**
     * Adds a Directory object to the ArrayList listOfDirectories if it doesn't already exist.
     *
     * @param directory Directory object to add to listOfDirectories
     */
    public void addDirectory(Directory directory) {
        if (!(listOfDirectories.contains(directory)))
            listOfDirectories.add(directory);
    }

    /**
     * Contains method that checks if the given path in the parameter matches a path in the ArrayList of
     * Directory objects called listOfDirectories and returns a boolean.
     *
     * @param path the Path of the Directory passed as a String
     * @return true or false based on whether or not listOfDirectories contains the given path.
     */
    public boolean directoryManagerContains(String path) {
        for (Directory dir : listOfDirectories) {
            if (dir.getPath().equals(path))
                return true;
        }
        return false;
    }

    /**
     * Getter for a Directory with the given path if it's in the ArrayList of Directory objects called
     * listOfDirectories.
     *
     * @param path the Path of the Directory passed as a String
     * @return a Directory object if listOfDirectories contains the path passed inside the parameter.
     */
    public Directory getDirectory(String path) {
        for (Directory dir : listOfDirectories) {
            if (dir.getPath().equals(path))
                return dir;
        }
        return null;
    }

    /**
     * Getter for the ArrayList of Directory objects called listOfDirectories.
     *
     * @return the ArrayList listOfDirectories
     */
    public ArrayList<Directory> getListOfDirectories() {
        return listOfDirectories;
    }

    /**
     * Loads the serialized directory file, and restores the last state of DirectoryManager.
     *
     * @throws ClassNotFoundException if ClassNotFoundException occurs, it's then thrown
     */
    @SuppressWarnings("unchecked")
    public void loadSavedDirectories() throws ClassNotFoundException {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        File fileExists = new File(Main.serializedDirectories);
        try {
            if (fileExists.isFile()) {
                FileInputStream fileIn = new FileInputStream(fileExists.getPath());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                listOfDirectories = ((ArrayList<Directory>) in.readObject());
                in.close();
                fileIn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the serialized directory file, saving the current state of DirectoryManager.
     */
    public void writeSavedDirectories() {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        try {
            FileOutputStream fileOut = new FileOutputStream(Main.serializedDirectories);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(listOfDirectories);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves the given ImageFile object from the currentDirectory to the targetDirectory .
     *
     * @param imageFile        ImageFile object that we want to move.
     * @param currentDirectory the current Directory that this ImageFile is in
     * @param targetDirectory  the Directory we want to move this ImageFile to
     * @param dest             rename destination of this ImageFile
     */
    public void moveImageFile(ImageFile imageFile, Directory currentDirectory, Directory targetDirectory, File dest) {
        boolean success = imageFile.imageFile.renameTo(dest);
        if (success) {
            targetDirectory.addFile(imageFile);
            currentDirectory.removeFile(imageFile);
            imageFile.setImageFile(dest);
        }
    }
}

