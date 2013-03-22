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

package net.imglib2.algorithm.roi;

import java.util.Arrays;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * OrderStatistics provides the framework to create Order Statistic operations.  It operates
 * by cursing over the input {@link Image}, and collecting a sorted list of the pixels "covered" by
 * a {@link StructuringElement}.  This list is made available to children classes, which are
 * responsible for setting the pixel value at the current position in the output Image.
 * 
 * @param <T> The input- and output-{@link Image} type.
 * @author Larry Lindsey
 */
public abstract class OrderStatistics<T extends RealType<T>> extends ROIAlgorithm<T, T> {
	//Member variables
	
	private final double[] statArray;
	public long statsTime;
	
	//Member functions
	
	public OrderStatistics(final Img<T> imageIn, long[][] path) {
        this(imageIn, path, new OutOfBoundsConstantValueFactory<T,Img<T>>(imageIn.firstElement().createVariable()));
    }
	
	public OrderStatistics(final Img<T> imageIn, long[][] path,
                           OutOfBoundsFactory<T,Img<T>> oobFactory) {
		super(imageIn.factory(), imageIn.firstElement().createVariable(),
		        new StructuringElementCursor<T>(
		                imageIn.randomAccess(oobFactory), path));
		statsTime = 0;
		statArray = new double[path.length];
	}

	public void collectStats(final StructuringElementCursor <T> cursor)
    {
        int i = 0;
        while(cursor.hasNext())
        {
            cursor.fwd();
            statArray[i++] = cursor.getType().getRealDouble();
        }

        Arrays.sort(statArray);
    }
	
	protected double[] getArray()
	{
	    return statArray;
	}
			
	@Override
	protected boolean patchOperation(
            final StructuringElementCursor<T> cursor,
            final T outputType) {
	    long p = System.currentTimeMillis();
		collectStats(cursor);
		statsTime += System.currentTimeMillis() - p;
		statsOp(outputType);
		
		return true;
	}
		
	/**
	 * Perform the order statistic operation, then set the value of the given type.
	 * @param outputType the type whose value is to be set.  Belongs to the output Image.
	 */
	protected abstract void statsOp(final T outputType);
	
}
