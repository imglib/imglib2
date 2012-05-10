package gui;

import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

public class Interactive3DViewer< T extends NumericType< T > > extends AbstractInteractiveViewer implements TransformEventHandler3D.TransformListener
{
	/**
	 * the {@link RandomAccessible} to display
	 */
	final protected RandomAccessible< T > source;

	/**
	 * the size of the {@link #source}. This is used for displaying the
	 * navigation wire-frame cube.
	 */
	final Interval sourceInterval;

	/**
	 * converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< T, ARGBType > converter;

	/**
	 * Display.
	 */
	final protected ImagePlus imp;

	/**
	 * Used to render into {@link #imp}.
	 */
	final protected ARGBScreenImage screenImage;

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * ImgLib2 logo overlay painter.
	 */
	final protected LogoPainter logo = new LogoPainter();

	/**
	 * Key and mouse handler, that maintains the current transformation. It
	 * triggers {@link #setTransform(AffineTransform2D), {
	 * @link #toggleInterpolation()}, and {@link #quit()}.
	 */
	protected TransformEventHandler3D transformEventHandler;

	/**
	 * Register and restore key and mouse handlers.
	 */
	protected GUI gui;

	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();

	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();

	final private ArrayList< AffineTransform3D > list = new ArrayList< AffineTransform3D >();

	final private AffineTransform3D affine = new AffineTransform3D();

	final private AffineTransform3D reducedAffine = new AffineTransform3D();

	final private AffineTransform3D reducedAffineCopy = new AffineTransform3D();

