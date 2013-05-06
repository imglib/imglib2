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

import net.imglib2.type.Type;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.util.Util;
import net.imglib2.img.Img;
import net.imglib2.img.ImgCursor;
import net.imglib2.img.ImgRandomAccess;

/**
 * TODO
 *
 */
public class StructuringElementCursor<T extends Type<T>> implements ImgCursor<T> {

	private ImgCursor<?> patchCenterCursor;
	private final ImgRandomAccess<T> cursor;
	private final long[][] path;
	private final int n;
	private final int numDimensions;
	private int pathPos;
	
	/**
	 * centerOffsetPos is used in the case where the strel cursor should 
	 * iterate over a kernel.  In this case, getPosition should return
	 * non-negative values, but we might still want cursor to iterate in the
	 * input image over a patch that is centered about the position indicated
	 * by patchCenterPos.
	 */
	private final long[] kernelOffsetPos;
	
	/**
	 * strelPos holds the strel cursor location, which is returned by getPosition.
	 */
	private long[] strelPos;
	/**
	 * patchCenterPos holds the current location of the patch center cursor.
	 */
	private final long[] patchCenterPos;
	/**
	 * cursorSetPos gets the location that the LocalizableByDimCursor is set to.
	 * This is equivalent to patchCenterPos + strelPos + kernelOffsetPos
	 */
	private final long[] cursorSetPos;
	
	public static long[] halveArray(long[] array)
	{
	    for (int i = 0; i < array.length; ++i)
	    {
	        array[i] /= 2;
	    }
	    return array;
	}
	public static long[] halveIntArray(long[] array)
	{
	    for (int i = 0; i < array.length; ++i)
	    {
	        array[i] /= 2;
	    }
	    return array;
	}

	public static <R extends ComplexType<R>> long[][] imageToPath(
	        final Img<R> im)
	{
	    return imageToPath(im, halveArray(Util.intervalDimensions(im)));
	}
	
	public static <R extends ComplexType<R>> long[][] imageToPath(
	        final Img<R> im, long[] strelImageCenter)
    {
	    ImgCursor<R> cursor = im.cursor();
	    long[] pos = new long[im.numDimensions()];
	    int count = 0;
	    long[][] path;
	    
        if (strelImageCenter == null)
        {
            long[] imDim = Util.intervalDimensions(im);
            strelImageCenter = new long[imDim.length];
            for (int i = 0; i < imDim.length; ++i)
            {
                strelImageCenter[i] = imDim[i] / 2;
            }
        }
        
        while (cursor.hasNext())
        {
            cursor.fwd();            
            if (cursor.get().getRealDouble() != 0)
            {
                ++count;
            }
        }
        
        cursor.reset();
        path = new long[count][im.numDimensions()];
        count = 0;
        
        while (cursor.hasNext())
        {
            cursor.fwd();            
            if (cursor.get().getRealDouble() != 0)
            {      
               cursor.localize(pos);
                for (int i = 0; i < pos.length; ++i)
                {
                    pos[i] -= strelImageCenter[i];
                }
                System.arraycopy(pos, 0, path[count], 0, path[count].length);
                ++count;
            }
        }

        return path;
    }
	
	public static long[][] sizeToPath(final long[] size)
	{
	    return sizeToPath(size, halveIntArray(size.clone()));
	}
	
	public static long[][] sizeToPath(final long[] size, long[] patchCenter)
	{
	    int n = 1;
	    int d = size.length;
	    long[][] path;
	    
	    for (long s : size)
	    {
	        n *= (int)s;
	    }

	    path = new long[n][d];
	    
        if (patchCenter == null)
        {
            patchCenter = new long[d];
            for (int j = 0; j < d; ++j)
            {
                patchCenter[j] = size[j] / 2;
            }
        }
        	    
	    for (int j = 0; j < d; ++j)
	    {
	        path[0][j] = -patchCenter[j];
	    }
	    	    	    
	    for (int i = 1; i < n; ++i)
	    {
	        int j = 0;
	        System.arraycopy(path[i - 1], 0, path[i], 0, d);
	        
	        path[i][0]++;
	        
	        while(path[i][j] >= (size[j] - patchCenter[j]) && j < d - 1)
	        {
	            path[i][j] = -patchCenter[j];
	            path[i][j+1]++;	            
	            j++;	            
	        }	       
	    }
	    
	    return path;
	}
	
	public <R extends ComplexType<R>> 
	    StructuringElementCursor(final ImgRandomAccess<T> cursor, 
            final Img<R> strelImage) {
        this(cursor, strelImage, null);
    }
	
	public <R extends ComplexType<R>> 
	    StructuringElementCursor(final ImgRandomAccess<T> cursor, 
            final Img<R> strelImage, final long[] strelImageCenter) {
        this(cursor, imageToPath(strelImage, strelImageCenter));
    }
	
	public StructuringElementCursor(final ImgRandomAccess<T> cursor,
            final long[] size)
    {
        this(cursor, sizeToPath(size, null));
    }
	
	public StructuringElementCursor(final ImgRandomAccess<T> cursor,
	        final long[] size, final long[] patchCenter)
	{
	    this(cursor, sizeToPath(size, patchCenter));
	}
	
