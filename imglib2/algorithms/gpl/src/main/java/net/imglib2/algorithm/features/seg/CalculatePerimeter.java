package net.imglib2.algorithm.features.seg;

import net.imglib2.Cursor;
import net.imglib2.algorithm.convolver.DirectConvolver;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.ops.img.UnaryObjectFactory;
import net.imglib2.ops.operation.Operations;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.img.unary.ImgConvert;
import net.imglib2.ops.operation.img.unary.ImgConvert.ImgConversionTypes;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

/**
 * Input: Outline Image {@link ExtractOutlineImg}
 * 
 * @author dietzc, schoenenbergerf
 * 
 */
public class CalculatePerimeter implements
		UnaryOutputOperation<Img<BitType>, DoubleType> {

	private final ImgConvert<BitType, UnsignedShortType> m_convert;

	private final DirectConvolver<UnsignedShortType, UnsignedShortType, UnsignedShortType> m_convolve;

	public CalculatePerimeter() {

		// TODO make out of bounds
		m_convolve = new DirectConvolver<UnsignedShortType, UnsignedShortType, UnsignedShortType>();

		// TODO don't do this physically, use views!
		m_convert = new ImgConvert<BitType, UnsignedShortType>(new BitType(),
				new UnsignedShortType(), ImgConversionTypes.DIRECT);
	}

	private static synchronized Img<UnsignedShortType> getKernel() {
		@SuppressWarnings("unchecked")
		final ArrayImg<UnsignedShortType, ShortArray> img = (ArrayImg<UnsignedShortType, ShortArray>) new ArrayImgFactory<UnsignedShortType>()
				.create(new long[] { 3, 3 }, new UnsignedShortType());

		final short[] storage = img.update(null).getCurrentStorageArray();

		storage[0] = 10;
		storage[1] = 2;
		storage[2] = 10;
		storage[3] = 2;
		storage[4] = 1;
		storage[5] = 2;
		storage[6] = 10;
		storage[7] = 2;
		storage[8] = 10;

		return img;
	}

	@Override
	public DoubleType compute(final Img<BitType> op, final DoubleType r) {
		Img<UnsignedShortType> img = null;
		try {
			img = (Img<UnsignedShortType>) m_convolve
					.compute(
							Views.extend(
									Operations.compute(m_convert, op),
									new OutOfBoundsMirrorFactory<UnsignedShortType, Img<UnsignedShortType>>(
											Boundary.SINGLE)), getKernel(),
							op.factory().imgFactory(new UnsignedShortType())
									.create(op, new UnsignedShortType()));
		} catch (IncompatibleTypeException e) {
			// If factory not compatible
			img = new ArrayImgFactory<UnsignedShortType>().create(op,
					new UnsignedShortType());
		}
		final Cursor<UnsignedShortType> c = img.cursor();

		int catA = 0;
		int catB = 0;
		int catC = 0;

		while (c.hasNext()) {
			c.fwd();
			final int curr = c.get().get();

			switch (curr) {
			case 15:
			case 7:
			case 25:
			case 5:
			case 17:
			case 27:
				catA++;
				break;
			case 21:
			case 33:
				catB++;
				break;
			case 13:
			case 23:
				catC++;
				break;
			}

		}

		r.set(catA + catB * Math.sqrt(2) + catC * ((1d + Math.sqrt(2)) / 2d));

		return r;
	}

	@Override
	public UnaryOutputOperation<Img<BitType>, DoubleType> copy() {
		return new CalculatePerimeter();
	}

	@Override
	public UnaryObjectFactory<Img<BitType>, DoubleType> bufferFactory() {
		return new UnaryObjectFactory<Img<BitType>, DoubleType>() {

			@Override
			public DoubleType instantiate(Img<BitType> a) {
				return new DoubleType();
			}
		};
	}
}
