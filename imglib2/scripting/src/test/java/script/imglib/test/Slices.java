/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
import net.imglib2.img.Img;
import net.imglib2.script.ImgLib;
import net.imglib2.script.slice.SliceXY;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * TODO
 *
 */
public class Slices {

	static public final void main(String[] args) {
		
		new ImageJ();
		
		try {
			String src = "http://imagej.nih.gov/ij/images/bat-cochlea-volume.zip";
			//String src = "/home/albert/Desktop/t2/bat-cochlea-volume.tif";	
			Img<UnsignedByteType> img = ImgLib.open(src);
			
			Img<UnsignedByteType> s = new SliceXY<UnsignedByteType>(img, 23);

			ImgLib.wrap(s, "23").show();
			ImgLib.wrap(img, "bat").show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
