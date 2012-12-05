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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.regiongrowing;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.type.NativeType;

/**
 * nD Connected Component Analysis.
 * 
 * @author Felix Schoenenberger (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 * @author Martin Horn (University of Konstanz)
 */
public class CCA< T extends NativeType< T > & Comparable< T >, I extends RandomAccessibleInterval< T > & IterableInterval< T >, LL extends RandomAccessibleInterval< LabelingType< Integer >> & IterableInterval< LabelingType< Integer >>> extends AbstractRegionGrowing< T, Integer, I, LL >
{

	private Cursor< T > srcCur;

	private RandomAccess< T > srcRA;

	private Integer m_labelNumber;

	private final T m_background;

	private T m_currentLabel;

	private ThreadSafeLabelNumbers m_synchronizer;

	/**
	 * @param structuringElement
	 * @param background
	 */
	public CCA( long[][] structuringElement, T background )
	{
		this( structuringElement, background, null );
	}

	/**
	 * @param structuringElement
	 * @param background
	 */
	public CCA( long[][] structuringElement, T background, ThreadSafeLabelNumbers synchronizer )
	{
		super( structuringElement, GrowingMode.ASYNCHRONOUS, false );

		if ( synchronizer == null )
			m_synchronizer = new ThreadSafeLabelNumbers();
		else
			m_synchronizer = synchronizer;

		m_background = background;
		m_labelNumber = m_synchronizer.aquireNewLabelNumber();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initRegionGrowing( I srcImg )
	{
		srcCur = srcImg.localizingCursor();
		srcRA = srcImg.randomAccess();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer nextSeedPosition( int[] seedPos )
	{
		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			if ( srcCur.get().compareTo( m_background ) != 0 )
			{
				srcCur.localize( seedPos );
				m_currentLabel = srcCur.get().copy();
				return m_labelNumber;
			}
		}
		return null;

	}

	protected synchronized Integer labelNumber()
	{
		return m_labelNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean includeInRegion( int[] oldPos, int[] nextPos, Integer label )
	{
		srcRA.setPosition( nextPos );
		return srcRA.get().compareTo( m_currentLabel ) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void queueProcessed()
	{
		m_labelNumber = m_synchronizer.aquireNewLabelNumber();

	}

	@Override
	public UnaryOperation< I, LL > copy()
	{
		return new CCA< T, I, LL >( m_structuringElement.clone(), m_background.copy(), m_synchronizer );
	}

	/**
	 * Simple helper class
	 * 
	 * @author Christian Dietz (University of Konstanz)
	 */
	private class ThreadSafeLabelNumbers
	{

		// Current labelnumber
		private int m_labelNumber;

		protected ThreadSafeLabelNumbers()
		{
			m_labelNumber = 1;
		}

		protected final synchronized int aquireNewLabelNumber()
		{
			return m_labelNumber++;
		}
	}
}
