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

import ij.IJ;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.view.Extend;
import net.imglib2.script.view.ExtendMirrorDouble;
import net.imglib2.script.view.ExtendMirrorSingle;
import net.imglib2.script.view.ExtendPeriodic;
import net.imglib2.script.view.ROI;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * TODO
 *
 */
public class TestExtend
{
	public static void main(String[] args) {
		
		Img<UnsignedByteType> img = ImgLib.wrap(IJ.openImage("/home/albert/Desktop/t2/bridge.gif"));
		
		Img<UnsignedByteType> multiPeriodic =
			new ROI<UnsignedByteType>(
				new ExtendPeriodic<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		
		Img<UnsignedByteType> multiMirroredDouble =
			new ROI<UnsignedByteType>(
				new ExtendMirrorDouble<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		
		Img<UnsignedByteType> multiMirroredSingle =
			new ROI<UnsignedByteType>(
				new ExtendMirrorSingle<UnsignedByteType>(img),
				new long[]{-512, -512},
				new long[]{1024, 1024});
		Img<UnsignedByteType> centered =
			new ROI<UnsignedByteType>(
					new Extend<UnsignedByteType>(img, 0),
					new long[]{-512, -512},
					new long[]{1024, 1024});
		
		// Above, notice the negative offsets. This needs fixing, it's likely due to recent change
		// in how offsets are used. TODO
		
		try {
			ImgLib.show(multiPeriodic, "periodic");
			ImgLib.show(multiMirroredDouble, "mirror double edge");
			ImgLib.show(multiMirroredSingle, "mirror single edge");
			ImgLib.show(centered, "translated 0 background");
		} catch (ImgLibException e) {
			e.printStackTrace();
		}
	}
}
