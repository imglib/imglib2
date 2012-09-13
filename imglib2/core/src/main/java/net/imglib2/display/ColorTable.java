/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.imglib2.display;

/**
 *
 * @author Aivar Grislis
 */
public interface ColorTable {
	//TODO ARG What about C,M,Y,K?
	public static final int RED   = 0;
	public static final int GREEN = 1;
	public static final int BLUE  = 2;
	public static final int ALPHA = 3;

	public int lookupARGB(double min, double max, double value);

	/**
	 * Gets the number of color components in the table (typically 3 for RGB or
	 * 4 for RGBA).
	 */
	public int getComponentCount();

	/**
	 * Gets the number of elements for each color component in the table.
	 */
	public int getLength();

	/**
	 * Gets an individual value from the color table.
	 *
	 * @param comp The color component to query.
	 * @param bin The index into the color table.
	 * @return The value of the table at the specified position.
	 */
	public int get(final int comp, final int bin);

	/**
	 * Gets an individual value from a color table with given number of bins.
	 *
	 * @param c The color component to query.
	 * @param bins The total number of bins.
	 * @param bin The index into the color table.
	 * @return The value of the table at the specified position.
	 */
	public int getResampled(final int comp, final int bins, final int bin);
}
