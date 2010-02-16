/**
 * Copyright (c) 2009--2010, Johannes Schindelin
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
 * @author Johannes Schindelin & Stephan Preibisch
 */
package mpicbg.imglib.container.imageplus;

import ij.ImagePlus;

import mpicbg.imglib.container.Container3D;
import mpicbg.imglib.container.ContainerImpl;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimOutsideCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public abstract class ImagePlusContainer<T extends Type<T>> extends ContainerImpl<T> implements Container3D<T>
{
	final ImagePlusContainerFactory factory;
	final int width, height, depth;

	ImagePlusContainer( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );		
		
		this.factory = factory;
		this.width = dim[ 0 ];
		
		if( dim.length < 2 )
			this.height = 1;
		else
			this.height = dim[ 1 ];
		
		if ( dim.length < 3 )
			this.depth = 1;
		else
			this.depth = dim[ 2 ];

/*		int i = 0;
		width = image.getWidth();
		height = image.getHeight();
		channels = image.getNChannels();
		slices = image.getNSlices();
		frames = image.getNFrames();

		dimensions = new int[2
			+ (channels > 1 ? 1 : 0)
			+ (slices > 1 ? 1 : 0)
			+ (frames > 1 ? 1 : 0)];
		dimensions[i++] = width;
		dimensions[i++] = height;
		if (channels > 1) dimensions[i++] = channels;
		if (slices > 1) dimensions[i++] = slices;
		if (frames > 1) dimensions[i++] = frames;
*/
	}
	
	protected static int[] getCorrectDimensionality( final ImagePlus imp )
	{
		int numDimensions = 3;
				
		if ( imp.getStackSize() == 1 )
			--numDimensions;
		
		if ( imp.getHeight() == 1 )
			--numDimensions;
		
		final int[] dim = new int[ numDimensions ];
		dim[ 0 ] = imp.getWidth();

		if ( numDimensions >= 2 )
			dim[ 1 ] = imp.getHeight();
		
		if ( numDimensions == 3 )
			dim[ 2 ] = imp.getStackSize();
		
		return dim;
	}

	@Override
	public int getWidth() { return width; }
	@Override
	public int getHeight() { return height; }
	@Override
	public int getDepth() { return depth; }

	public final int getPos( final int[] l ) 
	{
		if ( numDimensions > 1 )
			return l[ 1 ] * width + l[ 0 ];
		else
			return l[ 0 ];
	}	
	
	public abstract ImagePlus getImagePlus();

	public Cursor<T> createCursor( T type, Image<T> image ) 
	{
		return new ImagePlusCursor<T>( this, image, type );
	}

	public LocalizableCursor<T> createLocalizableCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizableCursor<T>( this, image, type );
	}
;
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizablePlaneCursor<T>( this, image, type );
	}
;
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image ) 
	{
		return new ImagePlusLocalizableByDimCursor<T>( this, image, type );
	}
;
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( T type, Image<T> image, OutsideStrategyFactory<T> outsideFactory ) 
	{
		return new ImagePlusLocalizableByDimOutsideCursor<T>( this, image, type, outsideFactory );
	}
	
	public ImagePlusContainerFactory getFactory() { return factory; }
}
