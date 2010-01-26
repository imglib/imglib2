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
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.BitContainer;
import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.container.basictypecontainer.CharContainer;
import mpicbg.imglib.container.basictypecontainer.DoubleContainer;
import mpicbg.imglib.container.basictypecontainer.FloatContainer;
import mpicbg.imglib.container.basictypecontainer.IntContainer;
import mpicbg.imglib.container.basictypecontainer.LongContainer;
import mpicbg.imglib.container.basictypecontainer.ShortContainer;
import mpicbg.imglib.type.Type;

public class CubeContainerFactory extends ContainerFactory
{
	protected int[] cubeSize;
	protected int standardCubeSize = 10;

	public CubeContainerFactory()
	{
	}
	
	public CubeContainerFactory( final int cubeSize )
	{
		this.standardCubeSize = cubeSize;
	}
	
	public CubeContainerFactory( final int[] cubeSize )
	{
		if ( cubeSize == null || cubeSize.length == 0 )
		{
			System.err.println("CubeContainerFactory(): cubeSize is null. Using equal cube size of 10.");
			this.cubeSize = null;
			return;
		}
		
		for ( int i = 0; i < cubeSize.length; i++ )
		{
			if ( cubeSize[ i ] <= 0 )
			{
				System.err.println("CubeContainerFactory(): cube size in dimension " + i + " is <= 0, using a size of " + standardCubeSize + ".");
				cubeSize[ i ] = standardCubeSize;
			}
		}
		
		this.cubeSize = cubeSize;
	}
	
	protected int[] checkDimensions( int dimensions[] )
	{
		if ( dimensions == null || dimensions.length == 0 )
		{
			System.err.println("CubeContainerFactory(): dimensionality is null. Creating a 1D cube with size 1.");
			dimensions = new int[]{1};
		}

		for ( int i = 0; i < dimensions.length; i++ )
		{
			if ( dimensions[ i ] <= 0 )
			{
				System.err.println("CubeContainerFactory(): size of dimension " + i + " is <= 0, using a size of 1.");
				dimensions[ i ] = 1;
			}
		}

		return dimensions;
	}
	
	protected int[] checkCubeSize( int[] cubeSize, int[] dimensions )
	{		
		if ( cubeSize == null )
		{
			cubeSize = new int[ dimensions.length ];
			for ( int i = 0; i < cubeSize.length; i++ )
				cubeSize[ i ] = standardCubeSize;
			
			return cubeSize;
		}
		
		if ( cubeSize.length != dimensions.length )
		{
			System.err.println("CubeContainerFactory(): dimensionality of image is unequal to dimensionality of cubes, adjusting cube dimensionality.");
			int[] cubeSizeNew = new int[ dimensions.length ];
			
			for ( int i = 0; i < dimensions.length; i++ )
			{
				if ( i < cubeSize.length )
					cubeSizeNew[ i ] = cubeSize[ i ];
				else
					cubeSizeNew[ i ] = standardCubeSize;
			}
					
			return cubeSizeNew;
		}
		
		return cubeSize;
	}

	@Override
	public <T extends Type<T>>BitContainer<T> createBitInstance( int[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new BitCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}
	
	@Override
	public <T extends Type<T>>ByteContainer<T> createByteInstance( int[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new ByteCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>CharContainer<T> createCharInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new CharCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>DoubleContainer<T> createDoubleInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new DoubleCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>FloatContainer<T> createFloatInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new FloatCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>IntContainer<T> createIntInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new IntCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>LongContainer<T> createLongInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new LongCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>>ShortContainer<T> createShortInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cubeSize = checkCubeSize( this.cubeSize, dimensions );
		
		return new ShortCube<T>( this, dimensions, cubeSize, entitiesPerPixel );
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printProperties()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameters(String configuration)
	{
		// TODO Auto-generated method stub
		
	}

}
