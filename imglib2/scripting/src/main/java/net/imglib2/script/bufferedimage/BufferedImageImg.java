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

package net.imglib2.script.bufferedimage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class BufferedImageImg<T extends NumericType<T> & NativeType<T>> extends ImgProxy<T>
{

	public BufferedImageImg(final BufferedImage bi) {
		super((Img<T>)createImg(bi));
	}

	private static final Img<?> createImg(final BufferedImage bi) {
		final long[] dims = new long[]{bi.getWidth(), bi.getHeight()};
		switch (bi.getType()) {
		case BufferedImage.TYPE_BYTE_GRAY:
		case BufferedImage.TYPE_BYTE_INDEXED:
			ByteArray ba = new ByteArray(((DataBufferByte)bi.getRaster().getDataBuffer()).getData());
			ArrayImg<UnsignedByteType, ByteArray> b = new ArrayImg<UnsignedByteType, ByteArray>(ba, dims, 1);
			b.setLinkedType(new UnsignedByteType(b));
			return b;
		case BufferedImage.TYPE_USHORT_GRAY:
			ShortArray sa = new ShortArray(((DataBufferShort)bi.getRaster().getDataBuffer()).getData());
			ArrayImg<UnsignedShortType, ShortArray> s = new ArrayImg<UnsignedShortType, ShortArray>(sa, dims, 1);
			s.setLinkedType(new UnsignedShortType(s));
			return s;
		case BufferedImage.TYPE_INT_RGB:
		case BufferedImage.TYPE_INT_ARGB:
			IntArray ia = new IntArray(((DataBufferInt)bi.getRaster().getDataBuffer()).getData());
			ArrayImg<ARGBType, IntArray> i = new ArrayImg<ARGBType, IntArray>(ia, dims, 1);
			i.setLinkedType(new ARGBType(i));
			return i;
		}
		throw new UnsupportedOperationException("Cannot wrap images of type " + bi.getType());
	}
}
