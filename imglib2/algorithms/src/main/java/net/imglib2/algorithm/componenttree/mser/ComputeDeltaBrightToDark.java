package net.imglib2.algorithm.componenttree.mser;

import net.imglib2.type.numeric.NumericType;

/**
 * Default implementation of {@link ComputeDelta} for bright-to-dark pass for
 * {@link NumericType}. For a given threshold value <em>a</em> compute the
 * threshold value delta steps down the component tree: <em>a+delta</em>.
 *
 * @author Tobias Pietzsch
 *
 * @param <T>
 *            value type of the input image.
 */
public final class ComputeDeltaBrightToDark< T extends NumericType< T > > implements ComputeDelta< T >
{
	private final T delta;

	ComputeDeltaBrightToDark( final T delta )
	{
		this.delta = delta;
	}

	@Override
	public T valueMinusDelta( final T value )
	{
		final T valueMinus = value.copy();
		valueMinus.add( delta );
		return valueMinus;
	}
}