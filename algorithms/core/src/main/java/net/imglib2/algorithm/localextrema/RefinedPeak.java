package net.imglib2.algorithm.localextrema;

import net.imglib2.Localizable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

/**
 * A {@link RealPoint} representing a sub-pixel-localized peak. Contains the
 * original non-refined peak, a value, and a boolean validity flag. See
 * {@link SubpixelLocalization}.
 *
 * @param <P>
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class RefinedPeak< P extends Localizable > extends RealPoint
{
	protected final P originalPeak;

	protected final double value;

	protected final boolean valid;

	public RefinedPeak( final P originalPeak, final RealLocalizable refinedLocation, final double refinedValue, final boolean valid )
	{
		super( refinedLocation );
		this.originalPeak = originalPeak;
		this.value = refinedValue;
		this.valid = valid;
	}

	public P getOriginalPeak()
	{
		return originalPeak;
	}

	public double getValue()
	{
		return value;
	}

	public boolean isValid()
	{
		return valid;
	}
}
