/*
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

package net.imglib2.converter.readwrite;

import net.imglib2.Sampler;
import net.imglib2.converter.Converters;
import net.imglib2.type.Type;

/**
 * This interface converts a {@link Sampler Sampler&lt;A&gt;} into an instance
 * of {@code B}, where {@code A} and {@code B} are typically {@link Type}s.
 * <p>
 * Its intended use is to create objects that <b>wrap</b> the provided
 * {@code Sampler} itself instead of wrapping values obtained through the
 * {@code Sampler}. In other words, <em>if the {@code Sampler} is pointed at a
 * different object, <b>existing</b> objects returned by this
 * {@code SamplerConverter} should point at it as well</em>.
 * <p>
 * {@code SamplerConverter}s are used in {@link Converters} to create on-the-fly
 * converted images that are both readable and writable. For example, consider
 * accessing a channel of an {@code ARGBType} image as {@code UnsignedByteType}.
 * The converted image needs to provide converted samplers (e.g., {@code
 * RandomAccess<UnsignedByteType>}) that "translate" to the samplers of the
 * original image (e.g., {@code RandomAccess<ARGBType>}).
 * <p>
 * The converted {@code Sampler} returns an {@code UnsignedByteType} instance on
 * {@code get()}. If {@code .setByte(10)} is called on that {@code
 * UnsignedByteType} instance, that must be translated back to the underlying
 * {@code ARGBType}. To achieve this, {@code SamplerConverter.convert(...)}
 * creates a special {@code UnsignedByteType} instance which knows about a
 * specific source sampler (e.g., {@code RandomAccess<ARGBType>}). When the
 * {@code UnsignedByteType} instance is written to (or read from), it gets the
 * current {@code ARGBType} from the source sampler and performs the conversion.
 *
 * @param <A> The type of values accessed by the {@link Sampler}.
 * @param <B> The type of the value returned by this {@link SamplerConverter}.
 */
public interface SamplerConverter< A, B >
{
	B convert( Sampler< ? extends A > sampler );
}
