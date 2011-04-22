/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package net.imglib2.image.display.imagej;

import java.util.ArrayList;
import java.util.Collection;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.type.Type;
import mpicbg.models.InvertibleCoordinateTransform;
import mpicbg.models.NoninvertibleModelException;

public class ImageJVirtualDisplay<T extends Type<T>> extends ImageStack
{
	final Collection<InverseTransformDescription<T>> transformDescription;
	final int type;
	final int[] dimensionPositions;

	final int dimX, dimY, dimZ;
	final int sizeX, sizeY, sizeZ;
	
	final ArrayList<SliceTransformableExtraction<T>> threadList = new ArrayList<SliceTransformableExtraction<T>>();

	double min, max;
	
	ImagePlus parent = null;
	
	public ImageJVirtualDisplay( final Collection<InverseTransformDescription<T>> interpolators, final int[] dimensions, 
			                     final int type, final int[] dim, final int[] dimensionPositions )
	{
		super( dimensions[ 0 ], dimensions[ 1 ], dimensions[ 2 ] );
		
		this.transformDescription = interpolators;
		this.dimX = dim[ 0 ];
		this.dimY = dim[ 1 ];
		this.dimZ = dim[ 2 ];
		this.dimensionPositions = dimensionPositions.clone();
		
		this.type = type;
		
		this.sizeX = dimensions[ 0 ];
		this.sizeY = dimensions[ 1 ];
		this.sizeZ = dimensions[ 2 ];
		
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		
		for ( InverseTransformDescription<T> it : interpolators )
		{
			if ( it.getImage().getDisplay().getMax() > max )
				max = it.getImage().getDisplay().getMax();
			
			if ( it.getImage().getDisplay().getMin() < min )
				min = it.getImage().getDisplay().getMin();
		}
	}
	
	public void setParent( ImagePlus parent ) { this.parent = parent; }
	public Collection<InverseTransformDescription<T>> getTransformDescription() { return transformDescription; }	
    
    /** Returns an ImageProcessor for the specified slice,
    were 1<=n<=nslices. Returns null if the stack is empty.
     */
	public ImageProcessor getProcessor( final int n ) 
	{
	    final ImageProcessor ip;
	    
	    if (n<1 || n>sizeZ)
	        throw new IllegalArgumentException("no slice " + n);
	    
	    if (sizeZ==0)
	        return null;
	    
	    switch(type) 
	    {
	    	/*case ImagePlus.GRAY8:
	    		ip = new ByteProcessor(size[0], size[1], extractSliceByte( img, display, n-1, dim, size, dimensionPositions, true ), null); break;
	     	case ImagePlus.COLOR_RGB:
	    		ip = new ColorProcessor( sizeX, sizeY, extractSliceRGBA( n-1 ) ); break;*/
	    	default:
	    		ip = new FloatProcessor( sizeX, sizeY, extractSliceFloat( n-1 ), null ); 
	    		ip.setMinAndMax( min, max );
	    		break;
	    }
	    
	    return ip;
	}

    public float[] extractSliceFloat( final int slice )
    {
    	// store the slice image
    	final float[] sliceImg = new float[ sizeX * sizeY ];
    	
    	if ( parent == null )
    		return sliceImg;

		for  ( SliceTransformableExtraction<T> thread : threadList )
			thread.stopThread();
    	
    	threadList.clear();
    	    	      
        for ( InverseTransformDescription<T> it : transformDescription )
        {
        	SliceTransformableExtractionFloat<T> thread = new SliceTransformableExtractionFloat<T>( transformDescription.size(), it, sliceImg, parent, dimensionPositions, dimX, dimY, dimZ, sizeX, sizeY, slice); 
			threadList.add( thread );
        }
        
		for( SliceTransformableExtraction<T> thread : threadList )
		{				
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();				
		}			

        return sliceImg;
    }
    
