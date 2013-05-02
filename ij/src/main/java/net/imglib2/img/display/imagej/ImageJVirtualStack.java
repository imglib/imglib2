/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package net.imglib2.img.display.imagej;

import ij.ImagePlus;
import ij.VirtualStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.display.XYProjector;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.view.Views;

/**
 * TODO
 *
 */
public abstract class ImageJVirtualStack< S, T extends NativeType< T > > extends VirtualStack
{
	final private XYProjector< S, T > projector;

	final private int size;
	final private int numDimensions;
	final private long[] higherSourceDimensions;

	final private int bitDepth;

	final protected ImageProcessor imageProcessor;

	protected ImageJVirtualStack( final RandomAccessibleInterval< S > source, final Converter< S, T > converter, final T type, final int ijtype )
	{
		super( ( int ) source.dimension( 0 ), getDimension1Size( source ), null, null );

		assert source.numDimensions() > 1;

		int tmpsize = 1;
		for ( int d = 2; d < source.numDimensions(); ++d )
			tmpsize *= (int)source.dimension(d);
		this.size = tmpsize;

		final int sizeX = ( int ) source.dimension( 0 );
		final int sizeY = getDimension1Size( source );

		final ArrayImg< T, ? > img = new ArrayImgFactory< T >().create( new long[] { sizeX, sizeY }, type );

		higherSourceDimensions = new long[3];
		higherSourceDimensions[ 0 ] = ( source.numDimensions() > 2 ) ? source.dimension( 2 ) : 1;
		higherSourceDimensions[ 1 ] = ( source.numDimensions() > 3 ) ? source.dimension( 3 ) : 1;
		higherSourceDimensions[ 2 ] = ( source.numDimensions() > 4 ) ? source.dimension( 4 ) : 1;
		this.numDimensions = source.numDimensions();

		// if the source interval is not zero-min, we wrap it into a view that translates it to the origin
		this.projector = new XYProjector< S, T >( Views.isZeroMin( source ) ? source : Views.zeroMin( source ), img, converter );

		switch ( ijtype )
		{
		case ImagePlus.GRAY8:
			this.bitDepth = 8;
			imageProcessor = new ByteProcessor( sizeX, sizeY, ( byte[] ) ( ( ArrayDataAccess< ? > ) img.update( null ) ).getCurrentStorageArray(), null );
			break;
		case ImagePlus.GRAY16:
			this.bitDepth = 16;
			imageProcessor = new ShortProcessor( sizeX, sizeY, ( short[] ) ( ( ArrayDataAccess< ? > ) img.update( null ) ).getCurrentStorageArray(), null );
			break;
		case ImagePlus.COLOR_RGB:
			this.bitDepth = 24;
			imageProcessor = new ColorProcessor( sizeX, sizeY, ( int[] ) ( ( ArrayDataAccess< ? > ) img.update( null ) ).getCurrentStorageArray() );
			break;
		case ImagePlus.GRAY32:
			this.bitDepth = 32;
			imageProcessor = new FloatProcessor( sizeX, sizeY, ( float[] ) ( ( ArrayDataAccess< ? > ) img.update( null ) ).getCurrentStorageArray(), null );
			// ip.setMinAndMax( display.getMin(), display.getMax() );
			break;
		default:
			throw new IllegalArgumentException( "unsupported color type " + ijtype );
		}
	}
	
	/**
	 * Get the size of the Y-dimension. If it is a one-dimensional source, return 1.
	 * 
	 * @param interval
	 * @return 1 if only-dimensional, else the size
	 */
	protected static int getDimension1Size( final Interval interval )
	{
		if ( interval.numDimensions() == 1 )
			return 1;

		return ( int ) interval.dimension( 1 );
	}

