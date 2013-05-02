
 * @author Stephan Preibisch
package net.imglib2.algorithm.math;

import net.imglib2.container.Img;
import net.imglib2.type.Type;

/**
 * TODO
 *
 */
public class ImageCalculatorInPlace <S extends Type<S>, T extends Type<T>> extends ImageCalculator<S, T, S>
{
	public ImageCalculatorInPlace( final Img<S> image1, final Img<T> image2, final Function<S, T, S> function )
	{
		super( image1, image2, image1, function );
	}
}
