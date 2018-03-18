package models;

import java.io.Serializable;

/**
 * The Tag class constructs a Tag object based on the name given.
 *
 * @author Cici Zhang
 * @version 1.0
 */
public class Tag implements Serializable {

    /**
     * Name of the tag.
     */
    private String name;

    /**
     * Constructs a new Tag object with the given name inside the parameter.
     *
     * @param name The name of the Tag.
     */
    public Tag(String name) {
        this.name = name.replaceAll("^ +| +$|( )+", "$1");
    }

    /**
     * Overrides the existing toString method. String representation of the Tag.
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        return "@" + this.name;
    }

    /**
     * Getter for the name of the Tag.
     *
     * @return the name of the Tag.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Overrides the existing equals method. Compares the names of the Tags and returns true or false based on if
     * they're the same or not.
     *
     * @param other Tag object to compare to the current one
     * @return true if the names of the two Tag objects match, and false if not.
     */
    @Override
    public boolean equals(Object other) {
        return (other instanceof Tag) && this.name.equals(((Tag) other).name);
    }
}
