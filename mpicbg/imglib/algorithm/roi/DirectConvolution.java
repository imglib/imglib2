package mpicbg.imglib.algorithm.roi;

import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.NumericType;

public class DirectConvolution <T extends NumericType<T>, S extends NumericType<S>> extends ROIAlgorithm<T, S> {

	private final Image<T> kernel;
	private final int[] kernelSize;
	private LocalizableByDimCursor<S> imageCursor;
	
	protected DirectConvolution(final S type, final LocalizableByDimCursor<T> inCursor, final Image<T> kernel) {
		super(type, inCursor, kernel.getDimensions());
		this.kernel = kernel;
		imageCursor = null;
		kernelSize = kernel.getDimensions();
	}
	
	private LocalizableByDimCursor<S> getCursor()
	{
		if (imageCursor == null)
		{
			imageCursor = getImage().createLocalizableByDimCursor();
		}		
		return imageCursor;
	}
	
	private void invertPosition(final int[] pos, final int[] invPos)
	{
	    for (int i = 0; i < kernel.getNumDimensions(); ++i)
		{
			invPos[i] = kernelSize[i] - pos[i] - 1;
		}
	}
	
	@Override
	protected boolean patchOperation(RegionOfInterestCursor<T> cursor) {
		final Image<S> image = getImage();
		final S conv = image.createType();
		final LocalizableByDimCursor<S> outCursor = getCursor();
		final int[] pos = new int[outCursor.getNumDimensions()];
		final int[] invPos = new int[outCursor.getNumDimensions()];
		
		conv.setZero();
		while(cursor.hasNext())
		{
			S mul = image.createType();
			cursor.fwd();
			cursor.getPosition(pos);
			invertPosition(pos, invPos);
			outCursor.setPosition(invPos);
			mul.setReal(cursor.getType().getReal() * outCursor.getType().getReal());
			conv.add(mul);			
		}
		return true;
	}

	@Override
	public boolean checkInput() {
		if (kernel.getNumDimensions() == getImage().getNumActiveCursors())
		{
			setErrorMessage("Kernel has different dimensionality from the Image");
			return false;
		}
		else
		{
			return true;
		}
	}

}
