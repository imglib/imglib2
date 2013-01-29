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

package net.imglib2.algorithm.localization;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * A collection of utility methods for localization algorithms.
 * @author Jean-Yves Tinevez
 */
public class LocalizationUtils {

	private static final GaussianMultiDLM g = new GaussianMultiDLM();
	private static final Random ran = new Random();
	
	public static final <T extends RealType<T>> void addGaussianSpotToImage(Img<T> img, double[] params) {
		Cursor<T> lc = img.localizingCursor();
		double[] position = new double[img.numDimensions()];
		double val;
		T var = img.firstElement().createVariable();
		while (lc.hasNext()) {
			lc.fwd();
			position[0] = lc.getDoublePosition(0);
			position[1] = lc.getDoublePosition(1);
			val = g.val(position, params);
			var.setReal(val);
			lc.get().add(var);
		}
	}

	public static final <T extends RealType<T>> void addGaussianNoiseToImage(Img<T> img, double sigma_noise) {
		Cursor<T> lc = img.localizingCursor();
		double val;
		T var = img.firstElement().createVariable();
		while (lc.hasNext()) {
			lc.fwd();
			val = Math.max(0, sigma_noise * ran.nextGaussian());
			var.setReal(val);
			lc.get().add(var);
		}
	}

}
