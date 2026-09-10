/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

/**
 * A block of primitive data and corresponding per-pixel validity.
 * <p>
 * Every {@code VolatileArray} holds a {@code byte[]} buffer for storing
 * per-pixel validity. Like the data buffer, this is allocated once and reused
 * (e.g., when {@code VolatileArray} is obtained from {@link TempArray}).
 * <p>
 * In addition, {@code VolatileArray} may have a {@link Validity} set.
 * <ul>
 * <li>{@code null} means <em>this array's own buffer is the validity</em>. This
 *     is the state a {@code VolatileArray} is handed out in, and the state a
 *     producer leaves it in when it writes per-pixel validity directly.</li>
 * <li>A {@link RangeValidity} is the compact form produced by a block copy. It
 *     is expanded into the own buffer lazily, on the first {@link #valid()}
 *     call, and not at all if only {@link Validity#allValid()} is ever asked.</li>
 * <li>A {@link DenseValidity} may be backed by this array's own buffer or by a
 *     foreign one (after {@link #setValidity}, which is how a pass-through stage
 *     forwards validity without copying it).</li>
 * </ul>
 * Instances are not thread-safe.
 *
 * @param <T>
 * 		primitive array type of the data, e.g. {@code byte[]}
 */
public final class VolatileArray< T >
{
	private final T data;

	/**
	 * A per-pixel validity buffer. Allocated on construction and potentially
	 * longer than current {@link #length} if this {@code VolatileArray} is
	 * reused.
	 * <p>
	 * This is a buffer owned by this {@code VolatileArray} for materializing
	 * and potentially backing {@code validity}.
	 * <p>
	 * Note that {@link #valid()} does <em>not</em> necessarily return this
	 * buffer!
	 */
	private final byte[] ownValid;

	/**
	 * Current length.
	 * (May differ from capacity if this {@code VolatileArray} is reused.)
	 */
	private int length;

	/**
	 * The current {@code Validity}. {@code null} is equivalent to a {@code
	 * DenseValidity} over {@link #ownValid}.
	 */
	private Validity validity;

	/**
	 * Whether {@link #ownValid} currently holds the expansion of {@link #validity}.
	 */
	private boolean expanded;

	public VolatileArray( final T data, final byte[] valid )
	{
		this.data = data;
		this.ownValid = valid;
		this.length = valid.length;
	}

	VolatileArray( final PrimitiveTypeProperties< T, ? > props, final int length )
	{
		this.data = props.allocate( length );
		this.ownValid = new byte[ length ];
		this.length = length;
	}

	public T data()
	{
		return data;
	}

	/**
	 * Capacity of the {@code data} and {@code ownValid} arrays.
	 */
	int capacity()
	{
		return ownValid.length;
	}

	/**
	 * Prepare this {@code VolatileArray} for reuse: set the length and clear
	 * {@link #validity}, so that by default our {@link #ownValid} buffer is
	 * used.
	 */
	void reset( final int length )
	{
		this.length = length;
		this.validity = null;
		this.expanded = true;
	}

	/**
	 * Per-pixel validity, for reading.
	 * <p>
	 * If the current {@link Validity} is a {@link RangeValidity}, it is expanded
	 * into this array's own buffer on the first call. If it is a {@link
	 * DenseValidity} backed by a foreign buffer, <em>that</em> buffer is
	 * returned, so the result is not necessarily our {@link #ownValid} buffer.
	 * Do not write through it; use {@link #validForWriting()} instead.
	 * <p>
	 * If no {@code Validity} is set, {@link #ownValid} is returned as-is. Its
	 * contents are then whatever was last written there (the buffer is not
	 * cleared for a reused {@code VolatileArray}). Producers are expected to
	 * either {@link #setValidity} or fill the buffer completely (via
	 * {@link #validForWriting}).
	 */
	public byte[] valid()
	{
		if ( validity instanceof DenseValidity )
			return ( ( DenseValidity ) validity ).array();
		ensureExpanded();
		return ownValid;
	}

	/**
	 * Per-pixel validity, for writing in place.
	 * <p>
	 * In contrast to {@link #valid()} this always returns {@link #ownValid}.
	 * <p>
	 * If a {@link #validity} is set, it's guaranteed to be materialized into
	 * {@link #ownValid} (at least once). This may mean expanding a {@link
	 * RangeValidity}, or copying a {@link DenseValidity} that is backed by a
	 * foreign buffer.
	 */
	public byte[] validForWriting()
	{
		ensureExpanded();
		// Everything except a DenseValidity over ownValid would be left
		// describing the pre-write state, so drop it.
		if ( !backedByOwnValid() )
			validity = null;
		return ownValid;
	}

	/**
	 * Whether {@link #validity} is a {@link DenseValidity} backed by our
	 * {@link #ownValid} buffer, that is, whether writing through {@code
	 * ownValid} writes through {@code validity}.
	 */
	private boolean backedByOwnValid()
	{
		return validity instanceof DenseValidity && ( ( DenseValidity ) validity ).array() == ownValid;
	}

	private void ensureExpanded()
	{
		if ( expanded )
			return;
		if ( validity != null )
			validity.expandTo( ownValid );
		expanded = true;
	}

	/**
	 * The current {@link Validity}. If none is set, a {@link DenseValidity}
	 * over {@link #ownValid} is created.
	 */
	public Validity getValidity()
	{
		if ( validity == null )
			validity = new DenseValidity( ownValid, length );
		return validity;
	}

	/**
	 * Set the {@link Validity}.
	 * <p>
	 * <em>Potential foot gun: Nothing is copied, so a subsequent {@link
	 * #valid()} may return a buffer owned by another {@code VolatileArray}.
	 * That other {@code VolatileArray} must not be reused while we still use
	 * its buffer.</em>
	 */
	public void setValidity( final Validity validity )
	{
		assert validity == null || validity.length() == length;
		this.validity = validity;
		expanded = backedByOwnValid();
	}
}
