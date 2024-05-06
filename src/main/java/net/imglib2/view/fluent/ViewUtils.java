package net.imglib2.view.fluent;

import java.util.Arrays;

class ViewUtils
{
	/**
	 * Expand or truncate the provided array of {@code values}  to length {@code
	 * n}. If {@code values.length < n}, the last value is repeated. That is, the
	 * remaining elements are filled with {@code values[values.length - 1]}.
	 */
	static long[] expand( final long[] values, final int n )
	{
		final long[] expandedValues = ( values.length == n ) ? values : Arrays.copyOf( values, n );
		if ( values.length < n )
			Arrays.fill( expandedValues, values.length, n, values[ values.length - 1 ] );
		return expandedValues;
	}


}