	/**
	 * Returns an ImageProcessor for the specified slice, were 1<=n<=nslices.
	 * Returns null if the stack is empty.
	 */
	@Override
	public ImageProcessor getProcessor( final int n )
	{
		if ( numDimensions > 2 )
		{
			final int[] position = new int[3];
			IntervalIndexer.indexToPosition( n - 1, higherSourceDimensions, position );
			projector.setPosition( position[0], 2 );
			if ( numDimensions > 3 )
				projector.setPosition( position[1], 3 );
			if ( numDimensions > 4 )
				projector.setPosition( position[2], 4 );
		}

		projector.map();
		return imageProcessor;
	}

	@Override
	public int getBitDepth()
	{
		return bitDepth;
	}

	/** Obsolete. Short images are always unsigned. */
	@Override
	public void addUnsignedShortSlice( final String sliceLabel, final Object pixels )
	{}

	/** Adds the image in 'ip' to the end of the stack. */
	@Override
	public void addSlice( final String sliceLabel, final ImageProcessor ip )
	{}

	/**
	 * Adds the image in 'ip' to the stack following slice 'n'. Adds the slice
	 * to the beginning of the stack if 'n' is zero.
	 */
	@Override
	public void addSlice( final String sliceLabel, final ImageProcessor ip, final int n )
	{}

	/** Deletes the specified slice, were 1<=n<=nslices. */
	@Override
	public void deleteSlice( final int n )
	{}

	/** Deletes the last slice in the stack. */
	@Override
	public void deleteLastSlice()
	{}

	/**
	 * Updates this stack so its attributes, such as min, max, calibration table
	 * and color model, are the same as 'ip'.
	 */
	@Override
	public void update( final ImageProcessor ip )
	{}

	/** Returns the pixel array for the specified slice, were 1<=n<=nslices. */
	@Override
	public Object getPixels( final int n )
	{
		return getProcessor( n ).getPixels();
	}

	/**
	 * Assigns a pixel array to the specified slice, were 1<=n<=nslices.
	 */
	@Override
	public void setPixels( final Object pixels, final int n )
	{}

	/**
	 * Returns the stack as an array of 1D pixel arrays. Note that the size of
	 * the returned array may be greater than the number of slices currently in
	 * the stack, with unused elements set to null.
	 */
	@Override
	public Object[] getImageArray()
	{
		return null;
	}

	/**
	 * Returns the slice labels as an array of Strings. Note that the size of
	 * the returned array may be greater than the number of slices currently in
	 * the stack. Returns null if the stack is empty or the label of the first
	 * slice is null.
	 */
	@Override
	public String[] getSliceLabels()
	{
		return null;
	}

	/**
	 * Returns the label of the specified slice, were 1<=n<=nslices. Returns
	 * null if the slice does not have a label. For DICOM and FITS stacks,
	 * labels may contain header information.
	 */
	@Override
	public String getSliceLabel( final int n )
	{
		return "" + n;
	}

	/**
	 * Returns a shortened version (up to the first 60 characters or first
	 * newline and suffix removed) of the label of the specified slice. Returns
	 * null if the slice does not have a label.
	 */
	@Override
	public String getShortSliceLabel( final int n )
	{
		return getSliceLabel( n );
	}

	/** Sets the label of the specified slice, were 1<=n<=nslices. */
	@Override
	public void setSliceLabel( final String label, final int n )
	{}

	/** Returns true if this is a 3-slice RGB stack. */
	@Override
	public boolean isRGB()
	{
		return false;
	}

	/** Returns true if this is a 3-slice HSB stack. */
	@Override
	public boolean isHSB()
	{
		return false;
	}

	/**
	 * Returns true if this is a virtual (disk resident) stack. This method is
	 * overridden by the VirtualStack subclass.
	 */
	@Override
	public boolean isVirtual()
	{
		return true;
	}

	/** Frees memory by deleting a few slices from the end of the stack. */
	@Override
	public void trim()
	{}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	public void setBitDepth( final int bitDepth )
	{}

	@Override
	public String getDirectory()
	{
		return null;
	}

	@Override
	public String getFileName( final int n )
	{
		return null;
	}
}
