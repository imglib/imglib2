package net.imglib2.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import net.imglib2.Interval;
import net.imglib2.realtransform.AffineTransform3D;

/**
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoxOverlay
{
	final private double[] origin;
	private double depth = 10.0;
	private double scale = 0.1;

	/**
	 * @param depth
	 *            distance from the eye to the projection plane z=0.
	 * @param scale
	 *            scale the 2D projection of the overlay box by this factor.
	 */
	public BoxOverlay()
	{
		this.origin = new double[ 3 ];
	}

	/**
	 * This paints the box overlay with perspective and scale set such that it fits approximately
	 * into the specified screen area.
	 *
	 * @param graphics
	 *            graphics context to paint to.
	 * @param sourceInterval
	 *            source interval (3D box) that is shown.
	 * @param targetInterval
	 *            target interval (2D box) into which a slice of sourceInterval
	 *            is projected.
	 * @param transform
	 *            transform from source to target.
	 * @param boxScreen
	 *            (approximate) area of the screen which to fill with the box
	 *            visualisation.
	 */
	public void paint( final Graphics2D graphics, final Interval sourceInterval, final Interval targetInterval, final AffineTransform3D transform, final Interval boxScreen )
	{
		assert ( sourceInterval.numDimensions() >= 3 );
		assert ( targetInterval.numDimensions() >= 2 );

		final double perspective = 3;
		final double screenBoxRatio = 0.75;

		final long sourceSize = Math.max( Math.max( sourceInterval.dimension( 0 ), sourceInterval.dimension( 1 ) ), sourceInterval.dimension( 2 ) );
		final long targetSize = Math.max( targetInterval.dimension( 0 ), targetInterval.dimension( 1 ) );

		final double vx = transform.get( 0, 0 );
		final double vy = transform.get( 1, 0 );
		final double vz = transform.get( 2, 0 );
		final double transformScale = Math.sqrt( vx*vx + vy*vy + vz*vz );
		setDepth( perspective * sourceSize * transformScale );

		final double bw = screenBoxRatio * boxScreen.dimension( 0 );
		final double bh = screenBoxRatio * boxScreen.dimension( 1 );
		scale = Math.min( bw / targetInterval.dimension( 0 ), bh / targetInterval.dimension( 1 ) );

		final double tsScale = transformScale * sourceSize / targetSize;
		if ( tsScale > 1.0 )
			scale /= tsScale;

		final long x = boxScreen.min( 0 ) + boxScreen.dimension( 0 ) / 2;
		final long y = boxScreen.min( 1 ) + boxScreen.dimension( 1 ) / 2;

		final AffineTransform t = graphics.getTransform();
		final AffineTransform translate = new AffineTransform( 1, 0, 0, 1, x, y );
		translate.preConcatenate( t );
		graphics.setTransform( translate );
		paint( graphics, sourceInterval, targetInterval, transform );
		graphics.setTransform( t );
	}

	public void setScale( final double scale )
	{
		this.scale = scale;
	}

	public void setDepth( final double depth )
	{
		this.depth = depth;
		origin[ 2 ] = -depth;
	}

	/**
	 *
	 * @param p point to project
	 * @return X coordinate of projected point
	 */
	private double perspectiveX( final double[] p )
	{
		return scale * ( p[ 0 ] - origin[ 0 ] ) / ( p[ 2 ] - origin[ 2 ] ) * depth;
	}

	/**
	 *
	 * @param p point to project
	 * @return Y coordinate of projected point
	 */
	private double perspectiveY( final double[] p )
	{
		return scale * ( p[ 1 ] - origin[ 1 ] ) / ( p[ 2 ] - origin[ 2 ] ) * depth;
	}

	private void splitEdge( final double[] a, final double[] b, final GeneralPath before, final GeneralPath behind )
	{
		final double[] t = new double[ 3 ];
		if ( a[ 2 ] <= 0 )
		{
			before.moveTo( perspectiveX( a ), perspectiveY( a ) );
			if ( b[ 2 ] <= 0 )
				before.lineTo( perspectiveX( b ), perspectiveY( b ) );
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				before.lineTo( perspectiveX( t ), perspectiveY( t ) );
				behind.moveTo( perspectiveX( t ), perspectiveY( t ) );
				behind.lineTo( perspectiveX( b ), perspectiveY( b ) );
			}
		}
		else
		{
			behind.moveTo( perspectiveX( a ), perspectiveY( a ) );
			if ( b[ 2 ] > 0 )
				behind.lineTo( perspectiveX( b ), perspectiveY( b ) );
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				behind.lineTo( perspectiveX( t ), perspectiveY( t ) );
				before.moveTo( perspectiveX( t ), perspectiveY( t ) );
				before.lineTo( perspectiveX( b ), perspectiveY( b ) );
			}
		}
	}

	/**
	 *
	 * @param graphics
	 *            graphics context to paint to.
	 * @param sourceInterval
	 *            source interval (3D box) that is shown.
	 * @param targetInterval
	 *            target interval (2D box) into which a slice of sourceInterval
	 *            is projected.
	 * @param transform
	 *            transform from source to target.
	 * @param x
	 *            screen coordinates where to paint the overlay
	 * @param y
	 *            screen coordinates where to paint the overlay
	 */
	public void paint( final Graphics2D graphics, final Interval sourceInterval, final Interval targetInterval, final AffineTransform3D transform, final double x, final double y )
	{
		final AffineTransform t = graphics.getTransform();
		final AffineTransform translate = new AffineTransform( 1, 0, 0, 1, x, y );
		translate.preConcatenate( t );
		graphics.setTransform( translate );
		paint( graphics, sourceInterval, targetInterval, transform );
		graphics.setTransform( t );
	}

	/**
	 *
	 * @param graphics
	 *            graphics context to paint to.
	 * @param sourceInterval
	 *            source interval (3D box) that is shown.
	 * @param targetInterval
	 *            target interval (2D box) into which a slice of sourceInterval
	 *            is projected.
	 * @param transform
	 *            transform from source to target.
	 */
	public void paint( final Graphics2D graphics, final Interval sourceInterval, final Interval targetInterval, final AffineTransform3D transform )
	{
		assert ( sourceInterval.numDimensions() >= 3 );
		assert ( targetInterval.numDimensions() >= 2 );

		final double sX0 = sourceInterval.min( 0 );
		final double sX1 = sourceInterval.max( 0 );
		final double sY0 = sourceInterval.min( 1 );
		final double sY1 = sourceInterval.max( 1 );
		final double sZ0 = sourceInterval.min( 2 );
		final double sZ1 = sourceInterval.max( 2 );

		final double tX0 = targetInterval.min( 0 );
		final double tX1 = targetInterval.max( 0 );
		final double tY0 = targetInterval.min( 1 );
		final double tY1 = targetInterval.max( 1 );

		origin[ 0 ] = tX0 + targetInterval.dimension( 0 ) / 2;
		origin[ 1 ] = tY0 + targetInterval.dimension( 1 ) / 2;

		final double[] p000 = new double[] { sX0, sY0, sZ0 };
		final double[] p100 = new double[] { sX1, sY0, sZ0 };
		final double[] p010 = new double[] { sX0, sY1, sZ0 };
		final double[] p110 = new double[] { sX1, sY1, sZ0 };
		final double[] p001 = new double[] { sX0, sY0, sZ1 };
		final double[] p101 = new double[] { sX1, sY0, sZ1 };
		final double[] p011 = new double[] { sX0, sY1, sZ1 };
		final double[] p111 = new double[] { sX1, sY1, sZ1 };

		final double[] q000 = new double[ 3 ];
		final double[] q100 = new double[ 3 ];
		final double[] q010 = new double[ 3 ];
		final double[] q110 = new double[ 3 ];
		final double[] q001 = new double[ 3 ];
		final double[] q101 = new double[ 3 ];
		final double[] q011 = new double[ 3 ];
		final double[] q111 = new double[ 3 ];

		final double[] px = new double[] { sX0 + sourceInterval.dimension( 0 ) / 2, sY0, sZ0 };
		final double[] py = new double[] { sX0, sY0 + sourceInterval.dimension( 1 ) / 2, sZ0 };
		final double[] pz = new double[] { sX0, sY0, sZ0 + sourceInterval.dimension( 2 ) / 2 };

		final double[] qx = new double[ 3 ];
		final double[] qy = new double[ 3 ];
		final double[] qz = new double[ 3 ];

		final double[] c000 = new double[] { tX0, tY0, 0 };
		final double[] c100 = new double[] { tX1, tY0, 0 };
		final double[] c010 = new double[] { tX0, tY1, 0 };
		final double[] c110 = new double[] { tX1, tY1, 0 };

		transform.apply( p000, q000 );
		transform.apply( p100, q100 );
		transform.apply( p010, q010 );
		transform.apply( p110, q110 );
		transform.apply( p001, q001 );
		transform.apply( p101, q101 );
		transform.apply( p011, q011 );
		transform.apply( p111, q111 );

		transform.apply( px, qx );
		transform.apply( py, qy );
		transform.apply( pz, qz );

		final GeneralPath box = new GeneralPath();
		final GeneralPath boxBehind = new GeneralPath();

		splitEdge( q000, q100, box, boxBehind );
		splitEdge( q100, q110, box, boxBehind );
		splitEdge( q110, q010, box, boxBehind );
		splitEdge( q010, q000, box, boxBehind );

		splitEdge( q001, q101, box, boxBehind );
		splitEdge( q101, q111, box, boxBehind );
		splitEdge( q111, q011, box, boxBehind );
		splitEdge( q011, q001, box, boxBehind );

		splitEdge( q000, q001, box, boxBehind );
		splitEdge( q100, q101, box, boxBehind );
		splitEdge( q110, q111, box, boxBehind );
		splitEdge( q010, q011, box, boxBehind );

		/* virtual slice canvas */
		final GeneralPath canvas = new GeneralPath();
		canvas.moveTo( perspectiveX( c000 ), perspectiveY( c000 ) );
		canvas.lineTo( perspectiveX( c100 ), perspectiveY( c100 ) );
		canvas.lineTo( perspectiveX( c110 ), perspectiveY( c110 ) );
		canvas.lineTo( perspectiveX( c010 ), perspectiveY( c010 ) );
		canvas.closePath();

		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.MAGENTA );
		graphics.draw( boxBehind );
		graphics.setPaint( new Color( 0x80ffffff, true ) );
		graphics.fill( canvas );
		graphics.setPaint( Color.GREEN );
		graphics.draw( box );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString( "x", ( float ) perspectiveX( qx ), ( float ) perspectiveY( qx ) - 2 );
		graphics.drawString( "y", ( float ) perspectiveX( qy ), ( float ) perspectiveY( qy ) - 2 );
		graphics.drawString( "z", ( float ) perspectiveX( qz ), ( float ) perspectiveY( qz ) - 2 );
	}
}
