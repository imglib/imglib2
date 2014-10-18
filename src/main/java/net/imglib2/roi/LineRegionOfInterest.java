/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.roi;

/**
 * A {@link RegionOfInterest} that allows one represents a line segment.
 * 
 * @author Barry DeZonia
 * 
 */
public class LineRegionOfInterest extends AbstractRegionOfInterest
{

	// -- declarations --
	private static final double SLOPE_TOLERANCE = 0.01;

	private static final double UNIT_TOLERANCE = 0.5;

	private final double[] p1, p2;

	private final double[] tmpMin, tmpMax;

	private final double[] lineVector;

	private final double[] tmpVector;

	// -- constructors --

	public LineRegionOfInterest( final double[] pt1, final double[] pt2 )
	{
		super( pt1.length );
		assert pt1.length == pt2.length;
		this.p1 = pt1.clone();
		this.p2 = pt2.clone();
		this.tmpMin = new double[ pt1.length ];
		this.tmpMax = new double[ pt1.length ];
		this.lineVector = new double[ pt1.length ];
		this.tmpVector = new double[ pt1.length ];
		invalidateCachedState();
		calcLineVector();
	}

	// -- LineRegionOfInterest methods --

	public void getPoint1( final double[] pt )
	{
		System.arraycopy( p1, 0, pt, 0, p1.length );
	}

	public void getPoint2( final double[] pt )
	{
		System.arraycopy( p2, 0, pt, 0, p2.length );
	}

	public void setPoint1( final double[] pt )
	{
		System.arraycopy( pt, 0, p1, 0, p1.length );
		invalidateCachedState();
		calcLineVector();
	}

	public void setPoint2( final double[] pt )
	{
		System.arraycopy( pt, 0, p2, 0, p2.length );
		invalidateCachedState();
		calcLineVector();
	}

	public double getPoint1( final int dim )
	{
		return p1[ dim ];
	}

	public double getPoint2( final int dim )
	{
		return p2[ dim ];
	}

	public void setPoint1( final double val, final int dim )
	{
		p1[ dim ] = val;
		invalidateCachedState();
		calcLineVector();
	}

	public void setPoint2( final double val, final int dim )
	{
		p2[ dim ] = val;
		invalidateCachedState();
		calcLineVector();
	}

	// -- RegionOfInterest methods --

	@Override
	public void move( final double displacement, final int d )
	{
		p1[ d ] += displacement;
		p2[ d ] += displacement;
		invalidateCachedState();
		calcLineVector();
	}

	// Could just calc the N-dim line equation, calc the value and compare it to
	// zero. This would be easy and efficient. But it causes some trickiness in
	// determining when a point near an end point is on the line (i.e. when the
	// line is rotated). End point checking could see if pt is within a distance
	// of an end point. This might be the best way to go.
	//
	// Other approaches
	// - calc a rotation/translation matrix from original line. transform given
	// point and compare it to transformed end points. simple tests. hard to
	// setup n-dim rotation/translation matrix
	// - create two circle ROIS and a polygon ROI that encompass the valid
	// space.
	// simple tests at runtime but a lot of object overhead and maybe slow. Also
	// might only be 2 dimensional.
	// - calc the perpendicular distance of the point from the line. need to
	// derive an n-dim equation though.

	// Another method. Form two vectors. Use dot product to see if they are
	// parallel. Check points for nearness when necessary. There is some drift
	// inaccuracy here. The farther away from one endpoint you are the more
	// you can deviate from the line and be considered "on".

	@Override
	public boolean contains( final double[] position )
	{
		// close enough to one endpoint?
		if ( dist( p1, position ) < UNIT_TOLERANCE )
			return true;
		// close enough to other endpoint?
		if ( dist( p2, position ) < UNIT_TOLERANCE )
			return true;
		// create vector from p1 to position
		for ( int i = 0; i < p1.length; i++ )
			tmpVector[ i ] = position[ i ] - p1[ 1 ];
		// calc dot prodiuct
		final double dotProduct = dot( lineVector, tmpVector );
		// calc magnitude product
		double magnitudeProduct = dist( p1, p2 );
		magnitudeProduct *= dist( p1, position );
		// the cosine value is the ratio
		final double cosTheta = dotProduct / magnitudeProduct;
		// test if nowhere near 180 degrees or -180 degrees
		final boolean nearCos0 =
				( cosTheta > 1.0 - SLOPE_TOLERANCE ) &&
						( cosTheta < 1.0 + SLOPE_TOLERANCE );
		final boolean nearCos180 =
				( cosTheta > -1.0 - SLOPE_TOLERANCE ) &&
						( cosTheta < -1.0 + SLOPE_TOLERANCE );
		if ( ( !nearCos0 && !nearCos180 ) )
			return false;
		// else its near the line
		// check that it is between the endpoints
		// for rotated lines this could be insufficient but those cases handled
		// by endpoint distance check earlier
		realMin( tmpMin );
		realMax( tmpMax );
		if ( !outOfBounds( tmpMin, tmpMax, position ) )
			return true;
		// somewhere outside narrow bounds of p1 and p2
		// already know its not near an endpoint
		// so too far away
		return false;
	}

	// -- AbstractRegionOfInterest methods --

	@Override
	protected void getRealExtrema( final double[] minima, final double[] maxima )
	{
		for ( int i = 0; i < p1.length; i++ )
		{
			minima[ i ] = Math.min( p1[ i ], p2[ i ] );
			maxima[ i ] = Math.max( p1[ i ], p2[ i ] );
		}
	}

	// -- private helpers --

	private void calcLineVector()
	{
		for ( int i = 0; i < p1.length; i++ )
			lineVector[ i ] = p2[ i ] - p1[ i ];
	}

	private double dist( final double[] pt1, final double[] pt2 )
	{
		double sum = 0;
		for ( int i = 0; i < p1.length; i++ )
		{
			final double term = pt2[ i ] - pt1[ i ];
			sum += term * term;
		}
		return Math.sqrt( sum );
	}

	private double dot( final double[] vec1, final double[] vec2 )
	{
		double sum = 0;
		for ( int i = 0; i < p1.length; i++ )
			sum += vec1[ i ] * vec2[ i ];
		return sum;
	}

	private boolean outOfBounds( final double[] mn, final double[] mx, final double[] pt )
	{
		for ( int i = 0; i < p1.length; i++ )
		{
			if ( pt[ i ] < mn[ i ] )
				return true;
			if ( pt[ i ] > mx[ i ] )
				return true;
		}
		return false;
	}

}
