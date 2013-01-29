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

package net.imglib2.algorithm.scalespace;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.kdtree.KDTree;
import net.imglib2.algorithm.kdtree.RadiusNeighborSearch;
import net.imglib2.algorithm.scalespace.DifferenceOfGaussian.SpecialPoint;
import net.imglib2.type.numeric.RealType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class AdaptiveNonMaximalSuppression<T extends RealType<T>> implements Algorithm, Benchmark
{
	final List<DifferenceOfGaussianPeak<T>> detections;
	double radius;
	
	long processingTime;
	String errorMessage = "";

	/**
	 * Performs adaptive non maximal suppression in the local neighborhood of each detection, seperately 
	 * for minima and maxima. It sets all extrema to invalid if their value is absolutely smaller than any other
	 * in the local neighborhood of a point.
	 * The method getClearedList() can also return an {@link ArrayList} that only contains valid remaining
	 * {@link DifferenceOfGaussianPeak}s.
	 * 
	 * @param detections - the {@link List} of {@link DifferenceOfGaussianPeak}s
	 * @param radius - the radius of the local neighborhood
	 */
	public AdaptiveNonMaximalSuppression( final List<DifferenceOfGaussianPeak<T>> detections, final double radius )
	{
		this.detections = detections;
		this.radius = radius;
		
		processingTime = -1;
	}
	
	/**
	 * Creates a new {@link ArrayList} that only contains all {@link DifferenceOfGaussianPeak}s that are valid.
	 * 
	 * @return - {@link ArrayList} of {@link DifferenceOfGaussianPeak}s
	 */
	public ArrayList<DifferenceOfGaussianPeak<T>> getClearedList()
	{
		final ArrayList<DifferenceOfGaussianPeak<T>> clearedList = new ArrayList<DifferenceOfGaussianPeak<T>>();
		
		for ( final DifferenceOfGaussianPeak<T> peak : detections )
			if ( peak.isValid() )
				clearedList.add( peak );
		
		return clearedList;
	}
	
	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		final KDTree<DifferenceOfGaussianPeak<T>> tree = new KDTree<DifferenceOfGaussianPeak<T>>( detections );
		final RadiusNeighborSearch<DifferenceOfGaussianPeak<T>> search = new RadiusNeighborSearch<DifferenceOfGaussianPeak<T>>( tree );

		for ( final DifferenceOfGaussianPeak<T> det : detections )
		{
			// if it has not been invalidated before we look if it is the highest detection
			if ( det.isValid() )
			{
				final ArrayList<DifferenceOfGaussianPeak<T>> neighbors = search.findNeighborsUnsorted( det, radius );				
				final ArrayList<DifferenceOfGaussianPeak<T>> extrema = new ArrayList<DifferenceOfGaussianPeak<T>>();
				
				for ( final DifferenceOfGaussianPeak<T> peak : neighbors )
					if ( det.isMax() && peak.isMax() ||  det.isMin() && peak.isMin() )
						extrema.add( peak );

				invalidateLowerEntries( extrema, det );
			}
		}
		
		processingTime = System.currentTimeMillis() - startTime;
		
		return true;
	}
	
	protected void invalidateLowerEntries( final ArrayList<DifferenceOfGaussianPeak<T>> extrema, final DifferenceOfGaussianPeak<T> centralPeak )
	{
		final double centralValue = Math.abs( centralPeak.getValue().getRealDouble() );
		
		for ( final DifferenceOfGaussianPeak<T> peak : extrema )
			if ( Math.abs( peak.getValue().getRealDouble() ) < centralValue )
				peak.setPeakType( SpecialPoint.INVALID );		
	}
	
	@Override
	public boolean checkInput()
	{
		if ( detections == null )
		{
			errorMessage = "List<DifferenceOfGaussianPeak<T>> detections is null.";
			return false;
		}
		else if ( detections.size() == 0 )
		{
			errorMessage = "List<DifferenceOfGaussianPeak<T>> detections is empty.";
			return false;			
		}
		else if ( Double.isNaN( radius ) )
		{
			errorMessage = "Radius is NaN.";
			return false;						
		}
		else
			return true;
	}
	
	@Override
	public String getErrorMessage() { return errorMessage; }

	@Override
	public long getProcessingTime() { return processingTime; }
}
