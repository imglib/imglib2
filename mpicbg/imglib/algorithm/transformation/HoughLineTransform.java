package mpicbg.imglib.algorithm.transformation;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.IntType;
import mpicbg.imglib.type.numeric.LongType;

public class HoughLineTransform <S extends NumericType<S>, T extends NumericType<T>> extends HoughTransform<S, T>
{
	public static final int DEFAULT_THETA = 180;
	private final double dTheta;
	private final double dRho;
	private final T threshold;
	private final int nRho;
	private final int nTheta;
	
	public static int defaultRho(final Image<?> inputImage)
	{
		return (int)(MathLib.computeLength(inputImage.getDimensions()));
	}
	
	public static <T extends NumericType<T>> HoughLineTransform<IntType, T> integerHoughLine(Image<T> inputImage)
	{
		return new HoughLineTransform<IntType, T>(inputImage, new IntType());
	}
	
	public static <T extends NumericType<T>> HoughLineTransform<LongType, T> longHoughLine(Image<T> inputImage)
	{
		return new HoughLineTransform<LongType, T>(inputImage, new LongType());
	}
	
	public HoughLineTransform(Image<T> inputImage, S type)
	{
		this(inputImage, DEFAULT_THETA, type);
	}
	
	public HoughLineTransform(Image<T> inputImage, int theta, S type)
	{
		this(inputImage, defaultRho(inputImage), DEFAULT_THETA, type);
	}

	public HoughLineTransform(Image<T> inputImage, int rho, int theta, S type)
	{
		super(inputImage, new int[]{rho, theta}, type);
		//Theta by definition is in [0..pi].
		dTheta = Math.PI / (double)(theta - 1);
		/*The furthest a point can be from the origin is the length calculated
		 * from the dimensions of the Image.
		 */
		dRho = MathLib.computeLength(inputImage.getDimensions()) / (double)rho;
		threshold = inputImage.createType();
		nRho = rho;
		nTheta = theta;
	}
	
	public HoughLineTransform(Image<T> inputImage, ImageFactory<S> factory, int rho, int theta)
	{
		super(inputImage, new int[]{rho, theta}, factory);
		dTheta = Math.PI / (double)(theta - 1);
		dRho = MathLib.computeLength(inputImage.getDimensions()) / (double)rho;
		threshold = inputImage.createType();
		nRho = rho;
		nTheta = theta;
	}
	
	public void setThreshold(final T inThreshold)
	{
		threshold.set(inThreshold);
	}

	@Override
	public boolean process() {
		final LocalizableCursor<T> imageCursor = getImage().createLocalizableCursor();
		final int[] position = new int[getImage().getDimensions().length];
		
		while (imageCursor.hasNext())
		{
			double theta, rho;
			int r;
			int[] voteLoc = new int[2];
			imageCursor.fwd();
			imageCursor.getPosition(position);
			
			for (int t = 0; t < nTheta; ++t)
			{
				if (imageCursor.getType().compareTo(threshold) > 0)
				{
					theta = dTheta * (double)t;
					rho = Math.cos(theta) * (double)position[0] + Math.sin(theta) * (double)position[1];
					r = Math.round((float)(rho / dRho));
					voteLoc[0] = r;
					voteLoc[1] = t;

					super.placeVote(voteLoc);
				}
			}			
		}
		
		imageCursor.close();	
		return false;
	}
	

}
