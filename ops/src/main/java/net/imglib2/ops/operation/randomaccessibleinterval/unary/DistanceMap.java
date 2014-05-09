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
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Image distance Map.
 * 
 * @author Jens Metzner (University of Konstanz)
 */
public class DistanceMap< T extends RealType< T >> implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< FloatType > >
{

	public final static int MAX_DIMS = 4;

	public final static int MIN_DIMS = 2;

	@Override
	public RandomAccessibleInterval<FloatType> compute( final RandomAccessibleInterval<T> src, final RandomAccessibleInterval<FloatType> res )
	{

		final RandomAccess< FloatType > resAccess = res.randomAccess();
		final RandomAccess< T > srcAccess = src.randomAccess();

		// Cursor<FloatType> resCursor = res.cursor();
		// DIM SELECTION
		final int[] dim_sel = new int[ MAX_DIMS ];
		final int[] dim_val = new int[ MAX_DIMS ];
		final double[] dim_unit = new double[ MAX_DIMS ];
		final int[] pos = new int[ src.numDimensions() ];
		{
			int a = 0;
			for ( int i = 0; i < src.numDimensions(); ++i )
			{
				// if (src.dimension(i) > 1) {
				if ( a < MAX_DIMS )
				{
					if ( src instanceof CalibratedSpace ) {
						final CalibratedSpace<?> space = ( CalibratedSpace<?> ) src;
						// TODO - using averageScale() introduces error for nonlinear axes
						dim_unit[a] = space.averageScale( i );
					}
					else
						dim_unit[ a ] = 1;

					dim_val[ a ] = ( int ) src.dimension( i );
					dim_sel[ a++ ] = i;

					// System.out.println(dim_sel[a - 1] +
					// " "
					// + dim_val[a - 1] + " " + dim_unit[a -
					// 1]);
				}
				else
				{
					throw new IllegalArgumentException( "Too many dimensions are selected." ); // TODO
				}
				// }
			}
			if ( a < MIN_DIMS ) { throw new IllegalArgumentException( "Two dimensions have to be selected." ); // TODO
			}
			for ( int i = a; i < MAX_DIMS; ++i )
			{
				dim_val[ i ] = 1;
			}
		}

		pos[ dim_sel[ 3 ] ] = dim_val[ 3 ];
		while ( --pos[ dim_sel[ 3 ] ] >= 0 )
		{

			final int[][] g = new int[ dim_val[ 0 ] ][ dim_val[ 1 ] ];
			pos[ dim_sel[ 2 ] ] = dim_val[ 2 ];
			while ( --pos[ dim_sel[ 2 ] ] >= 0 )
			{
				// First Phase
				pos[ dim_sel[ 0 ] ] = dim_val[ 0 ];
				while ( --pos[ dim_sel[ 0 ] ] >= 0 )
				{
					pos[ dim_sel[ 1 ] ] = 0;
					srcAccess.setPosition( pos );
					g[ pos[ dim_sel[ 0 ] ] ][ 0 ] = ( srcAccess.get().getRealDouble() == 1.0 ? dim_val[ 0 ] + dim_val[ 1 ] : 0 );
					for ( pos[ dim_sel[ 1 ] ] = 1; pos[ dim_sel[ 1 ] ] < dim_val[ 1 ]; ++pos[ dim_sel[ 1 ] ] )
					{
						// Scan 1
						srcAccess.setPosition( pos );
						g[ pos[ dim_sel[ 0 ] ] ][ pos[ dim_sel[ 1 ] ] ] = ( srcAccess.get().getRealDouble() == 1.0 ) ? 1 + g[ pos[ dim_sel[ 0 ] ] ][ pos[ dim_sel[ 1 ] ] - 1 ] : 0;

					}
					pos[ dim_sel[ 1 ] ] = dim_val[ 1 ] - 1;
					while ( --pos[ dim_sel[ 1 ] ] >= 0 )
					{ // Scan
						// 2
						final int gnext = g[ pos[ dim_sel[ 0 ] ] ][ pos[ dim_sel[ 1 ] ] + 1 ];
						if ( gnext < g[ pos[ dim_sel[ 0 ] ] ][ pos[ dim_sel[ 1 ] ] ] )
						{
							g[ pos[ dim_sel[ 0 ] ] ][ pos[ dim_sel[ 1 ] ] ] = 1 + gnext;
						}
					}
				}
				// Second Phase
				{
					int q = 0;
					int w;
					pos[ dim_sel[ 1 ] ] = dim_val[ 1 ];
					final int[] s = new int[ dim_val[ 0 ] ];
					final int[] t = new int[ dim_val[ 0 ] ];
					while ( --pos[ dim_sel[ 1 ] ] >= 0 )
					{
						q = 0;
						s[ 0 ] = 0;
						t[ 0 ] = 0;
						for ( int u = 1; u < dim_val[ 0 ]; ++u )
						{ // Scan
							// 3
							while ( q >= 0 )
							{
								final int tqsq = ( t[ q ] - s[ q ] );
								final int gsqy = g[ s[ q ] ][ pos[ dim_sel[ 1 ] ] ];
								final int tqu = ( t[ q ] - u );
								final int guy = g[ u ][ pos[ dim_sel[ 1 ] ] ];
								if ( ( tqsq * tqsq + gsqy * gsqy ) > ( tqu * tqu + guy * guy ) )
								{
									--q;
								}
								else
								{
									break;
								}
							}
							if ( q < 0 )
							{
								q = 0;
								s[ 0 ] = u;
							}
							else
							{
								final int sq = s[ q ];
								final int guy = g[ u ][ pos[ dim_sel[ 1 ] ] ];
								final int gsqy = g[ s[ q ] ][ pos[ dim_sel[ 1 ] ] ];
								w = 1 + ( ( u * u - sq * sq + guy * guy - gsqy * gsqy ) / ( ( u - sq ) << 1 ) );
								if ( w < dim_val[ 0 ] )
								{
									++q;
									s[ q ] = u;
									t[ q ] = w;
								}
							}
						}
						for ( int u = dim_val[ 0 ] - 1; 0 <= u; --u )
						{ // Scan
							// 4
							final int sq = s[ q ];
							final int gsqy = g[ s[ q ] ][ pos[ dim_sel[ 1 ] ] ];
							pos[ dim_sel[ 0 ] ] = u;
							resAccess.setPosition( pos );

							resAccess.get().set( ( float ) ( dim_val[ 2 ] > 1 ? ( u - sq ) * ( u - sq ) + gsqy * gsqy : Math.sqrt( ( ( u - sq ) * ( u - sq ) + gsqy * gsqy ) ) ) );
							if ( u == t[ q ] )
							{
								--q;
							}
						}
					}
				}
			}
			// Second phase for z
			if ( dim_val[ 2 ] > 1 )
			{
				final int[] s = new int[ dim_val[ 2 ] ];
				final int[] t = new int[ dim_val[ 2 ] ];
				final double[] val_z = new double[ dim_val[ 2 ] ];
				int q = 0;
				int w;
				pos[ dim_sel[ 0 ] ] = dim_val[ 0 ];
				while ( --pos[ dim_sel[ 0 ] ] >= 0 )
				{
					resAccess.setPosition( pos[ dim_sel[ 0 ] ], dim_sel[ 0 ] );
					pos[ dim_sel[ 1 ] ] = dim_val[ 1 ];
					while ( --pos[ dim_sel[ 1 ] ] >= 0 )
					{
						resAccess.setPosition( pos[ dim_sel[ 1 ] ], dim_sel[ 1 ] );
						pos[ dim_sel[ 2 ] ] = dim_val[ 2 ];
						while ( --pos[ dim_sel[ 2 ] ] >= 0 )
						{
							q = 0;
							s[ 0 ] = 0;
							t[ 0 ] = 0;
							for ( int u = 1; u < dim_val[ 2 ]; ++u )
							{ // Scan
								// 3
								while ( q >= 0 )
								{
									final int tqsq = ( t[ q ] - s[ q ] );
									resAccess.setPosition( s[ q ], dim_sel[ 2 ] );
									final int gsq = ( int ) resAccess.get().getRealDouble();
									final int tqu = ( t[ q ] - u );
									resAccess.setPosition( u, dim_sel[ 2 ] );
									final int gu = ( int ) resAccess.get().getRealDouble();
									if ( ( tqsq * tqsq + gsq * gsq ) > ( tqu * tqu + gu * gu ) )
									{
										--q;
									}
									else
									{
										break;
									}
								}
								if ( q < 0 )
								{
									q = 0;
									s[ 0 ] = u;
								}
								else
								{
									final int sq = s[ q ];
									resAccess.setPosition( u, dim_sel[ 2 ] );
									final int gu = ( int ) resAccess.get().getRealDouble();
									resAccess.setPosition( s[ q ], dim_sel[ 2 ] );
									final int gsq = ( int ) resAccess.get().getRealDouble();
									w = 1 + ( u * u - sq * sq + gu * gu - gsq * gsq ) / ( ( u - sq ) << 1 );
									if ( w < dim_val[ 2 ] )
									{
										++q;
										s[ q ] = u;
										t[ q ] = w;
									}
								}
							}
							for ( int u = dim_val[ 2 ] - 1; 0 <= u; --u )
							{ // Scan
								// 4
								resAccess.setPosition( u, dim_sel[ 2 ] );
								final int sq = s[ q ];
								resAccess.setPosition( s[ q ], dim_sel[ 2 ] );
								final double gsq = resAccess.get().getRealDouble();
								val_z[ u ] = Math.sqrt( ( ( u - sq ) * ( u - sq ) + gsq * gsq ) );
								if ( u == t[ q ] )
								{
									--q;
								}
							}
						}
						for ( int u = 0; u < dim_val[ 2 ]; ++u )
						{
							resAccess.setPosition( u, dim_sel[ 2 ] );
							resAccess.get().set( ( float ) val_z[ u ] );
						}
					}
				}
			}
		}

		return res;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<FloatType> > copy()
	{
		return new DistanceMap< T >();
	}
}
