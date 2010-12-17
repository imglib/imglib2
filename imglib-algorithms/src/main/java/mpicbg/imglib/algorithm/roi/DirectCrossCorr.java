package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.cursor.Localizable;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.RealType;

/**
 * DirectCrossCorr performs direct cross-correlation of a kernel against an image.
 * @author Larry Lindsey
 *
 * @param <T> input image type
 * @param <R> kernel type
 * @param <S> output image type 
 */
public class DirectCrossCorr
	<T extends RealType<T>, R extends RealType<R>, S extends RealType<S>>
		extends DirectConvolution<T, R, S>
{
    
    
    private final int[] kernelSizeMinusOne;
    private final int[] invertPos;
    
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel)
	{
		super(type, inputImage, kernel, null);
		setName(inputImage.getName() + " x " + kernel.getName());
		kernelSizeMinusOne = kernel.getDimensions();
		invertPos = new int[kernelSizeMinusOne.length];

		fixKernelSize();
	}
		
	public DirectCrossCorr(final S type, final Image<T> inputImage, final Image<R> kernel,
			final OutOfBoundsStrategyFactory<T> outsideFactory) {
		super(type, inputImage, kernel, outsideFactory);
		setName(inputImage.getName() + " x " + kernel.getName());
		kernelSizeMinusOne = kernel.getDimensions();
		invertPos = new int[kernelSizeMinusOne.length];

		fixKernelSize();
	}
	
	public DirectCrossCorr(final ImageFactory<S> factory,
            final Image<T> inputImage,
            final Image<R> kernel,
            final OutOfBoundsStrategyFactory<T> outsideFactory)
    {
	    super(factory, inputImage, kernel, outsideFactory);
	    setName(inputImage.getName() + " x " + kernel.getName());
	    kernelSizeMinusOne = kernel.getDimensions();
	    invertPos = new int[kernelSizeMinusOne.length];
	    
	    fixKernelSize();
    }
	
	private void fixKernelSize()
	{
	    for (int i = 0; i < kernelSizeMinusOne.length; ++i)
	    {
	        kernelSizeMinusOne[i] -= 1;
	    }
	}
	
	protected void setKernelCursorPosition(final Localizable l)
    {
	    l.getPosition(invertPos);
	    for(int i = 0; i < invertPos.length; ++i)
	    {
	        invertPos[i] = kernelSizeMinusOne[i] - invertPos[i];
	    }
        kernelCursor.setPosition(invertPos);
    }

}
