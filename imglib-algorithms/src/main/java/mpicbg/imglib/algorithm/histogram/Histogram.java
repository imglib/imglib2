/**
 * Copyright (c) 2010, Larry Lindsey
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Larry Lindsey
 */
package mpicbg.imglib.algorithm.histogram;

import java.util.ArrayList;
import java.util.Arrays;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.Type;

/**
 * Implements a Histogram over an Image.
 */
public class Histogram <T extends Type<T>> implements Algorithm, Benchmark
{
	/**
	 * Processing time, milliseconds.
	 */
	private long pTime = 0;
	
	/**
	 * Hold the histogram itself.
	 */
	private final int[] histogram;
	
	/**
	 * The Cursor from which the histogram is to be calculated.
	 */
	private final Cursor<T> cursor;
	
	/**
	 * The HistogramBinFactory to use for generating HistogramBin's and
	 * HistogramKey's.
	 */
	private final HistogramBinMapper<T> binMapper;	

	/**
	 * Create a Histogram using the given factory, calculating from the given
	 * Cursor.
	 * @param factory the HistogramBinFactory used to generate  
	 * {@link HistogramKey}s and {@link HistogramBin}s 
	 * @param c a Cursor corresponding to the Image from which the Histogram
	 * will be calculated
	 * 
	 */
	public Histogram(final HistogramBinMapper<T> mapper,
			final Cursor<T> c)
	{		
		cursor = c;
		binMapper = mapper;
		histogram = new int[binMapper.getNumBins()];
	}
	
	public Histogram(final HistogramBinMapper<T> mapper,
			final Image<T> image)
	{
		this(mapper, image.createCursor());
	}
	
	public void reset()
	{
		Arrays.fill(histogram, 0);
		cursor.reset();
	}
	
	
	/**
	 * Returns the bin corresponding to a given {@link Type}.
	 * @param t the Type corresponding to the requested 
	 * {@link HistogramBin}
	 * @return The requested HistogramBin.
	 */
	public int getBin(final T t)
	{
		return histogram[binMapper.map(t)];
	}
	
	public int[] getHistogram()
	{
		return histogram; 
	}
	
	public ArrayList<T> getBinCenters()
	{
		ArrayList<T> binCenters = new ArrayList<T>(histogram.length);
		for (int i = 0; i < histogram.length; ++i)
		{
			binCenters.set(i, binMapper.invMap(i));
		}
		
		return binCenters;
	}
	
	@Override
	public boolean checkInput() {		
		return true;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public boolean process() {
		long startTime = System.currentTimeMillis();
		
		while (cursor.hasNext())
		{			
			cursor.fwd();
			histogram[binMapper.map(cursor.getType())]++;
		}
		
		pTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public long getProcessingTime() {		
		return pTime;
	}
	
}
