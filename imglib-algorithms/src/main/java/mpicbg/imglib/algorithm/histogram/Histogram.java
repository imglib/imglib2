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
import java.util.Hashtable;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.cursor.Cursor;
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
	private Hashtable<HistogramKey<T>, HistogramBin<T>>  hashTable;
	
	/**
	 * The Cursor from which the histogram is to be calculated.
	 */
	private Cursor<T> cursor;
	
	/**
	 * The HistogramBinFactory to use for generating HistogramBin's and
	 * HistogramKey's.
	 */
	private final HistogramBinFactory<T> binFactory;	

	/**
	 * Create a Histogram using the given factory, calculating from the given
	 * Cursor.
	 * @param factory the HistogramBinFactory used to generate  
	 * {@link HistogramKey}s and {@link HistogramBin}s 
	 * @param c a Cursor corresponding to the Image from which the Histogram
	 * will be calculated
	 * 
	 */
	public Histogram(HistogramBinFactory<T> factory, Cursor<T> c)
	{
		cursor = c;
		hashTable = new Hashtable<HistogramKey<T>, HistogramBin<T>>();
		binFactory = factory;		
	}
	
	/**
	 * Returns an ArrayList containing the {@link HistogramKey}s generated
	 * when calculating this Histogram.
	 * @return an ArrayList containing the {@link HistogramKey}s generated
	 * when calculating this Histogram.
	 */
	public ArrayList<HistogramKey<T>> getKeys()
	{
		return new ArrayList<HistogramKey<T>>(hashTable.keySet());
	}

	/**
	 * Returns the center {@link Type} corresponding to each
	 * {@link HistogramKey} generated when calculating this Histogram.
	 * @return
	 */
	public ArrayList<T> getKeyTypes()
	{
		ArrayList<HistogramKey<T>> keys = getKeys();
		ArrayList<T> types = new ArrayList<T>(keys.size());
		for (HistogramKey<T> hk : keys)
		{
			types.add(hk.getType());			
		}
		
		return types;
	}
	
	/**
	 * Returns the bin corresponding to a given {@link Type}.
	 * @param t the Type corresponding to the requested 
	 * {@link HistogramBin}
	 * @return The requested HistogramBin.
	 */
	public HistogramBin<T> getBin(T t)
	{
		return getBin(binFactory.createKey(t));
	}
	
	/**
	 * Returns the bin corresponding to a given {@link HistogramKey}.
	 * @param key the HistogramKey corresponding to the requested 
	 * {@link HistogramBin}
	 * @return The requested HistogramBin.
	 */
	public HistogramBin<T> getBin(HistogramKey<T> key)
	{
		if (hashTable.containsKey(key))
		{
			return hashTable.get(key);
		}
		else
		{
			/*
			 * If the hash table doesn't contain the key in question, create a 
			 * zero bin and return that.
			 */
			HistogramBin<T> zeroBin = binFactory.createBin(key.getType());
			return zeroBin;
		}
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
			//Create a key for the given type
			HistogramKey<T> key = binFactory.createKey(cursor.getType());
			//Grab the HistogramBin corresponding to that key, if it exists.
			HistogramBin<T> bin = hashTable.get(key);
			
			if (bin == null)
			{
				//If there wasn't a bin already, create one and add it to the 
				//hash table.
				bin = binFactory.createBin(key.getType());
				hashTable.put(key, bin);
			}
			
			//Increment the count of the bin.
			bin.inc();
		}
		
		pTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public long getProcessingTime() {		
		return pTime;
	}
	
}
