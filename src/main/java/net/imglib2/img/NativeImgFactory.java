/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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
 * #L%
 */

package net.imglib2.img;

import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.util.Fraction;

/**
 * TODO
 * 
 */
public abstract class NativeImgFactory< T extends NativeType< T > > extends ImgFactory< T >
{
	/**
	 * This class will ask the {@link Type} to create a suitable {@link Img} for
	 * the {@link Type} and the dimensionality.
	 * 
	 * {@link Type} will then call one of the abstract methods defined below to
	 * create the {@link NativeImg}
	 * 
	 * @return {@link Img} - the instantiated Container
	 */
	@Override
	public NativeImg< T, ? > create( final long[] dim, final T type )
	{
		return type.createSuitableNativeImg( this, dim );
	}

	/* basic type containers */
	public abstract NativeImg< T, ? extends ByteAccess > createByteInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends CharAccess > createCharInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends ShortAccess > createShortInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends IntAccess > createIntInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends LongAccess > createLongInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends FloatAccess > createFloatInstance( long[] dimensions, Fraction entitiesPerPixel );

	public abstract NativeImg< T, ? extends DoubleAccess > createDoubleInstance( long[] dimensions, Fraction entitiesPerPixel );
}
