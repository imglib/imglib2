package net.imglib2.view.fluent;

import java.util.Arrays;

class ViewUtils
{
	/**
	 * Expand or truncate the provided array of {@code steps}  to length {@code
	 * n}. If {@code steps.length < n}, the last step is repeated. That is, the
	 * remaining elements are filled with {@code steps[steps.length - 1]}.
	 */
	static long[] getSubsampleSteps( final long[] steps, final int n )
	{
		final long[] fullSteps = ( steps.length == n ) ? steps : Arrays.copyOf( steps, n );
		if ( steps.length < n )
			Arrays.fill( fullSteps, steps.length, n, steps[ steps.length - 1 ] );
		return fullSteps;
	}


}
