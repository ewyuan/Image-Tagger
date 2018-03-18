package models;

import views.Main;

import java.io.*;
import java.util.ArrayList;

/**
 * Favourite class to keep track of all of the user's favourite ImageFile objects.
 *
 * @author Khori
 * @version 1.0
 */
public class Favourite implements Serializable {

    /**
     * An ArrayList of all the images the user has designated as a Favourite
     */
    private ArrayList<ImageFile> listOfFavourites;

    /**
     * Creates a new Favourites object that will never be used, since
     * no object is ever needed to access the Favourite class' methods
     */
    public Favourite() {
        this.listOfFavourites = new ArrayList<>();
    }

    /**
     * Adds the specified ImageFile object to the Favourite class'
     * listOfFavourites
     *
     * @param imgFile ImageFile to add to the listOfFavourites
     */
    public void addFavourite(ImageFile imgFile) {
        if (!this.listOfFavourites.contains(imgFile)) {
            this.listOfFavourites.add(imgFile);
        }
    }

    /**
     * Removes the specified ImageFile object from the Favourite class'
     * listOfFavourites
     *
     * @param imgFile The ImageFile object to be removed from the listOfFavourites
     */
    public void removeFavourite(ImageFile imgFile) {
        if (this.listOfFavourites.contains(imgFile)) {
            this.listOfFavourites.remove(imgFile);
        }
    }

    /**
     * Returns the Favourite class' listOfFavourites ArrayList
     *
     * @return The ArrayList of Favourite ImageFile objects
     */
    public ArrayList<ImageFile> getListOfFavourites() {
        return this.listOfFavourites;
    }

    /**
     * Loads the serialized favourite file, and restores the last state of Favourite ArrayList.
     *
     * @throws IOException            if IOException occurs, it's then thrown
     * @throws ClassNotFoundException if ClassNotFoundException occurs, it's then thrown
     */
    @SuppressWarnings("unchecked")
    public void loadSavedFavourites() throws IOException, ClassNotFoundException {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        File fileExists = new File(Main.serializedFavourites);
        if (fileExists.isFile()) {
            FileInputStream fileIn = new FileInputStream(fileExists.getPath());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            listOfFavourites = ((ArrayList<ImageFile>) in.readObject());
            in.close();
            fileIn.close();
        }
    }

    /**
     * Writes to the serialized Favourite file, saving the current state of Favourite.
     */
    public void writeSavedFavourites() throws IOException {
        // Code adapted from https://www.tutorialspoint.com/java/java_serialization.htm
        // 2017-11-19
        FileOutputStream fileOut = new FileOutputStream(Main.serializedFavourites);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(listOfFavourites);
        out.close();
        fileOut.close();
    }
}
