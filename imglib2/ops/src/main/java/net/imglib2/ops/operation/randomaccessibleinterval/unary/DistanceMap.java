/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2010
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   30 Dec 2010 (hornm): created
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
 * @author metznerj, University of Konstanz
 */
public class DistanceMap< T extends RealType< T >, K extends RandomAccessibleInterval< T >, M extends RandomAccessibleInterval< FloatType >> implements UnaryOperation< K, M >
{

	/**
	 *
	 */
	public final static int MAX_DIMS = 4;

	/**
	 *
	 */
	public final static int MIN_DIMS = 2;

	/**
	 * {@inheritDoc}
	 * 
	 * @return
	 */
	@Override
	public M compute( final K src, final M res )
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
					if ( src instanceof CalibratedSpace )
						dim_unit[ a ] = ( ( CalibratedSpace ) src ).calibration( i );
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
	public UnaryOperation< K, M > copy()
	{
		return new DistanceMap< T, K, M >();
	}
}
