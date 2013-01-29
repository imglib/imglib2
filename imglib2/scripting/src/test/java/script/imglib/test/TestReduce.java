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

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.img.Img;
import net.imglib2.script.analysis.ImgMax;
import net.imglib2.script.analysis.ImgMean;
import net.imglib2.script.analysis.ImgStdDev;
import net.imglib2.script.analysis.ImgSum;
import net.imglib2.script.img.FloatImage;
import net.imglib2.script.math.Add;
import net.imglib2.script.math.Compute;
import net.imglib2.script.math.Pow;
import net.imglib2.script.math.Random;
import net.imglib2.script.math.Subtract;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * TODO
 *
 */
public class TestReduce {

	static private final double now() { 
		return System.nanoTime() / 1000.0;
	}
	
	static private double reduceMax(final IterableRealInterval<? extends RealType<?>> img) {
		final double t0 = now();
		final RealCursor<? extends RealType<?>> c = img.cursor();
		c.fwd();
		double r = c.get().getRealDouble();
		while (c.hasNext()) {
			c.fwd();
			r = Math.max(r, c.get().getRealDouble());
		}
		final double t1 = now() - t0;
		//System.out.println("direct max: " + r);
		return t1;
	}
	
	static private double reduceMaxScript(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		final double t0 = now();
		double r = new ImgMax(img).doubleValue();
		final double t1 = now() - t0;
		//System.out.println("script max: " + r);
		return t1;
	}
	
	static private double reduceStdDev(final IterableRealInterval<? extends RealType<?>> img) {
		final double t0 = now();
		final RealCursor<? extends RealType<?>> c = img.cursor();
		// Mean:
		double sum = 0;
		while (c.hasNext()) {
			c.fwd();
			sum += c.get().getRealDouble();
		}
		double mean = sum / img.size();
		
		// StdDev:
		c.reset();
		double r = 0;
		while (c.hasNext()) {
			c.fwd();
			r += Math.pow(c.get().getRealDouble() - mean, 2);
		}
		double stdDev = r / (img.size() -1);
		double t1 = now() - t0;
		//System.out.println("direct mean, StdDev: " + mean + ", " + stdDev);
		return t1;
	}
	
	static private double reduceStdDevScript(final IterableRealInterval<? extends RealType<?>> img) {
		final double t0 = now();
		try {
			double mean = new ImgMean(img).doubleValue();
			double stdDev = new ImgStdDev(img, mean).doubleValue();
			double t1 = now() - t0;
			//System.out.println("script mean, StdDev: " + mean + ", " + stdDev);
			return t1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	static private double reduceStdDevScript2(final IterableRealInterval<? extends RealType<?>> img) {
		final double t0 = now();
		try {
			double stdDev = new ImgSum(new Pow(new Subtract(img, new ImgMean(img)), 2)).doubleValue() / (img.size() -1);
			double t1 = now() - t0;
			//System.out.println("script other StdDev: " + stdDev);
			return t1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	static public final void main(String[] args) {
		
		try {
			Img<FloatType> img = Compute.inFloats(new Add(new FloatImage(new long[]{3, 3}), new Random(123)));
		
			for (int i=0; i<5; ++i) {
				System.out.println("Round " + (i + 1));
				System.out.println("  reduceMax:       " + reduceMax(img));
				System.out.println("  reduceMaxScript: " + reduceMaxScript(img));
				System.out.println("  stdDev        : " + reduceStdDev(img));
				System.out.println("  stdDevScript  : " + reduceStdDevScript(img));
				System.out.println("  stdDevScript 2: " + reduceStdDevScript2(img));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
