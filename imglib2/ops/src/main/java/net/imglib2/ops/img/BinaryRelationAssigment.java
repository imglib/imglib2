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

package net.imglib2.ops.img;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.relation.BinaryRelation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Util;

/**
 * @author Christian Dietz
 */
public class BinaryRelationAssigment< A extends RealType< A >, B extends RealType< B >> implements BinaryOperation< IterableInterval< A >, IterableInterval< B >, IterableInterval< BitType >>
{

	private BinaryRelation< A, B > relation;

	private ImgFactory< BitType > fac;

	public BinaryRelationAssigment( ImgFactory< BitType > fac, BinaryRelation< A, B > rel )
	{
		this.relation = rel;
		this.fac = fac;
	}

	@Override
	public IterableInterval< BitType > compute( IterableInterval< A > inputA, IterableInterval< B > inputB, IterableInterval< BitType > output )
	{
		if ( !Util.equalIterationOrder( inputA, inputB, output ) )
			throw new IllegalStateException( "Incompatible IterationOrder" );

		Cursor< A > inCursorA = inputA.cursor();
		Cursor< B > inCursorB = inputB.cursor();

		Cursor< BitType > outCursor = output.cursor();

		while ( outCursor.hasNext() )
		{
			outCursor.fwd();
			inCursorA.fwd();
			inCursorB.fwd();

			outCursor.get().set( relation.holds( inCursorA.get(), inCursorB.get() ) );
		}
		return output;

	}

	@Override
	public BinaryOperation< IterableInterval< A >, IterableInterval< B >, IterableInterval< BitType >> copy()
	{
		return new BinaryRelationAssigment< A, B >( fac, relation.copy() );
	}

}
