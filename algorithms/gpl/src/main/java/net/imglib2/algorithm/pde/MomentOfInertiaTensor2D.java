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
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.pde;

import java.util.Vector;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.region.localneighborhood.RectangleCursor;
import net.imglib2.algorithm.region.localneighborhood.RectangleNeighborhoodGPL;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorExpWindowingFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

/**
 * A class to compute a diffusion tensor for anisotropic diffusion, based on
 * moment of inertia.
 * <p>
 * A neighborhood of a given scale is inspected at each pixel location, and the
 * moment of inertia are calculated. This yields a <code>2x2</code> real
 * symmetric matrix <code>[ Ixx Ixy ; Ixy Iyy ] </code> that can be diagonalized
 * to find the preferred directions of the local linear structures. The
 * eigenvalues and eigenvectors are then used to build a diffusion tensor that
 * privileges diffusion only in the direction of the structures, and that can be
 * used elsewhere in a anisotropic diffusion scheme. Here we implement the idea
 * outlined in the following paper:
 * <p>
 * <tt>  <i>Nonlinear anisotropic diffusion filtering of three-dimensional image data from two-photon microscopy</i>
 * Philip. J. Broser, R. Schulte, S. Lang, A. Roth Fritjof, Helmchen, J. Waters, Bert Sakmann, and G. Wittum, <b>J. Biomed. Opt. 9, 1253 (2004)</b>, 
 * DOI:10.1117/1.1806832 </tt>
 * <p>
 * This class limits itself to build a 2D tensor. The source image needs not to
 * be 2D, but only a 2D neighborhood will be iterated to compute moment of
 * inertia. Therefore the later will be made of only 3 components at each point:
 * <code>Dxx, Dxy, Dyy</code>
 * 
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar 30, 2012
 * 
 * @param <T>
 */
public class MomentOfInertiaTensor2D< T extends RealType< T >> extends MultiThreadedBenchmarkAlgorithm implements OutputAlgorithm< Img< FloatType >>
{

	private static final double DEFAULT_EPSILON_1 = 1;

	private static final double DEFAULT_EPSILON_2 = 1e-3;

	private static final String BASE_ERROR_MESSAGE = "[" + MomentOfInertiaTensor2D.class.getSimpleName() + "] ";

	private final RandomAccessibleInterval< T > input;

	private final double epsilon_1;

	private final double epsilon_2;

	private final int scale;

	private Img< FloatType > D;

