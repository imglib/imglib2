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

import mpicbg.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.algorithm.MultiThreadedBenchmarkAlgorithm;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.algorithm.gauss.Gauss;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.multithreading.Chunk;
import net.imglib2.multithreading.SimpleMultiThreading;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public class CoherenceEnhancingDiffusionTensor2D< T extends RealType< T >> extends MultiThreadedBenchmarkAlgorithm implements OutputAlgorithm< Img< FloatType >>
{

	/*
	 * FIELDS
	 */

	private static final String BASE_ERROR_MESSAGE = "[" + CoherenceEnhancingDiffusionTensor2D.class.getSimpleName() + "] ";

	private final Img< T > input;

	/**
	 * Stores the diffusions tensor. The tensor is stored as a multi-C image,
	 * one channel per component of the tensor. This is the part that takes a
	 * lot of memory. We need this because we compute the diffusion tensor at
	 * once, then use it in the iterative part.
	 */
	private Img< FloatType > D;

	private final double sigma = 2;

	private final double rho = 4;

	/** Anisotropic diffusion ratio in Weickert equation. */
	private final double alpha = 1e-3;

	private final double C = 1;

	private final int m = 1;

	/*
	 * CONSTRUCTOR
	 */

	public CoherenceEnhancingDiffusionTensor2D( final Img< T > input )
	{
		this.input = input;
	}

	/*
	 * METHODS
	 */

	@Override
	public Img< FloatType > getResult()
	{
		return D;
	}

	@Override
	public boolean process()
	{

		/* 0. Instantiate tensor holder, and initialize cursors. */
		final long[] tensorDims = new long[ input.numDimensions() + 1 ];
		for ( int i = 0; i < input.numDimensions(); i++ )
		{
			tensorDims[ i ] = input.dimension( i );
		}
		tensorDims[ input.numDimensions() ] = 3;
		try
		{
			D = input.factory().imgFactory( new FloatType() ).create( tensorDims, new FloatType() );
		}
		catch ( final IncompatibleTypeException e )
		{
			errorMessage = BASE_ERROR_MESSAGE + "Failed to create tensor holder:\n" + e.getMessage();
			return false;
		}

		/* 1. Create a smoothed version of the input. */
		final Img< FloatType > smoothed = Gauss.toFloat( new double[] { sigma, sigma }, input );

		/* 2. Compute the gradient of the smoothed input, but only algon X & Y */
		final boolean[] doDimension = new boolean[ input.numDimensions() ];
		doDimension[ 0 ] = true;
		doDimension[ 1 ] = true;
		final Gradient< FloatType > gradientCalculator = new Gradient< FloatType >( smoothed, doDimension );
		gradientCalculator.process();
		final Img< FloatType > gradient = gradientCalculator.getResult();

		/* 3. Compute the structure tensor. */

		final Img< FloatType > J = D.factory().create( D, new FloatType() );
		final int newDim = input.numDimensions();

		final Vector< Chunk > chunks = SimpleMultiThreading.divideIntoChunks( input.size(), numThreads );
		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

		for ( int i = 0; i < threads.length; i++ )
		{

			final Chunk chunk = chunks.get( i );
			threads[ i ] = new Thread( "" + BASE_ERROR_MESSAGE + "thread " + i )
			{

				@Override
				public void run()
				{

					float ux, uy;
					final Cursor< T > cursor = input.localizingCursor();
					final RandomAccess< FloatType > grad_ra = gradient.randomAccess();
					final RandomAccess< FloatType > J_ra = J.randomAccess();

					cursor.jumpFwd( chunk.getStartPosition() );
					for ( long k = 0; k < chunk.getLoopSize(); k++ )
					{

						cursor.fwd();
						for ( int i = 0; i < input.numDimensions(); i++ )
						{
							grad_ra.setPosition( cursor.getLongPosition( i ), i );
							J_ra.setPosition( cursor.getLongPosition( i ), i );
						}

						grad_ra.setPosition( 0, newDim );
						ux = grad_ra.get().get();
						grad_ra.fwd( newDim );
						uy = grad_ra.get().get();

						J_ra.setPosition( 0, newDim );
						J_ra.get().set( ux * ux );
						J_ra.fwd( newDim );
						J_ra.get().set( ux * uy );
						J_ra.fwd( newDim );
						J_ra.get().set( uy * uy );
					}
				}
			};
		}

		SimpleMultiThreading.startAndJoin( threads );

		/* 3.5 Smoooth the structure tensor. */

		Gauss.inFloat( new double[] { rho, rho, 0 }, J );

		/* 4. Construct Diffusion tensor. */

		for ( int i = 0; i < threads.length; i++ )
		{

			final Chunk chunk = chunks.get( i );
			threads[ i ] = new Thread( "" + BASE_ERROR_MESSAGE + "thread " + i )
			{

				@Override
				public void run()
				{

					final Cursor< T > cursor = input.localizingCursor();
					final RandomAccess< FloatType > J_ra = J.randomAccess();
					final RandomAccess< FloatType > D_ra = D.randomAccess();

					float Jxx, Jxy, Jyy;
					double tmp, v1x, v1y, v2x, v2y, mag, mu1, mu2, lambda1, lambda2, di;
					double newLambda1, newLambda2, Dxx, Dxy, Dyy;
					double scale;

					cursor.jumpFwd( chunk.getStartPosition() );
					for ( long k = 0; k < chunk.getLoopSize(); k++ )
					{

						cursor.fwd();

						for ( int j = 0; j < input.numDimensions(); j++ )
						{
							D_ra.setPosition( cursor.getLongPosition( j ), j );
							J_ra.setPosition( cursor.getLongPosition( j ), j );
						}

						// Compute eigenvalues

						J_ra.setPosition( 0, newDim );
						Jxx = J_ra.get().get();
						J_ra.fwd( newDim );
						Jxy = J_ra.get().get();
						J_ra.fwd( newDim );
						Jyy = J_ra.get().get();

						tmp = Math.sqrt( ( Jxx - Jyy ) * ( Jxx - Jyy ) + 4 * Jxy * Jxy );
						v2x = 2 * Jxy;
						v2y = Jyy - Jxx + tmp;

						mag = Math.sqrt( v2x * v2x + v2y * v2y );
						v2x /= mag;
						v2y /= mag;

						v1x = -v2y;
						v1y = v2x;

						mu1 = 0.5 * ( Jxx + Jyy + tmp );
						mu2 = 0.5 * ( Jxx + Jyy - tmp );

						// Large one in abs values must be the 2nd
						if ( Math.abs( mu2 ) > Math.abs( mu1 ) )
						{

							lambda1 = mu1;
							lambda2 = mu2;

						}
						else
						{

							lambda1 = mu2;
							lambda2 = mu1;

						}

						di = lambda2 - lambda1;
						scale = Util.pow( di * di, m );
						newLambda1 = alpha + ( 1 - alpha ) * Math.exp( -C / scale );
						newLambda2 = alpha;

						Dxx = newLambda1 * v1x * v1x + newLambda2 * v2x * v2x;
						Dxy = newLambda1 * v1x * v1y + newLambda2 * v2x * v2x;
						Dyy = newLambda1 * v1y * v1y + newLambda2 * v2y * v2y;

						D_ra.setPosition( 0, 2 );
						D_ra.get().setReal( Dxx );
						D_ra.fwd( 2 );
						D_ra.get().setReal( Dxy );
						D_ra.fwd( 2 );
						D_ra.get().setReal( Dyy );

					}
				}
			};
		}

		SimpleMultiThreading.startAndJoin( threads );

		return true;

	}

	@Override
	public boolean checkInput()
	{
		return true;
	}

}