    public float[] extractSliceFloatSingleThreaded( final int slice )
    {    	
    	// store the slice image
    	final float[] sliceImg = new float[ sizeX * sizeY ];
    	
    	// store the current position
    	final float[] initialPosition = new float[ dimensionPositions.length ];
    	    	
    	for ( int d = 0; d < initialPosition.length; ++d )
    		initialPosition[ d ] = dimensionPositions[ d ];

    	if ( dimZ < initialPosition.length )
    		initialPosition[ dimZ ] = slice;

    	final float[] position = initialPosition.clone();
    	
		for ( final InverseTransformDescription<T> desc : transformDescription )
		{
			final RealRandomAccess<T> it = desc.getInterpolatorFactory().create( desc.getImage() );
			final InvertibleCoordinateTransform transform = desc.getTransform();
	    	final float[] offset = desc.getOffset();
	
			try
			{
		    	final T type = it.get().createVariable();
		    	final Display<T> display = it.getImage().getDisplay();
	
		    	int i = 0;
		    	
		    	for ( int y = 0; y < sizeY; y++ )
		    	{
		        	if ( dimY < initialPosition.length )
		        		initialPosition[ dimY ] = y;

		        	for ( int x = 0; x < sizeX; x++ )
		        	{
			        	for ( int d = 0; d < initialPosition.length; ++d )
			        		position[ d ] = initialPosition[ d ] + offset[ d ];

			        	position[ dimX ] = x + offset[ dimX ];

			        	transform.applyInverseInPlace( position );
			        	it.setPosition( position );
			        	
		        		sliceImg[ i ] += display.get32Bit(type);
			        	++i;
		        	}
		    	}
			}
			catch ( NoninvertibleModelException e )
			{
				System.out.println( it + " has a no invertible model: " + e );
			}
		}
     	
    	return sliceImg;
    }
    
 	/** Obsolete. Short images are always unsigned. */
    public void addUnsignedShortSlice(String sliceLabel, Object pixels) {}
   
	/** Adds the image in 'ip' to the end of the stack. */
	public void addSlice(String sliceLabel, ImageProcessor ip) {}
   
    /** Adds the image in 'ip' to the stack following slice 'n'. Adds
       the slice to the beginning of the stack if 'n' is zero. */
    public void addSlice(String sliceLabel, ImageProcessor ip, int n) {}
   
    /** Deletes the specified slice, were 1<=n<=nslices. */
    public void deleteSlice(int n) {}
   
    /** Deletes the last slice in the stack. */
    public void deleteLastSlice() {}
       
    /** Updates this stack so its attributes, such as min, max,
        calibration table and color model, are the same as 'ip'. */
    public void update(ImageProcessor ip) {}
   
    /** Returns the pixel array for the specified slice, were 1<=n<=nslices. */
    public Object getPixels(int n) { return getProcessor(n).getPixels(); }
   
    /** Assigns a pixel array to the specified slice,
        were 1<=n<=nslices. */
    public void setPixels(Object pixels, int n) {}
   
    /** Returns the stack as an array of 1D pixel arrays. Note
        that the size of the returned array may be greater than
        the number of slices currently in the stack, with
        unused elements set to null. */
    public Object[] getImageArray() { return null; }
   
    /** Returns the slice labels as an array of Strings. Note
        that the size of the returned array may be greater than
        the number of slices currently in the stack. Returns null
        if the stack is empty or the label of the first slice is null.  */
    public String[] getSliceLabels() { return null; }
   
    /** Returns the label of the specified slice, were 1<=n<=nslices.
        Returns null if the slice does not have a label. For DICOM
        and FITS stacks, labels may contain header information. */
    public String getSliceLabel(int n) { return "" + n; }
   
    /** Returns a shortened version (up to the first 60 characters or first newline and 
        suffix removed) of the label of the specified slice.
        Returns null if the slice does not have a label. */
    public String getShortSliceLabel(int n) { return getSliceLabel(n); }

    /** Sets the label of the specified slice, were 1<=n<=nslices. */
    public void setSliceLabel(String label, int n) {}

    /** Returns true if this is a 3-slice RGB stack. */
    public boolean isRGB() { return false; }
   
    /** Returns true if this is a 3-slice HSB stack. */
    public boolean isHSB() { return false; }

    /** Returns true if this is a virtual (disk resident) stack. 
        This method is overridden by the VirtualStack subclass. */
    public boolean isVirtual() { return true; }

    /** Frees memory by deleting a few slices from the end of the stack. */
    public void trim() {}

    public String toString() { return ("Virtual Display of " + transformDescription.size() + " Interpolators"); }	
}