	public StructuringElementCursor(final ImgRandomAccess<T> cursor,
	        final StructuringElementCursor<?> strelCursor)
	{
	    this(cursor, strelCursor.path);
	}
	
	public StructuringElementCursor(final ImgRandomAccess<T> cursor, 
	        final long[][] inPath) {
		this.patchCenterCursor = cursor.getImg().cursor();
		this.cursor = cursor;
		this.n = inPath.length;
		numDimensions = inPath[0].length;		
		path = new long[n][numDimensions];
		patchCenterPos = new long[numDimensions];
		cursorSetPos = new long[numDimensions];
		kernelOffsetPos = new long[numDimensions];
		Arrays.fill(kernelOffsetPos, 0);
		
		for (int j = 0; j < n; ++j)
		{
		    System.arraycopy(inPath[j], 0, path[j], 0, inPath[j].length);
		}
						
		reset();
		
		while (hasNext())
		{
		    fwd();
		    System.out.println(getPositionAsString());
		}
		System.out.println();
		reset();
	}
	
	public void setKernelOffset(final long[] ko)
	{
	    System.arraycopy(ko, 0, kernelOffsetPos, 0, kernelOffsetPos.length);
	}
	
	public void centerKernel(final long[] dim)
	{	    
	    System.arraycopy(dim, 0, kernelOffsetPos, 0, kernelOffsetPos.length);
	    halveIntArray(kernelOffsetPos);
	}
	
	public void setPatchCenterCursor(final ImgCursor<?> newPCC)
	{
	    patchCenterCursor = newPCC;
	    reset();
	}
	
	public ImgCursor<?> getPatchCenterCursor()
	{
	    return patchCenterCursor;
	}

	@Override
	public T get() {		
		return cursor.get();
	}

	@Override
	public void reset() {
		pathPos = -1;
		patchCenterCursor.localize(patchCenterPos);
		
		for (int i = 0; i < numDimensions; ++i)
		{
		    patchCenterPos[i] -= kernelOffsetPos[i];
		}
	}
	
	@Override
	public boolean hasNext() {		
		return pathPos + 1 < n;
	}

	@Override
	public void fwd() {
		++pathPos;
		strelPos = path[pathPos];
		
		for (int j = 0; j < numDimensions; ++j)
		{
		    cursorSetPos[j] = patchCenterPos[j] + strelPos[j];
		}
		
		cursor.setPosition(cursorSetPos);
		
	}

	public boolean patchHasNext()
	{
	    return patchCenterCursor.hasNext();
	}
	
	public boolean patchFwd()
	{
	    if (patchCenterCursor.hasNext())
	    {
	        patchCenterCursor.fwd();
	        reset();
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}
	
	public void patchReset()
	{
	    patchCenterCursor.reset();
	    reset();
	}
	
	@Override
	public void localize(final int[] position) {
		System.arraycopy(strelPos, 0, position, 0, numDimensions);
	}

	@Override
	public int getIntPosition(final int dim) {
		return (int) strelPos[dim];
	}
	
	@Override
	public long getLongPosition(final int dim) {
		return strelPos[(int)dim];
	}

	public String getPositionAsString() {
	    String pos = "(" + strelPos[ 0 ];
        
        for (int d = 1; d < numDimensions; d++ )
        {
            pos += ", " + strelPos[ d ];
        }
        
        pos += ")";
        
        return pos;
	}	
	
	public int getPathLength()
	{
	    return n;
	}

	@Override
	public Img<T> getImg() {
		return cursor.getImg();
	}

	@Override
	public T getType() {
		return cursor.get();
	}

	@Override
	public int numDimensions() {
		return cursor.numDimensions();
	}

	@Override
	public long min(int d) {
		return cursor.min(d);
	}

	@Override
	public void min(long[] min) {
		cursor.min(min);
	}

	@Override
	public long max(int d) {
		return cursor.max(d);
	}

	@Override
	public void max(long[] max) {
		cursor.max(max);
	}

	@Override
	public void dimensions(long[] dimensions) {
		cursor.dimensions(dimensions);
	}

	@Override
	public long dimension(int d) {
		return cursor.dimension(d);
	}

	@Override
	public double realMin(int d) {
		return cursor.realMin(d);
	}

	@Override
	public void realMin(double[] min) {
		cursor.realMin(min);
	}

	@Override
	public double realMax(int d) {
		return cursor.realMax(d);
	}

	@Override
	public void realMax(double[] max) {
		cursor.realMax(max);
	}

	@Override
	public void localize(float[] position) {
		cursor.localize(position);
	}

	@Override
	public void localize(double[] position) {
		cursor.localize(position);
	}

	@Override
	public float getFloatPosition(int dim) {
		return cursor.getFloatPosition(dim);
	}

	@Override
	public double getDoublePosition(int dim) {
		return cursor.getDoublePosition(dim);
	}

	@Override
	public void localize(long[] position) {
		cursor.localize(position);
	}

	@Override
	public void jumpFwd(long steps) {
		cursor.move(Util.getArrayFromValue(steps, numDimensions()));
	}
	@Override
	public T next() {
		fwd();
		return get();
	}
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
