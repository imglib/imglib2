package mpicbg.imglib.algorithm.roi;


import java.util.Arrays;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.Localizable;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.StructuringElementCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyValueFactory;
import mpicbg.imglib.type.numeric.ComplexType;
import mpicbg.imglib.type.numeric.integer.ShortType;

/**
 * DirectConvolution is an ROIAlgorithm designed to do both convolution and cross-correlation 
 * by operating on the image and kernel directly, rather than by using such time-saving tricks as
 * FFT.
 * @author Larry Lindsey
 *
 * @param <T> input image type
 * @param <R> kernel type
 * @param <S> output image type
 */
public class DirectConvolution
	<T extends ComplexType<T>, R extends ComplexType<R>, S extends ComplexType<S>>
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
	
	private static int[] zeroArray(final int d)
	{
	    int[] zeros = new int[d];
	    Arrays.fill(zeros, 0);
	    return zeros;
	}
	
	private final Image<R> kernel;
	protected  final LocalizableByDimCursor<R> kernelCursor;
	
	private final S accum;
	private final S mul;
	private final S temp;
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<R> kernel)
	{
		this(type, inputImage, kernel, new OutOfBoundsStrategyValueFactory<T>());
	}
	
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<R> kernel,
			final OutOfBoundsStrategyFactory<T> outsideFactory) {
		this(new ImageFactory<S>(type, new ArrayContainerFactory()), 
		        inputImage, kernel, outsideFactory);
	}
	
	public DirectConvolution(final ImageFactory<S> factory,
	        final Image<T> inputImage,
	        final Image<R> kernel,
			final OutOfBoundsStrategyFactory<T> outsideFactory)
	{
		super(factory, new StructuringElementCursor<T>(
		        inputImage.createLocalizableByDimCursor(outsideFactory), 
		        inputImage.getDimensions(),
		        zeroArray(inputImage.getNumDimensions())));

		getStrelCursor().centerKernel(kernel.getDimensions());
		
		this.kernel = kernel;
		kernelCursor = kernel.createLocalizableByDimCursor();
		
		setName(inputImage.getName() + " * " + kernel.getName());
		
		accum = factory.createType();
		mul = factory.createType();
		temp = factory.createType();
	}
		
	protected void setKernelCursorPosition(final Localizable l)
	{
	    kernelCursor.setPosition(l);
	}
	
	@Override
	protected boolean patchOperation(final StructuringElementCursor<T> strelCursor,
            final S outputType) {		
		T inType;
		R kernelType;
		
		accum.setZero();
			
		while(strelCursor.hasNext())
		{
		    
		    
			mul.setOne();
			strelCursor.fwd();			
			setKernelCursorPosition(strelCursor);			
			
			inType = strelCursor.getType();
			kernelType = kernelCursor.getType();
			
			temp.setReal(kernelType.getRealDouble());
			temp.setComplex(-kernelType.getComplexDouble());			
			mul.mul(temp);
			
			temp.setReal(inType.getRealDouble());
			temp.setComplex(inType.getComplexDouble());
			mul.mul(temp);
			
			accum.add(mul);			
		}
				
		outputType.set(accum);
		return true;
	}

	@Override
	public boolean checkInput() {
		if (super.checkInput())
		{
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
		else
		{
			return false;
		}
	}

}
