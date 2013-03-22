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

import net.imglib2.algorithm.histogram.HistogramBinMapper;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * Created by IntelliJ IDEA.
 * User: larry
 * Date: 2/7/11
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 *
 */
public class MedianHistogramFilter<T extends RealType<T>> extends HistogramStatistics<T>
{

    private final HistogramBinMapper<T> mapper;

    public MedianHistogramFilter(final Img<T> imageIn,
            long[] size, OutOfBoundsFactory<T,Img<T>> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        this(imageIn, StructuringElementCursor.sizeToPath(size), oobFactory, binMapper);
    }

    public MedianHistogramFilter(final Img<T> imageIn,
            long[][] path, OutOfBoundsFactory<T,Img<T>> oobFactory,
            HistogramBinMapper<T> binMapper)
    {
        super(imageIn, path, oobFactory, binMapper);
        setName(imageIn + " Median Histogram Filter");
        mapper = binMapper;
    }

	public MedianHistogramFilter(final Img<T> imageIn,
	        long[] size, HistogramBinMapper<T> binMapper) {
		this(imageIn, StructuringElementCursor.sizeToPath(size), binMapper);
	}

	public MedianHistogramFilter(final Img<T> imageIn,
	        long[][] path, HistogramBinMapper<T> binMapper)
	{
	    super(imageIn, path, binMapper);
	    setName(imageIn + " Median Histogram Filter");
        mapper = binMapper;
	}

	@Override
	protected void statsOp(final T outputType) {
	    final int n = super.getIntArray().length;
        final int t = (n + 1) / 2;
        int count = 0, i;
        for (i = 0; i < n && count < t; ++i)
        {
            count += super.getIntArray()[i];
        }
		outputType.set(mapper.invMap(i));
	}

}