	private ImgFactory< FloatType > imgFactory;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Sets the image factory to be either the ImgFactory of the input with FloatType, or
	 * if types are incompatible, a default {@ArrayImgFactory}<FloatType>.
	 * @param input
	 * @param scale
	 * @param epsilon_1
	 * @param epsilon_2
	 * @deprecated Use
	 *             {@link #MomentOfInertiaTensor2D(RandomAccessibleInterval, ImgFactory, int, double, double)}
	 *             and specify the {@link ImgFactory}<Float> for the output.
	 */
	@Deprecated
	public MomentOfInertiaTensor2D( Img< T > input, int scale, double epsilon_1, double epsilon_2 )
	{
		this( input, chooseFactory( input ), scale, epsilon_1, epsilon_2 );
	}

	//TODO: Remove with above
	private static ImgFactory< FloatType > chooseFactory( Img< ? > input )
	{
		try
		{
			return input.factory().imgFactory( new FloatType() );
		}
		catch ( IncompatibleTypeException e )
		{
			return new ArrayImgFactory< FloatType >();
		}
	}

	/**
	 * Calls {@link #MomentOfInertiaTensor2D(RandomAccessibleInterval, int, double, double) }
	 * @param input
	 * @param scale
	 * @deprecated Use
	 *             {@link #MomentOfInertiaTensor2D(RandomAccessibleInterval, ImgFactory, int)}
	 *             and specify the {@link ImgFactory}<Float> for the output.
	 */
	@Deprecated
	public MomentOfInertiaTensor2D( Img< T > input, int scale )
	{
		this( input, scale, DEFAULT_EPSILON_1, DEFAULT_EPSILON_2 );
	}

	public MomentOfInertiaTensor2D( RandomAccessibleInterval< T > input, ImgFactory< FloatType > imgFactory, int scale )
	{
		this( input, imgFactory, scale, DEFAULT_EPSILON_1, DEFAULT_EPSILON_2 );
	}

	public MomentOfInertiaTensor2D( RandomAccessibleInterval< T > input, ImgFactory< FloatType > imgFactory, int scale, double epsilon_1, double epsilon_2 )
	{
		this.input = input;
		this.scale = scale;
		this.epsilon_1 = epsilon_1;
		this.epsilon_2 = epsilon_2;
		this.imgFactory = imgFactory;
	}

	/*
	 * METHODS
	 */

	@Override
	public boolean checkInput()
	{
		return true;
	}

	@Override
	public boolean process()
	{

		// Instantiate tensor holder, and initialize cursors
		long[] tensorDims = new long[ input.numDimensions() + 1 ];
		for ( int i = 0; i < input.numDimensions(); i++ )
		{
			tensorDims[ i ] = input.dimension( i );
		}
		tensorDims[ input.numDimensions() ] = 3;
		final int tensorDim = input.numDimensions(); // the dim to write the
														// tensor components to.

		D = imgFactory.create( tensorDims, new FloatType() );

		Vector< Chunk > chunks = SimpleMultiThreading.divideIntoChunks( Views.iterable( input ).size(), numThreads );
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

		for ( int i = 0; i < threads.length; i++ )
		{

			final Chunk chunk = chunks.get( i );

			threads[ i ] = new Thread( "" + BASE_ERROR_MESSAGE + "thread " + i )
			{

				@Override
				public void run()
				{

					Cursor< T > cursor = Views.iterable( input ).localizingCursor();
					RandomAccess< FloatType > Dcursor = D.randomAccess();

					// Main cursor position
					final long[] position = new long[ input.numDimensions() ];
					// Neighborhood position
					final long[] pos = new long[ input.numDimensions() ];

					long[] domain = new long[ input.numDimensions() ];
					domain[ 0 ] = ( scale - 1 ) / 2;
					domain[ 1 ] = ( scale - 1 ) / 2; // iterate only over X & Y,
														// but for all pixels

					OutOfBoundsFactory< T, RandomAccessibleInterval< T >> oobf = new OutOfBoundsMirrorExpWindowingFactory< T, RandomAccessibleInterval< T >>( ( scale - 1 ) / 2 );
					RectangleNeighborhoodGPL< T > neighborhood = new RectangleNeighborhoodGPL< T >( input, oobf );
					neighborhood.setSpan( domain );

					RectangleCursor< T > neighborhoodCursor = neighborhood.cursor();

					cursor.jumpFwd( chunk.getStartPosition() );
					for ( long j = 0; j < chunk.getLoopSize(); j++ )
					{

						cursor.fwd();
						cursor.localize( position );

						// Move the tensor to the right position (but for last
						// dim)
						for ( int i = 0; i < position.length; i++ )
						{
							Dcursor.setPosition( position[ i ], i );
						}

						double mass, x, y, x2, y2;
						// double z, z2;
						double totalmass = 0;

						// Compute center of mass position
						double cmx = 0;
						double cmy = 0;

						neighborhood.setPosition( position );
						neighborhoodCursor.reset();
						while ( neighborhoodCursor.hasNext() )
						{

							neighborhoodCursor.fwd();
							neighborhoodCursor.localize( pos );

							mass = neighborhoodCursor.get().getRealDouble();
							totalmass += mass;

							cmx += mass * pos[ 0 ];
							cmy += mass * pos[ 1 ];

						}

						if ( totalmass > 0 )
						{
							cmx /= totalmass;
							cmy /= totalmass;
						}

						// Compute inertia moments
						double Ixx = 0;
						double Ixy = 0;
						double Iyy = 0;

						neighborhoodCursor.reset();
						while ( neighborhoodCursor.hasNext() )
						{

							neighborhoodCursor.fwd();
							neighborhoodCursor.localize( pos );

							x = ( pos[ 0 ] - cmx );
							y = ( pos[ 1 ] - cmy );
							x2 = x * x;
							y2 = y * y;
							mass = neighborhoodCursor.get().getRealDouble();

							Ixx += mass * x2;
							Iyy += mass * y2;
							Ixy -= mass * x * y;
						}

						double[] arr = PdeUtil.realSymetricMatrix2x2( Ixx, Iyy, Ixy );
						double mu_1 = arr[ 0 ];
						double mu_2 = arr[ 1 ];
						double cosalpha = arr[ 2 ];
						double sinalpha = arr[ 3 ];

						double lambda_1, lambda_2;
						if ( mu_1 == mu_2 )
						{
							lambda_1 = epsilon_1;
							lambda_2 = epsilon_1;
						}
						else
						{
							lambda_1 = epsilon_1;
							lambda_2 = epsilon_2;
						}

						// Diffusion tensor [ a b ; b c ]
						double a = lambda_1 * cosalpha * cosalpha + lambda_2 * sinalpha * sinalpha;
						double b = -( lambda_1 - lambda_2 ) * cosalpha * sinalpha;
						double c = lambda_1 * sinalpha * sinalpha + lambda_2 * cosalpha * cosalpha;

						// Store
						Dcursor.setPosition( 0, tensorDim );
						Dcursor.get().setReal( a );
						Dcursor.fwd( tensorDim );
						Dcursor.get().setReal( b );
						Dcursor.fwd( tensorDim );
						Dcursor.get().setReal( c );
					}
				}
			};

		}

		SimpleMultiThreading.startAndJoin( threads );

		return true;
	}

	@Override
	public Img< FloatType > getResult()
	{
		return D;
	}

}
