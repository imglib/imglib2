/*

Copyright (c) 2011, Christian Dietz
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.image;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.Type;

/**
 * 
 * @author Christian Dietz
 * 
 */
public class UnaryOperationAssignment< T extends Type< T >, V extends Type< V >>
	implements UnaryOperation< IterableInterval< T >, IterableInterval< V >>
{

	private final UnaryOperation< T, V > m_op;

	public UnaryOperationAssignment( final UnaryOperation< T, V > op )
	{
		m_op = op;
	}

	@Override
	public IterableInterval< V > compute( IterableInterval< T > input, IterableInterval< V > output )
	{

		if ( !IterationOrderUtil.equalIterationOrder( input, output ) ||
				!IterationOrderUtil.equalInterval( input, output ) ) {
			throw new IllegalArgumentException( "Intervals are not compatible" );
		}

		final Cursor< T > opc = input.cursor();
		final Cursor< V > outCursor = output.cursor();
		while ( opc.hasNext() )
		{
			opc.fwd();
			outCursor.fwd();
			m_op.compute( opc.get(), outCursor.get() );
		}

		return output;
	}

	@Override
	public UnaryOperation< IterableInterval< T >, IterableInterval< V >> copy()
	{
		return new UnaryOperationAssignment< T, V >( m_op.copy() );
	}
}
