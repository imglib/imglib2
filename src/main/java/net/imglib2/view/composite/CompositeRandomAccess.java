package net.imglib2.view.composite;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;

/**
 * @author Philipp Hanslovsky
 */
public class CompositeRandomAccess< T, C extends Composite< T > > implements RandomAccess< C >
{
    final protected RandomAccess< T > sourceAccess;

    final protected CompositeFactory< T, C > compositeFactory;

    final protected ToSourceDimension toSourceDimension;

    final protected int collapseDimension;

    final protected C composite;

    final int n;

    public CompositeRandomAccess(
            final RandomAccess< T > sourceAccess,
            final CompositeFactory< T, C > compositeFactory,
            final ToSourceDimension toSourceDimension,
            final int collapseDimension )
    {
        this.sourceAccess = sourceAccess;
        this.compositeFactory = compositeFactory;
        this.toSourceDimension = toSourceDimension;
        this.collapseDimension = collapseDimension;
        this.composite = compositeFactory.create( sourceAccess, collapseDimension );
        this.n = sourceAccess.numDimensions() - 1;
    }

    @Override
    public void localize( final int[] position )
    {
        for ( int d = 0; d < n; ++d )
            position[ d ] = sourceAccess.getIntPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void localize( final long[] position )
    {
        for ( int d = 0; d < n; ++d )
            position[ d ] = sourceAccess.getLongPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public int getIntPosition( final int d )
    {
        return sourceAccess.getIntPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public long getLongPosition( final int d )
    {
        return sourceAccess.getLongPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void localize( final float[] position )
    {
        for ( int d = 0; d < n; ++d )
            position[ d ] = sourceAccess.getFloatPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void localize( final double[] position )
    {
        for ( int d = 0; d < n; ++d )
            position[ d ] = sourceAccess.getDoublePosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public float getFloatPosition( final int d )
    {
        return sourceAccess.getFloatPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public double getDoublePosition( final int d )
    {
        return sourceAccess.getFloatPosition( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public int numDimensions()
    {
        return n;
    }

    @Override
    public void fwd( final int d )
    {
        sourceAccess.fwd( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void bck( final int d )
    {
        sourceAccess.bck( toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void move( final int distance, final int d )
    {
        sourceAccess.move( distance, toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void move( final long distance, final int d )
    {
        sourceAccess.move( distance, toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void move( final Localizable localizable )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.move( localizable.getLongPosition( d ), toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void move( final int[] distance )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.move( distance[ d ], toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void move( final long[] distance )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.move( distance[ d ], toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void setPosition( final Localizable localizable )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.setPosition( localizable.getLongPosition( d ), toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void setPosition( final int[] position )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.setPosition( position[ d ], toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void setPosition( final long[] position )
    {
        for ( int d = 0; d < n; ++d )
            sourceAccess.setPosition( position[ d ], toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void setPosition( final int position, final int d )
    {
        sourceAccess.setPosition( position, toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public void setPosition( final long position, final int d )
    {
        sourceAccess.setPosition( position, toSourceDimension.toSourceDimension( d ) );
    }

    @Override
    public C get()
    {
        return composite;
    }

    @Override
    public CompositeRandomAccess<T, C> copy() {
        return copyRandomAccess();
    }

    @Override
    public CompositeRandomAccess<T, C> copyRandomAccess() {
        return new CompositeRandomAccess<>( sourceAccess.copyRandomAccess(), compositeFactory, toSourceDimension, collapseDimension);
    }
}
