package script.imglib.algorithm.fn;

import mpicbg.imglib.algorithm.roi.StatisticalOperation;
import mpicbg.imglib.algorithm.roi.StructuringElement;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

/** Morphological operations such as Open, Close, Erode and Dilate.
 *  The operation takes any of CUBE, BALL, BAR, each with a length.
 *  In ImageJ, the equivalent would be a CUBE with length 3.
 *  
 *  The {@code lengthDim} is only useful for BAR, and specifies
 *  in which axis to grow the bar.
 * 
 * 
 */
public abstract class Morph extends Array<FloatType,FloatAccess>
{
	static public enum Shape { BALL, CUBE, BAR };

	static public final Shape BALL = Shape.BALL;
	static public final Shape CUBE = Shape.CUBE; 
	static public final Shape BAR = Shape.BAR; 

	// TODO: is a.update(null) really the way to get the underlying data? It's odd.
	private Morph(final Array<FloatType,FloatAccess> a) {
		super(a.update(null), AlgorithmUtil.extractDimensions(a), 1);
	}

	public Morph(final Object fn, final Class<?> c, final Shape s, final int shapeLength, final int lengthDim, final float outside) throws Exception {
		this(Morph.process(c, fn, s, shapeLength, lengthDim, outside));
	}

	@SuppressWarnings("unchecked")
	private final static <R extends RealType<R>> Img<R> process(final Class<?> c, final Object fn, final Shape s, final int shapeLength, final int lengthDim, final float outside) throws Exception {
		final Img<R> img = AlgorithmUtil.wrap(fn);
		StructuringElement strel;
		switch (s) {
		case BALL:
			// CAREFUL: for ball shapeLength is the radius!
			strel = StructuringElement.createBall(img.numDimensions(), shapeLength);
			break;
		case BAR:
			strel = StructuringElement.createBar(img.numDimensions(), shapeLength, lengthDim);
			break;
		case CUBE:
		default:
			strel = StructuringElement.createCube(img.numDimensions(), shapeLength);
			break;
		}
		R t = img.firstElement().createVariable();
		t.setReal(outside);
		StatisticalOperation<R> morph = (StatisticalOperation<R>) c.getConstructor(Image.class, StructuringElement.class, OutOfBoundsStrategyFactory.class)
				.newInstance(img, strel, new OutOfBoundsConstantValueFactory<R>(t));
		if (!morph.process()) { // !checkInput becomes true? TODO
			throw new Exception(morph.getClass().getSimpleName() + ": " + morph.getErrorMessage());
		}
		return morph.getResult();
	}
}