	/**
	 *
	 * @param width
	 *            width of the display window
	 * @param height
	 *            height of the display window
	 * @param source
	 *            the {@link RandomAccessible} to display
	 * @param sourceInterval
	 *            size of the source. This is only for displaying a navigation
	 *            wire-frame cube.
	 * @param converter
	 *            converts {@link #source} type T to ARGBType for display
	 * @param initialTransform
	 *            initial transformation to apply to the {@link #source}
	 * @param yScale
	 *            scale factor for the Y axis, that is, the pixel width/height
	 *            ratio.
	 * @param zScale
	 *            scale factor for the Z axis, that is, the pixel width/depth
	 *            ratio.
	 * @param currentSlice
	 *            which slice to display initially.
	 */
	public Interactive3DViewer( final int width, final int height, final RandomAccessible< T > source, final Interval sourceInterval, final Converter< T, ARGBType > converter, final AffineTransform3D initialTransform, final double yScale, final double zScale, final double currentSlice )
	{
		this.converter = converter;
		this.source = source;
		this.sourceInterval = sourceInterval;

		final ColorProcessor cp = new ColorProcessor( width, height );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] ) cp.getPixels() );
		projector = createProjector( nnFactory );

		if ( initialTransform != null )
			list.add( initialTransform );
		list.add( affine );
		TransformEventHandler3D.reduceAffineTransformList( list, reducedAffine );

		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();

		// create and register key and mouse handler
		transformEventHandler = new TransformEventHandler3D( imp, this, yScale, zScale, currentSlice );
		gui = new GUI( imp );
		gui.addHandler( transformEventHandler );

		requestRepaint();
		startPainter();
	}

	/**
	 * Add new event handler.
	 */
	public void addHandler( final Object handler )
	{
		gui.addHandler( handler );
	}

	// -- TransformEventHandler3D.TransformListener --

	@Override
	public void setTransform( final AffineTransform3D transform )
	{
		synchronized ( reducedAffine )
		{
			affine.set( transform );
			TransformEventHandler3D.reduceAffineTransformList( list, reducedAffine );
		}
		requestRepaint();
	}

	@Override
	public void quit()
	{
		stopPainter();
		if ( imp != null )
		{
			gui.restoreGui();
		}
	}

	protected int interpolation = 0;

	@Override
	public void toggleInterpolation()
	{
		++interpolation;
		interpolation %= 2;
		switch ( interpolation )
		{
		case 0:
			projector = createProjector( nnFactory );
			break;
		case 1:
			projector = createProjector( nlFactory );
			break;
		}
		requestRepaint();
	}

	protected XYRandomAccessibleProjector< T, ARGBType > createProjector( final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( source, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, reducedAffineCopy.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}

	// -- AbstractInteractiveExample --

	@Override
	public void paint()
	{
		synchronized ( reducedAffine )
		{
			reducedAffineCopy.set( reducedAffine );
		}
		projector.map();
		logo.paint( screenImage );
		visualize();
		imp.updateAndDraw();
	}

	private double perspectiveX( final double[] p, final double d, final double w2 )
	{
		return ( p[ 0 ] - w2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + w2 / 5;
	}

	private double perspectiveY( final double[] p, final double d, final double h2 )
	{
		return ( p[ 1 ] - h2 ) / 10 / ( p[ 2 ] / 10 + d ) * d + h2 / 5;
	}

	private void splitEdge( final double[] a, final double[] b, final GeneralPath before, final GeneralPath behind, final double d2, final double w2, final double h2 )
	{
		final double[] t = new double[ 3 ];
		if ( a[ 2 ] <= 0 )
		{
			before.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
			if ( b[ 2 ] <= 0 )
				before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				before.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				behind.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			}
		}
		else
		{
			behind.moveTo( perspectiveX( a, d2, w2 ), perspectiveY( a, d2, h2 ) );
			if ( b[ 2 ] > 0 )
				behind.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			else
			{
				final double d = a[ 2 ] / ( a[ 2 ] - b[ 2 ] );
				t[ 0 ] = ( b[ 0 ] - a[ 0 ] ) * d + a[ 0 ];
				t[ 1 ] = ( b[ 1 ] - a[ 1 ] ) * d + a[ 1 ];
				behind.lineTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				before.moveTo( perspectiveX( t, d2, w2 ), perspectiveY( t, d2, h2 ) );
				before.lineTo( perspectiveX( b, d2, w2 ), perspectiveY( b, d2, h2 ) );
			}
		}
	}

	final protected void visualize()
	{
		final double w = sourceInterval.dimension( 0 ) - 1;
		final double h = sourceInterval.dimension( 1 ) - 1;
		final double d = sourceInterval.dimension( 2 ) - 1;
		final double w2 = screenImage.dimension( 0 ) / 2.0;
		final double h2 = screenImage.dimension( 1 ) / 2.0;
		final double d2 = d;

		final double[] p000 = new double[] { 0, 0, 0 };
		final double[] p100 = new double[] { w, 0, 0 };
		final double[] p010 = new double[] { 0, h, 0 };
		final double[] p110 = new double[] { w, h, 0 };
		final double[] p001 = new double[] { 0, 0, d };
		final double[] p101 = new double[] { w, 0, d };
		final double[] p011 = new double[] { 0, h, d };
		final double[] p111 = new double[] { w, h, d };

		final double[] q000 = new double[ 3 ];
		final double[] q100 = new double[ 3 ];
		final double[] q010 = new double[ 3 ];
		final double[] q110 = new double[ 3 ];
		final double[] q001 = new double[ 3 ];
		final double[] q101 = new double[ 3 ];
		final double[] q011 = new double[ 3 ];
		final double[] q111 = new double[ 3 ];

		final double[] px = new double[] { w / 2, 0, 0 };
		final double[] py = new double[] { 0, h / 2, 0 };
		final double[] pz = new double[] { 0, 0, d / 2 };

		final double[] qx = new double[ 3 ];
		final double[] qy = new double[ 3 ];
		final double[] qz = new double[ 3 ];

		final double[] c000 = new double[] { 0, 0, 0 };
		final double[] c100 = new double[] { screenImage.dimension( 0 ), 0, 0 };
		final double[] c010 = new double[] { 0, screenImage.dimension( 1 ), 0 };
		final double[] c110 = new double[] { screenImage.dimension( 0 ), screenImage.dimension( 1 ), 0 };

		reducedAffineCopy.apply( p000, q000 );
		reducedAffineCopy.apply( p100, q100 );
		reducedAffineCopy.apply( p010, q010 );
		reducedAffineCopy.apply( p110, q110 );
		reducedAffineCopy.apply( p001, q001 );
		reducedAffineCopy.apply( p101, q101 );
		reducedAffineCopy.apply( p011, q011 );
		reducedAffineCopy.apply( p111, q111 );

		reducedAffineCopy.apply( px, qx );
		reducedAffineCopy.apply( py, qy );
		reducedAffineCopy.apply( pz, qz );

		final GeneralPath box = new GeneralPath();
		final GeneralPath boxBehind = new GeneralPath();

		splitEdge( q000, q100, box, boxBehind, d2, w2, h2 );
		splitEdge( q100, q110, box, boxBehind, d2, w2, h2 );
		splitEdge( q110, q010, box, boxBehind, d2, w2, h2 );
		splitEdge( q010, q000, box, boxBehind, d2, w2, h2 );

		splitEdge( q001, q101, box, boxBehind, d2, w2, h2 );
		splitEdge( q101, q111, box, boxBehind, d2, w2, h2 );
		splitEdge( q111, q011, box, boxBehind, d2, w2, h2 );
		splitEdge( q011, q001, box, boxBehind, d2, w2, h2 );

		splitEdge( q000, q001, box, boxBehind, d2, w2, h2 );
		splitEdge( q100, q101, box, boxBehind, d2, w2, h2 );
		splitEdge( q110, q111, box, boxBehind, d2, w2, h2 );
		splitEdge( q010, q011, box, boxBehind, d2, w2, h2 );

		/* virtual slice canvas */
		final GeneralPath canvas = new GeneralPath();
		canvas.moveTo( perspectiveX( c000, d2, w2 ), perspectiveY( c000, d2, h2 ) );
		canvas.lineTo( perspectiveX( c100, d2, w2 ), perspectiveY( c100, d2, h2 ) );
		canvas.lineTo( perspectiveX( c110, d2, w2 ), perspectiveY( c110, d2, h2 ) );
		canvas.lineTo( perspectiveX( c010, d2, w2 ), perspectiveY( c010, d2, h2 ) );
		canvas.closePath();

		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D ) image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.MAGENTA );
		graphics.draw( boxBehind );
		graphics.setPaint( new Color( 0x80ffffff, true ) );
		graphics.fill( canvas );
		graphics.setPaint( Color.GREEN );
		graphics.draw( box );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString( "x", ( float ) perspectiveX( qx, d2, w2 ), ( float ) perspectiveY( qx, d2, h2 ) - 2 );
		graphics.drawString( "y", ( float ) perspectiveX( qy, d2, w2 ), ( float ) perspectiveY( qy, d2, h2 ) - 2 );
		graphics.drawString( "z", ( float ) perspectiveX( qz, d2, w2 ), ( float ) perspectiveY( qz, d2, h2 ) - 2 );
	}
}
