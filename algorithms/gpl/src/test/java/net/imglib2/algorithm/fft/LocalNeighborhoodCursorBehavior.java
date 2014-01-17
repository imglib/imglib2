/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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

package net.imglib2.algorithm.fft;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public class LocalNeighborhoodCursorBehavior {

	public static final void main(String[] args) {
		
		try {
			
			Img<FloatType> img = new ArrayImgFactory<FloatType>().create(new int[]{1, 1}, new FloatType());
			LocalNeighborhoodCursor<FloatType> lnc =
				new LocalNeighborhoodCursor<FloatType>(Views.extendMirrorSingle(img).randomAccess(), 1);
			Cursor<FloatType> cursor = img.cursor();
			
			long[] pos = new long[img.numDimensions()];
			long[] lpos = new long[img.numDimensions()];
			
			while (cursor.hasNext()) {
				cursor.fwd();
				cursor.localize(pos);
				lnc.reset(pos);
				
				StringBuilder sb = new StringBuilder();
				sb.append("At pos: ").append(pos[0]);
				for (int i=1; i<pos.length; ++i) {
					sb.append(',').append(pos[i]);
				}
				sb.append(":\n");
				
				int x = 0;
				
				while (lnc.hasNext()) {
					lnc.fwd();
					lnc.localize(lpos);
					sb.append(' ').append(lpos[0]);
					for (int i=1; i<lpos.length; ++i) {
						sb.append(',').append(lpos[i]);
					}
					sb.append('\n');
					
					x++;
					System.out.println("x: " + x);
				}
				System.out.println(sb.toString());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
