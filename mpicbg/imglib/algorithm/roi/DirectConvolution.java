package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;

public class DirectConvolution <T extends NumericType<T>, S extends NumericType<S>> extends ROIAlgorithm<T, S> {

	private final Image<T> kernel;
	private final int[] kernelSize;
	private LocalizableByDimCursor<S> outputImageCursor;
	private final LocalizableByDimCursor<T> kernelCursor;
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<T> kernel)
	{
		this(type, inputImage, kernel, null);
	}
	
	
	public DirectConvolution(final S type, final Image<T> inputImage, final Image<T> kernel,
			final OutsideStrategyFactory<T> outsideFactory) {
		super(type, inputImage, kernel.getDimensions(), outsideFactory);
		this.kernel = kernel;
		outputImageCursor = null;
		kernelSize = kernel.getDimensions();
		kernelCursor = kernel.createLocalizableByDimCursor();
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
		final S type = outCursor.getImage().createType();
		
		outCursor.setPosition(position);
		
		while(roiCursor.hasNext())
		{
			roiCursor.fwd();
			roiCursor.getPosition(pos);
			invertPosition(pos, invPos);
			kernelCursor.setPosition(invPos);
			conv += roiCursor.getType().getReal() * kernelCursor.getType().getReal();					
		}
		type.setReal(conv);
				
		outCursor.getType().set(type);
		
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
