package mpicbg.imglib.algorithm.math.operation;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.NumericType;

/**
 * A class to add multiple images together into a single output image.  Images may be of different
 * sizes.  The default behavior is to add images as aligned at the top-left corner, however,
 * each image may optionally be offset from the others.  Pixel values outside of the boundaries
 * are determined by an {@link OutOfBoundsStrategyFactory}, optionally on a per-image basis.  The
 * default strategy is to assume a zero value for out-of-bounds pixels. 
 * @author Larry Lindsey
 *
 * @param <T> {@link Image} type.
 */
public class ImageAddition<T extends NumericType<T>> extends MultipleImageOperation<T, T, TypeAddition<T>>
{
	
	public static <S extends NumericType<S>> ImageAddition<S> addImages(Image<S> ... im)
	{
		ImageAddition<S> imageAddition;
		final int[] outSize = new int[im[0].getNumDimensions()];

		//Calculate bounding box
		for (int i = 0; i < im[0].getNumDimensions(); ++i)
		{
			outSize[i] = im[0].getDimension(i);
			for (int j = 1; j < im.length; ++j)
			{
				int dim = im[j].getDimension(i);
				if (dim > outSize[i])
				{
					outSize[i] = dim;
				}
			}
		}

		imageAddition = new ImageAddition<S>(outSize, im[0].getImageFactory(), im[0].createType());

		for (Image<S> image : im)
		{
			imageAddition.addInputImage(image);
		}

		return imageAddition;
	}
	
	private final TypeAddition<T> addition;

	public ImageAddition(int[] outSize, ImageFactory<T> imageFactory, T type)
	{
		super(outSize, imageFactory, new TypeAddition<T>(type), type);
		addition = super.getOperation();
	}
	
	@Override
	public boolean addInputImage(Image<T> im, int[] offset,
			OutOfBoundsStrategyFactory<T> outsideFactory)
	{
		addition.addFactor();
		return super.addInputImage(im, offset, outsideFactory);
	}
	
	public void setFactor(int i, T t)
	{
		addition.setFactor(i, t);
	}
		
}
