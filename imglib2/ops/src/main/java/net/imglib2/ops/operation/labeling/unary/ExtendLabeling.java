/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.view.Views;

/**
 * 
 * @author Christian Dietz (University of Konstanz)
 * 
 * @param <L>
 */
public class ExtendLabeling< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private final ConnectedType ct;

	private boolean labelingBased;

	public ExtendLabeling( final ConnectedType type, boolean labelingBased )
	{
		this.ct = type;
		this.labelingBased = labelingBased;
	}

	@Override
	public Labeling< L > compute( final Labeling< L > op, final Labeling< L > r )
	{
		Collection< L > labels = null;

		labels = op.getLabels();

		Set< int[] > newSeeds = new HashSet< int[] >();

		OutOfBounds< LabelingType< L >> opRa = Views.extendValue( op, new LabelingType< L >( op.firstElement().getMapping().emptyList() ) ).randomAccess();
		OutOfBounds< LabelingType< L >> resRa = Views.extendValue( r, new LabelingType< L >( op.firstElement().getMapping().emptyList() ) ).randomAccess();

		IterableInterval< LabelingType< L >> ii = null;
		Cursor< LabelingType< L >> iiCursor = null;
		int[] pos = new int[ op.numDimensions() ];

		newSeeds.clear();
		for ( L label : labels )
		{

			ii = op.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( op );

			iiCursor = ii.cursor();

			while ( iiCursor.hasNext() )
			{
				iiCursor.fwd();

				opRa.setPosition( iiCursor );
				resRa.setPosition( iiCursor );
				opRa.localize( pos );

				setLabeling( iiCursor.get().getLabeling(), resRa, labelingBased );

				if ( ct == ConnectedType.EIGHT_CONNECTED )
				{
					newSeeds.addAll( operate8Connected( pos, iiCursor.get().getLabeling(), opRa, resRa, labelingBased ) );
				}
				else if ( ct == ConnectedType.FOUR_CONNECTED )
				{
					newSeeds.addAll( operate4Connected( pos, iiCursor.get().getLabeling(), opRa, resRa, labelingBased ) );
				}

			}
		}
		return r;
	}

	private static synchronized < L extends Comparable< L >> Set< int[] > operate8Connected( final int[] currentPos, final List< L > currentLabeling, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa, boolean labelingBased )
	{

		Set< int[] > nextSeeds = new HashSet< int[] >();

		// middle left
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// middle right
		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// upper right
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// lower right
		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// lower middle
		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// lower left
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// upper left
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		// upper middle
		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		return nextSeeds;
	}

	private static synchronized < L extends Comparable< L >> void checkAndSet( final List< L > currentLabeling, int[] currentPos, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa, final Set< int[] > nextSeeds, boolean labelingBased )
	{

		if ( !opRa.get().getLabeling().containsAll( currentLabeling ) && !opRa.isOutOfBounds() )
		{
			// result access is set
			resRa.setPosition( opRa );

			// position is retrieved
			int[] tmpPos = currentPos.clone();
			resRa.localize( tmpPos );

			// Labeling is set
			setLabeling( currentLabeling, resRa, labelingBased );

			// pos is added to the list of new seeds
			nextSeeds.add( tmpPos.clone() );
		}

	}

	private static synchronized < L extends Comparable< L >> Collection< int[] > operate4Connected( final int[] currentPos, final List< L > currentLabeling, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa, boolean labelingBased )
	{

		// 4 Connected
		Set< int[] > nextSeeds = new HashSet< int[] >();

		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds, labelingBased );

		return nextSeeds;
	}

	private static synchronized < L extends Comparable< L >> void setLabeling( final List< L > currentLabels, final RandomAccess< LabelingType< L >> resRa, boolean labelingBased )
	{
		List< L > labeling = resRa.get().getLabeling();
		if ( !labeling.isEmpty() && !labelingBased ) { return; }

		HashSet< L > tmpLabels = new HashSet< L >();
		tmpLabels.clear();
		tmpLabels.addAll( currentLabels );
		tmpLabels.addAll( labeling );

		resRa.get().setLabeling( new ArrayList< L >( tmpLabels ) );
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ExtendLabeling< L >( ct, this.labelingBased );
	}
}
