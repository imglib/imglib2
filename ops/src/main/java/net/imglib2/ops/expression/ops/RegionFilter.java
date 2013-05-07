package net.imglib2.ops.expression.ops;

import net.imglib2.IterableInterval;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Sampler;
import net.imglib2.newroi.Regions;
import net.imglib2.newroi.util.PositionableIterableInterval;
import net.imglib2.ops.expression.AbstractUnaryOp;
import net.imglib2.ops.expression.UnaryOp;
import net.imglib2.type.BooleanType;
import net.imglib2.type.Type;
import net.imglib2.view.Views;

public final class RegionFilter< T extends Comparable< T > & Type< T >, B extends BooleanType< B >, I extends RandomAccessibleInterval< B > & Positionable & Localizable > extends AbstractUnaryOp< RandomAccessibleInterval< T >, RandomAccessible< T > >
{
	protected final I region;

	final protected UnaryOp< T, IterableInterval< T > > op;

	public RegionFilter( final I region, final UnaryOp< T, IterableInterval< T > > op )
	{
		this.region = region;
		this.op = op;
	}

	public RegionFilter( final I region, final UnaryOp< T, IterableInterval< T > > op, final Sampler< RandomAccessibleInterval< T > > output, final Sampler< RandomAccessible< T > > input1 )
	{
		super( output, input1 );
		this.region = region;
		this.op = op;
	}

	@Override
	public RandomAccessibleInterval< T > get()
	{
		final RandomAccessible< T > imgInput = input1.get();
		final RandomAccessibleInterval< T > imgOutput = output.get();

		final PositionableIterableInterval< B > roi = Regions.iterablePositionable( region );
		// TODO: find a way to do this without casting:
		@SuppressWarnings( { "rawtypes", "unchecked" } )
		final RandomAccessible< IterableInterval< T > > imageRegions = ( RandomAccessible ) Regions.accessible( Regions.samplePositionable( roi, imgInput ) );
		final IterableInterval< IterableInterval< T > > input = Views.iterable( Views.interval( imageRegions, imgOutput ) );
		final UnaryIterate< T, IterableInterval< T > > regionReduceFilter = Ops.iterate( op );

		regionReduceFilter.input1().setConst( input );
		regionReduceFilter.output().setConst( Views.iterable( imgOutput ) );
		regionReduceFilter.get();

		return imgOutput;
	}

	@Override
	public RegionFilter< T, B, I > copy()
	{
		// TODO
		throw new UnsupportedOperationException();
	}
}
