/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */
package interactive;

public class InteractiveOpenConnectomeRotationExample
{
/*
	final static public void main( final String[] args ) throws ImgIOException
	{
		//final OpenConnectomeRandomAccessibleInterval map = new OpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 64, 64, 8, 1, 0 );
//		final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/bock11", 135424, 119808, 1239, 128, 128, 16, 2917, 0 );
		final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/bock11", 135424, 119808, 1239, 256, 256, 32, 2917, 0 );
		//final VolatileOpenConnectomeRandomAccessibleInterval map = new VolatileOpenConnectomeRandomAccessibleInterval( "http://openconnecto.me/emca/kasthuri11", 21504, 26624, 1850, 50, 50, 5, 1, 0 );

		final int w = 720, h = 405;

		final double yScale = 1.0, zScale = 10.0;
		final AffineTransform3D initial = new AffineTransform3D();
		initial.set(
			1.0, 0.0, 0.0, ( w - map.dimension( 0 ) ) / 2.0,
			0.0, yScale, 0.0, ( h - map.dimension( 1 ) * yScale ) / 2.0,
			0.0, 0.0, zScale, -( map.dimension( 2 ) / 2.0 - 0.5 ) * zScale );

		final LogoPainter logo = new LogoPainter();
//		final RandomAccessible< UnsignedByteType > extended = Views.extendValue( map, new UnsignedByteType( 127 ) );
		final RandomAccessible< VolatileRealType< UnsignedByteType > > extended = Views.extendValue( map, new VolatileRealType< UnsignedByteType >( new UnsignedByteType( 127 ) ) );
//		new InteractiveViewer3D< UnsignedByteType >( w, h, extended, map, initial, new RealARGBConverter< UnsignedByteType >( 0, 255 ) )
		new InteractiveViewer3D< VolatileRealType< UnsignedByteType > >( w, h, extended, map, initial, new VolatileRealTypeARGBConverter( 0, 255 ) )
		{
			@Override
			public boolean drawScreenImage()
			{
				final boolean valid = super.drawScreenImage();
				logo.paint( screenImage );
				return valid;
			}

			@Override
			protected Volatile2DRandomAccessibleProjector< UnsignedByteType, VolatileRealType< UnsignedByteType >, ARGBType > createProjector()
			{
				final InterpolatorFactory< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > > interpolatorFactory;
				switch ( interpolation )
				{
				case 0:
					interpolatorFactory = new NearestNeighborInterpolatorFactory< VolatileRealType< UnsignedByteType > >();
					break;
				case 1:
				default:
					interpolatorFactory = new NLinearInterpolatorFactory< VolatileRealType< UnsignedByteType > >();
					break;
				}
				final Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > > interpolant =
						new Interpolant< VolatileRealType< UnsignedByteType >, RandomAccessible< VolatileRealType< UnsignedByteType > > >( source, interpolatorFactory );
				final AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet > mapping =
						new AffineRandomAccessible< VolatileRealType< UnsignedByteType >, AffineGet >( interpolant, sourceToScreen.inverse() );
				return new Volatile2DRandomAccessibleProjector< UnsignedByteType, VolatileRealType< UnsignedByteType >, ARGBType >( mapping, screenImage, converter );
			}

//			@Override
//			protected XYRandomAccessibleProjector< UnsignedByteType, ARGBType > createProjector()
//			{
//				final InterpolatorFactory< UnsignedByteType, RandomAccessible< UnsignedByteType > > interpolatorFactory;
//				switch ( interpolation )
//				{
//				case 0:
//					interpolatorFactory = new NearestNeighborInterpolatorFactory< UnsignedByteType >();
//					break;
//				case 1:
//				default:
//					interpolatorFactory = new NLinearInterpolatorFactory< UnsignedByteType >();
//					break;
//				}
//				final Interpolant< UnsignedByteType, RandomAccessible< UnsignedByteType > > interpolant =
//						new Interpolant< UnsignedByteType, RandomAccessible< UnsignedByteType > >( source, interpolatorFactory );
//				final AffineRandomAccessible< UnsignedByteType, AffineGet > mapping =
//						new AffineRandomAccessible< UnsignedByteType, AffineGet >( interpolant, sourceToScreen.inverse() );
//				return new XYRandomAccessibleProjector< UnsignedByteType, ARGBType >( mapping, screenImage, converter );
//			}
		};
	}
*/
}
