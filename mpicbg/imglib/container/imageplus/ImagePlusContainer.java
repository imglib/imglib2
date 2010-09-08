/**
 * Copyright (c) 2009--2010, Stephan Preibisch, Stephan Saalfeld & Johannes Schindelin
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
 */
package mpicbg.imglib.container.imageplus;

import java.util.ArrayList;

import ij.ImagePlus;

import mpicbg.imglib.container.AbstractDirectAccessContainer;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.exception.ImgLibException;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.sampler.RasterPlaneIterator;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.sampler.RasterSampler;
import mpicbg.imglib.sampler.imageplus.ImagePlusBasicRasterIterator;
import mpicbg.imglib.sampler.imageplus.ImagePlusLocalizingRasterIterator;
import mpicbg.imglib.sampler.imageplus.ImagePlusRasterPlaneIterator;
import mpicbg.imglib.sampler.imageplus.ImagePlusPositionableRasterSampler;
import mpicbg.imglib.sampler.imageplus.ImagePlusOutOfBoundsPositionableRasterSampler;
import mpicbg.imglib.sampler.imageplus.ImagePlusStorageAccess;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * @param <A>
 * 
 * @author Stephan Preibisch, Stephan Saalfeld and Johannes Schindelin
 */
public class ImagePlusContainer< T extends Type< T >, A extends ArrayDataAccess< A > >
		extends AbstractDirectAccessContainer< T, A >
		implements Container< T >
{
	final ImagePlusContainerFactory factory;

	final int width, height, depth;

	final ArrayList< A > mirror;

	ImagePlusContainer( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );

		this.factory = factory;
		this.width = dim[ 0 ];

		if ( dim.length < 2 ) this.height = 1;
		else this.height = dim[ 1 ];

		if ( dim.length < 3 ) this.depth = 1;
		else this.depth = dim[ 2 ];

		mirror = new ArrayList< A >( depth );
	}

	ImagePlusContainer( final ImagePlusContainerFactory factory, final A creator, final int[] dim, final int entitiesPerPixel )
	{
		this( factory, dim, entitiesPerPixel );

		for ( int i = 0; i < depth; ++i )
			mirror.add( creator.createArray( width * height * entitiesPerPixel ) );
	}

	public ImagePlus getImagePlus() throws ImgLibException
	{
		throw new ImgLibException( this, "has no ImagePlus instance, it is not a standard type of ImagePlus" );
	}

	@Override
	public A update( final RasterSampler< ? > c )
	{
		return mirror.get( ( ( ImagePlusStorageAccess ) c ).getStorageIndex() );
	}

	protected static int[] getCorrectDimensionality( final ImagePlus imp )
	{
		int numDimensions = 3;

		if ( imp.getStackSize() == 1 ) --numDimensions;

		if ( imp.getHeight() == 1 ) --numDimensions;

		final int[] dim = new int[ numDimensions ];
		dim[ 0 ] = imp.getWidth();

		if ( numDimensions >= 2 ) dim[ 1 ] = imp.getHeight();

		if ( numDimensions == 3 ) dim[ 2 ] = imp.getStackSize();

		return dim;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getDepth()
	{
		return depth;
	}

	public final int getPos( final int[] l )
	{
		if ( numDimensions > 1 ) return l[ 1 ] * width + l[ 0 ];
		else return l[ 0 ];
	}

	@Override
	public RasterIterator< T > createRasterIterator( final Image< T > image )
	{
		return new ImagePlusBasicRasterIterator< T >( this, image );
	}

	@Override
	public RasterIterator< T > createLocalizingRasterIterator( final Image< T > image )
	{
		return new ImagePlusLocalizingRasterIterator< T >( this, image );
	}

	@Override
	public RasterPlaneIterator< T > createRasterPlaneIterator( final Image< T > image )
	{
		return new ImagePlusRasterPlaneIterator< T >( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public PositionableRasterSampler< T > createPositionableRasterSampler( final Image< T > image )
	{
		return new ImagePlusPositionableRasterSampler< T >( this, image );
	}

	@Override
	public ImagePlusOutOfBoundsPositionableRasterSampler< T > createPositionableRasterSampler( final Image< T > image, OutOfBoundsStrategyFactory< T > outOfBoundsFactory )
	{
		return new ImagePlusOutOfBoundsPositionableRasterSampler< T >( this, image, outOfBoundsFactory );
	}

	@Override
	public ImagePlusContainerFactory getFactory()
	{
		return factory;
	}

	@Override
	public void close()
	{
		for ( final A array : mirror )
			array.close();
	}
}
