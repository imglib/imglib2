package mpicbg.imglib.algorithm.roi;


import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.ShortType;

public class DirectConvolution
	<T extends NumericType<T>, R extends NumericType<R>, S extends NumericType<S>>
		extends ROIAlgorithm<T, S>
{

	protected static void quickKernel2D(short[][] vals, Image<ShortType> kern)
	{
		final LocalizableByDimCursor<ShortType> cursor = kern.createLocalizableByDimCursor();
		final int[] pos = new int[2];

		for (int i = 0; i < vals.length; ++i)
		{
			for (int j = 0; j < vals[i].length; ++j)
			{
				pos[0] = i;
				pos[1] = j;
				cursor.setPosition(pos);
				cursor.getType().set(vals[i][j]);
			}
		}
		cursor.close();		
	}
	
	public static Image<ShortType> sobelVertical()
	{
		final ImageFactory<ShortType> factory = new ImageFactory<ShortType>(new ShortType(),
				new ArrayContainerFactory());
		final Image<ShortType> sobel = factory.createImage(new int[]{3, 3}, "Vertical Sobel");
		final short[][] vals = {{-1, -2, -1},
				{0, 0, 0},
				{1, 2, 1}};
		
		quickKernel2D(vals, sobel);		
		
		return sobel;
	}
	
	public static Image<ShortType> sobelHorizontal()
	{
		final ImageFactory<ShortType> factory = new ImageFactory<ShortType>(new ShortType(),
				new ArrayContainerFactory());
		final Image<ShortType> sobel = factory.createImage(new int[]{3, 3}, "Horizontal Sobel");
		final short[][] vals = {{1, 0, -1},
				{2, 0, -2},
				{1, 0, -1}};
		
		quickKernel2D(vals, sobel);		
		
		return sobel;
	}
	
	
	private final Image<R> kernel;
	private final int[] kernelSize;
	private LocalizableByDimCursor<S> outputImageCursor;
	private final LocalizableByDimCursor<R> kernelCursor;
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<R> kernel)
	{
		this(type, inputImage, kernel, null);
	}
	
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<R> kernel,
			final OutsideStrategyFactory<T> outsideFactory) {
		super(type, inputImage, kernel.getDimensions(), outsideFactory);
		this.kernel = kernel;
		outputImageCursor = null;
		kernelSize = kernel.getDimensions();
		kernelCursor = kernel.createLocalizableByDimCursor();
		
		setName(inputImage.getName() + " * " + kernel.getName());
	}
	
	private LocalizableByDimCursor<S> getOutputCursor()
	{
		if (outputImageCursor == null)
		{
			outputImageCursor = getOutputImage().createLocalizableByDimCursor();
		}		
		return outputImageCursor;
	}
	
	private void invertPosition(final int[] pos, final int[] invPos)
	{
	    for (int i = 0; i < kernel.getNumDimensions(); ++i)
		{
			invPos[i] = kernelSize[i] - pos[i] - 1;
		}
	}
	
	@Override
	protected boolean patchOperation(final int[] position, final RegionOfInterestCursor<T> roiCursor) {
		final LocalizableByDimCursor<S> outCursor = getOutputCursor();
		final int[] pos = new int[outCursor.getNumDimensions()];
		final int[] invPos = new int[outCursor.getNumDimensions()];
		float conv = 0;
		//final S type = outCursor.getImage().createType();
		
		outCursor.setPosition(position);
		
		while(roiCursor.hasNext())
		{
			roiCursor.fwd();
			roiCursor.getPosition(pos);
			invertPosition(pos, invPos);
			kernelCursor.setPosition(invPos);
			conv += roiCursor.getType().getReal() * kernelCursor.getType().getReal();					
		}
				
		outCursor.getType().setReal(conv);
		
		return true;
	}

	@Override
	public boolean checkInput() {
		if (kernel.getNumDimensions() == getOutputImage().getNumActiveCursors())
		{
			setErrorMessage("Kernel has different dimensionality than the Image");
			return false;
		}
		else
		{
			return true;
		}
	}

}
