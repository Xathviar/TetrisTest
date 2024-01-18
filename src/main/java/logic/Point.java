package logic;

import config.Constants;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * This Class stores if the Point is free and which Color it has
 */
@Getter
@Setter
public class Point {

    /**
     * This variable is used to store the status of whether the Point is free or not.
     */
    private boolean isFree;

    /**
     * The `color` variable represents the color of a point on the Tetris board.
     *
     * The `color` variable is an instance of the `Color` class, which is a Java abstract class used to encapsulate colors in the default sRGB color space or colors in arbitrary color
     * spaces identified by a {@link java.awt.color.ColorSpace}.
     *
     * You can use the `getColor()` method to retrieve the color value stored in the `color` variable.
     *
     * Example usage:
     *
     * ```java
     * Point point = new Point(true, Constants.selectedColor);
     * Color color = point.getColor();
     * ```
     */
    private Color color;

    /**
     * This class represents a point on a Tetris board. Each point can be marked as either free or occupied, and can have a color assigned to it.
     */
    public Point(boolean isFree, Color color) {
        this.isFree = isFree;
        this.color = color;
    }

    /**
     * Checks whether the Point is free or occupied.
     *
     * @return true if the Point is free, false if it is occupied
     */
    public boolean isFree() {
        return isFree;
    }

    @Override
    public String toString() {
        return isFree ? "O" : "X";
    }

    /**
     * Resets the current point to its default values.
     * The point is marked as free and its color is set to the background color.
     */
    public void resetPoint() {
        isFree = true;
        color = Constants.backgroundColor;
    }
}
