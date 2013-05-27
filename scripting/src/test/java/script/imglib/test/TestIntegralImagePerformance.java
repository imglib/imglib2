/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package script.imglib.test;

import ij.ImageJ;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import mpicbg.ij.integral.DoubleIntegralImage;
import net.imglib2.script.ImgLib;
import net.imglib2.script.algorithm.IntegralImage;
import net.imglib2.script.img.FloatImage;

/** Compare performance with Saalfeld's 2d {@link DoubleIntegralImage}.
 * 
 * Beating Saalfeld's by a factor of 5!
 *
 */
public class TestIntegralImagePerformance {

	public static void main(String[] args) {
		final int side = 2048;
		final float[] pix = new float[side * side];
		for (int i=0; i<pix.length; ++i) pix[i] = 1;
		final FloatProcessor fp = new FloatProcessor(side, side, pix, null);
		final FloatImage fii = new FloatImage(new long[]{side, side}, pix);
		
		for (int i=0; i<10; ++i) {
			long t0 = System.currentTimeMillis();
			DoubleIntegralImage dii = new DoubleIntegralImage(fp);
			FloatProcessor ip = new FloatProcessor(side, side);
			for (int y=0; y<ip.getHeight(); ++y){
				for (int x=0; x<ip.getWidth(); ++x){
					ip.setf(x, y, (float)dii.getDoubleSum(x,y));
				}
			}
			long t1 = System.currentTimeMillis();
			IntegralImage dig = new IntegralImage(fii); // also uses double as type
			long t2 = System.currentTimeMillis();
			System.out.println("Saalfeld'ss: " + (t2 - t1) + " ms -- imglib2.script: " + (t1 - t0) + " ms");
			if (i == 1) {
				new ImageJ();
				try {
					new ImagePlus("Saalfeld's", ip).show();
					ImgLib.show(dig, "Mine");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
