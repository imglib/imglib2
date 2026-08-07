/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2020 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.position;

import net.imglib2.type.Type;

/**
 * Convenience methods to create some typical functions in integer and real
 * space.
 *
 * @author Stephan Saalfeld &lt;saalfelds@janelia.hhmi.org&gt;
 *
 */
public interface Functions
{
	/**
	 * Creates a one pixel checker board pattern.
	 *
	 * @param <T>
	 * @param n number of dimensions
	 * @param on on value
	 * @param off off value (at 0^n)
	 *
	 * @return
	 */
	public static < T extends Type< T > > FunctionRandomAccessible< T > checker(
			final int n,
			final T on,
			final T off)
	{
		return new FunctionRandomAccessible<>(
				n,
				(position, value ) -> {
					boolean isOn = ( position.getLongPosition( 0 ) & 1 ) == 1;
					for ( int d = 1; d < n; ++d ) {
						isOn ^= ( position.getLongPosition( d ) & 1 ) == 1;
					}
					if ( isOn )
						value.set( on );
					else
						value.set( off );
				},
				on::createVariable );
	}

	/**
	 * Creates a checker board pattern with a step size of 2<sup>bit</sup>.
	 *
	 * @param <T>
	 * @param n number of dimensions
	 * @param on on value
	 * @param off off value (at 0^n)
	 * @param bit the size of blocks is 2<sup>bit</sup>
	 *
	 * @return
	 */
	public static < T extends Type< T > > FunctionRandomAccessible< T > checker(
			final int n,
			final T on,
			final T off,
			final int bit )
	{
		return new FunctionRandomAccessible<>(
				n,
				(position, value ) -> {
					boolean isOn = ( ( position.getLongPosition( 0 ) >> bit ) & 1 ) == 1;
					for ( int d = 1; d < n; ++d ) {
						isOn ^= ( ( position.getLongPosition( d ) >> bit ) & 1 ) == 1;
					}
					if ( isOn )
						value.set( on );
					else
						value.set( off );
				},
				on::createVariable );
	}

	/**
	 * Creates a one pixel checker board pattern.
	 *
	 * @param <T>
	 * @param n number of dimensions
	 * @param on on value
	 * @param off off value (at 0^n)
	 *
	 * @return
	 */
	public static < T extends Type< T > > FunctionRealRandomAccessible< T > realChecker(
			final int n,
			final T on,
			final T off)
	{
		return new FunctionRealRandomAccessible<>(
				n,
				(position, value ) -> {
					boolean isOn = ( ( long )Math.floor( position.getDoublePosition( 0 ) ) & 1 ) == 1;
					for ( int d = 1; d < n; ++d ) {
						isOn ^= ( ( long )Math.floor( position.getDoublePosition( d ) ) & 1 ) == 1;
					}
					if ( isOn )
						value.set( on );
					else
						value.set( off );
				},
				on::createVariable );
	}
}
