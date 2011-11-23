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
}
