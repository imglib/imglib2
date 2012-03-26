package net.imglib2.script.edit;

import java.util.ArrayList;

import net.imglib2.RealInterval;
import net.imglib2.img.Img;

/**
 * Extract the dimensions of an {@link Img}.
 */
@SuppressWarnings("serial")
public class Dimensions extends ArrayList<Long> {
	
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)(img.realMax(i) - img.realMin(i)) + 1);
		}
	}
	
	/**
	 * Extract the dimensions of {@param img} multiplied by the {@param factor}.
	 * @param img The interval to extract the dimensions of.
	 * @param factor The factor to multiply the dimensions.
	 */
	@SuppressWarnings("boxing")
	public Dimensions(final RealInterval img, final Number factor) {
		for (int i=0; i<img.numDimensions(); ++i) {
			add((long)((img.realMax(i) - img.realMin(i) + 1) * factor.doubleValue()));
		}
	}
}
