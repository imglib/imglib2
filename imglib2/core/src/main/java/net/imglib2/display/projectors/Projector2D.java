//package net.imglib2.display.projectors;
//
//import net.imglib2.Cursor;
//import net.imglib2.FinalInterval;
//import net.imglib2.IterableInterval;
//import net.imglib2.RandomAccessibleInterval;
//import net.imglib2.converter.Converter;
//import net.imglib2.type.Type;
//import net.imglib2.view.IterableRandomAccessibleInterval;
//
///*
// * depends on SubsetViews which are in ops
// * 
// * => at the moment not functional
// */
//
///**
// * A general 2D Projector that uses two dimensions as input to create the 2D result. Starting from the
// * reference point two dimensions are sampled such that a plain gets cut out of a higher dimensional data
// * volume.
// * <br>
// * The mapping function can be specified with a {@link Converter}.
// * <br>
// * A basic example is cutting out a time frame from a (greyscale) video 
// * 
// * @author zinsmaie
// *
// * @param <A>
// * @param <B>
// */
//public class Projector2D<A extends Type<A>, B extends Type<B>> extends
//		Abstract2DProjector<A, B> {
//
//	final Converter<A, B> converter;
//	final protected IterableInterval<B> target;
//	final int numDimensions;
//	private final int dimX;
//	private final int dimY;
//
//	final int X = 0;
//	final int Y = 1;
//	private RandomAccessibleInterval<A> source;
//
//	/**
//	 * creates a new 2D projector that samples a plain in the dimensions dimX, dimY.
//	 * 
//	 * @param dimX
//	 * @param dimY
//	 * @param source
//	 * @param target
//	 * @param converter a converter that is applied to each point in the plain. This can e.g. be used
//	 * for normalization, conversions, ...
//	 */
//	public Projector2D(final int dimX, final int dimY,
//			final RandomAccessibleInterval<A> source,
//			final IterableInterval<B> target, final Converter<A, B> converter) {
//		super(source.numDimensions());
//		this.dimX = dimX;
//		this.dimY = dimY;
//		this.target = target;
//		this.source = source;
//		this.converter = converter;
//		this.numDimensions = source.numDimensions();
//	}
//
//	/**
//	 * projects data from the source to the target and
//	 * applies the former specified {@link Converter} e.g. for normalization.
//	 */
//	@Override
//	public void map() {
//		// fix interval for all dimensions
//		for (int d = 0; d < position.length; ++d)
//			min[d] = max[d] = position[d];
//
//		min[dimX] = target.min(X);
//		min[dimY] = target.min(Y);
//		max[dimX] = target.max(X);
//		max[dimY] = target.max(Y);
//		final FinalInterval sourceInterval = new FinalInterval(min, max);
//
//		IterableRandomAccessibleInterval<A> iterableSubsetView = SubsetViews
//				.iterableSubsetView(source, sourceInterval);
//
//		final Cursor<B> targetCursor = target.localizingCursor();
//		final Cursor<A> sourceCursor = iterableSubsetView.cursor();
//
//
//		while (targetCursor.hasNext()) {
//			targetCursor.fwd();
//			sourceCursor.fwd();
//                        converter.convert(sourceCursor.get(),
//                                        targetCursor.get());
//		}
//	}
// }
