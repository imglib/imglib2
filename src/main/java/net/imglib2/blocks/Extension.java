/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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
package net.imglib2.blocks;

import static net.imglib2.outofbounds.OutOfBoundsMirrorFactory.Boundary.SINGLE;

import net.imglib2.blocks.ViewNode.ExtensionViewNode;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.outofbounds.OutOfBoundsZeroFactory;
import net.imglib2.type.operators.SetZero;
import net.imglib2.util.Cast;

interface Extension
{
	enum Type
	{
		CONSTANT(true),
		BORDER(false),
		MIRROR_SINGLE(false),
		MIRROR_DOUBLE(false),
		UNKNOWN( true );

		private final boolean isValueDependent;

		Type( final boolean isValueDependent )
		{
			this.isValueDependent = isValueDependent;
		}

		/**
		 * Whether this extension depends on the pixel value. E.g., {@code
		 * BORDER}, {@code MIRROR_SINGLE}, {@code MIRROR_DOUBLE} are only
		 * dependent on position ({@code isValueDependent()==false}), while
		 * {@code CONSTANT} is dependent on the out-of-bounds value ({@code
		 * isValueDependent()==true}).
		 */
		public boolean isValueDependent()
		{
			return isValueDependent;
		}
	}

	Type type();

	static Extension border()
	{
		return ExtensionImpl.border;
	}

	static Extension mirrorSingle()
	{
		return ExtensionImpl.mirrorSingle;
	}

	static Extension mirrorDouble()
	{
		return ExtensionImpl.mirrorDouble;
	}

	static < T > Extension constant( T oobValue )
	{
		return new ExtensionImpl.ConstantExtension<>( oobValue );
	}

	static Extension of( OutOfBoundsFactory< ?, ? > oobFactory )
	{
		if ( oobFactory instanceof OutOfBoundsBorderFactory )
		{
			return border();
		}
		else if ( oobFactory instanceof OutOfBoundsMirrorFactory )
		{
			final OutOfBoundsMirrorFactory.Boundary boundary = ( ( OutOfBoundsMirrorFactory< ?, ? > ) oobFactory ).getBoundary();
			return boundary == SINGLE ? mirrorSingle() : mirrorDouble();
		}
		else if ( oobFactory instanceof OutOfBoundsConstantValueFactory )
		{
			return constant( ( ( OutOfBoundsConstantValueFactory ) oobFactory ).getValue() );
		}
		else
		{
			return new ExtensionImpl.UnknownExtension<>( oobFactory );
		}
	}

	static Extension of( ExtensionViewNode node )
	{
		OutOfBoundsFactory< ?, ? > oobFactory = node.getOutOfBoundsFactory();
		if ( oobFactory instanceof OutOfBoundsZeroFactory )
		{
			final net.imglib2.type.Type< ? > type = Cast.unchecked( node.view().getType() );
			final SetZero zero = Cast.unchecked( type.createVariable() );
			zero.setZero();
			oobFactory = new OutOfBoundsConstantValueFactory<>( zero );
		}
		return of( oobFactory );
	}
}
