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
import net.imglib2.util.Fraction;

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
			ArrayImg<UnsignedByteType, ByteArray> b = new ArrayImg<UnsignedByteType, ByteArray>(ba, dims, new Fraction());
			b.setLinkedType(new UnsignedByteType(b));
			return b;
		case BufferedImage.TYPE_USHORT_GRAY:
			ShortArray sa = new ShortArray(((DataBufferShort)bi.getRaster().getDataBuffer()).getData());
			ArrayImg<UnsignedShortType, ShortArray> s = new ArrayImg<UnsignedShortType, ShortArray>(sa, dims, new Fraction());
			s.setLinkedType(new UnsignedShortType(s));
			return s;
		case BufferedImage.TYPE_INT_RGB:
		case BufferedImage.TYPE_INT_ARGB:
			IntArray ia = new IntArray(((DataBufferInt)bi.getRaster().getDataBuffer()).getData());
			ArrayImg<ARGBType, IntArray> i = new ArrayImg<ARGBType, IntArray>(ia, dims, new Fraction());
			i.setLinkedType(new ARGBType(i));
			return i;
		}
		throw new UnsupportedOperationException("Cannot wrap images of type " + bi.getType());
	}
}
