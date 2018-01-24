/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.img.array;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgs;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.type.numeric.real.DoubleType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Performance benchmark to demonstrate {@link LoopBuilder} performance.
 *
 * @author Matthias Arzt
 */
@State( Scope.Benchmark )
public class ArrayRandomAccessPolymorphismBenchmark
{

	Img< DoubleType > inArray = ArrayImgs.doubles( 300, 300 );
	Img< DoubleType > inPlaner = PlanarImgs.doubles( 300, 300 );
	Img< DoubleType > inCell = new CellImgFactory<DoubleType>().create(DoubleType::new, 300, 300 );
	Img< DoubleType > out = initOut( inArray, inPlaner, inCell );

	private ArrayImg< DoubleType, DoubleArray > initOut( Img< DoubleType > in, Img< DoubleType > in2, Img< DoubleType > in3 )
	{
		ArrayImg< DoubleType, DoubleArray > out = ArrayImgs.doubles( 300, 300 );
		// NB: copy and setPosition, need to be applied to different Cursors / Localizables.
		// The just in time compile would otherwise perform unrealistic optimisations.
		for ( int i = 0; i < 10; i++ )
		{
			copy( in, out );
			copy( in2, out );
			copy( in3, out );
		}
		return out;
	}

	@Benchmark
	public void copyLocalizingCursorArray()
	{
		final Cursor< DoubleType > cursor = inArray.localizingCursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();
		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}

	@Benchmark
	public void copyLocalizingCursorPlanar()
	{
		final Cursor< DoubleType > cursor = inPlaner.localizingCursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();
		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}

	@Benchmark
	public void copyLocalizingCursorCell()
	{
		final Cursor< DoubleType > cursor = inCell.localizingCursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();
		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}

	@Benchmark
	public void copyFunctionLocalizingCursorArray()
	{
		copy( inArray, out );
	}

	@Benchmark
	public void copyFunctionLocalizingCursorPlanar()
	{
		copy( inPlaner, out );
	}

	@Benchmark
	public void copyFunctionLocalizingCursorCell()
	{
		copy( inCell, out );
	}

	private void copy( Img< DoubleType > in, Img< DoubleType > out )
	{
		final Cursor< DoubleType > cursor = in.localizingCursor();
		final RandomAccess< DoubleType > ra = out.randomAccess();
		while ( cursor.hasNext() ) {
			cursor.fwd();
			ra.setPosition( cursor );
			ra.get().set(cursor.get());
		}
	}
}
