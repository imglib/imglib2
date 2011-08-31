package net.imglib2.algorithm.mser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.PriorityQueue;

import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

public class ComponentTree< T extends Comparable< T > & Type< T > >
{
	final RandomAccessibleInterval< T > input;

	final long[] dimensions;

	final RandomAccessible< BitType > accessiblePixels;

	final PriorityQueue< BoundaryPixel< T > > boundaryPixels;

	final Deque< ComponentInfo< T > > componentStack;

	public ComponentTree( final RandomAccessibleInterval< T > input, T biggerThanMax )
	{
		this.input = input;

		// final long[] dimensions = new long[ input.numDimensions() ];
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );

		ImgFactory< BitType > imgFactory = new ArrayImgFactory< BitType >();
		accessiblePixels = imgFactory.create( dimensions, new BitType() );

		boundaryPixels = new PriorityQueue< BoundaryPixel< T > >();

		componentStack = new ArrayDeque< ComponentInfo< T > >();
		componentStack.push( new ComponentInfo< T >( biggerThanMax ) );
	}
	
	void markAccessible( final Localizable position )
	{
		RandomAccess< BitType > accessiblePixelsRA = accessiblePixels.randomAccess();
		accessiblePixelsRA.setPosition( position );
		accessiblePixelsRA.get().set( true );
	}

	boolean isAccessible( final Localizable position )
	{
		RandomAccess< BitType > accessiblePixelsRA = accessiblePixels.randomAccess();
		accessiblePixelsRA.setPosition( position );
		return accessiblePixelsRA.get().get();
	}
	
	public class Neighborhood
	{
		private int n;
		private final int maxN;

		public Neighborhood()
		{
			this( 0 );
		}
		
		public Neighborhood( int nextNeighborIndex )
		{
			n = nextNeighborIndex;
			maxN = input.numDimensions() * 2 - 1;
		}
		
		public int getNextNeighborIndex()
		{
			return n;
		}
		
		public void setNextNeighborIndex( int n )
		{
			this.n = n;
		}
		
		public void reset()
		{
			n = 0;
		}

		public boolean hasNext()
		{
			return n < maxN;
		}

		public void next( final Localizable current, final Positionable neighbor )
		{
			neighbor.setPosition( current );
			neighbor.move( n % 2 == 0 ? -1 : 1, n / 2 );
			++n;
		}
	}

	public void run()
	{
		RandomAccess< T > current = input.randomAccess();
		RandomAccess< T > neighbor = input.randomAccess();
		Neighborhood n = new Neighborhood();
		input.min( current );
		T currentLevel = current.get().createVariable();
		T neighborLevel = current.get().createVariable();

		// step 2
		markAccessible( current );
		currentLevel.set( current.get() );
		
		// step 3
		componentStack.push( new ComponentInfo< T >( currentLevel ) );
		
		// step 4
		while ( true )
		{
			while ( n.hasNext() )
			{
				n.next( current, neighbor );
				if ( ! isAccessible( neighbor ) )
				{
					neighborLevel.set( neighbor.get() );
					if ( neighborLevel.compareTo( currentLevel ) >= 0 )
					{
						boundaryPixels.add( new BoundaryPixel< T >( neighbor, neighborLevel, 0 ) );
					}
					else
					{
						boundaryPixels.add( new BoundaryPixel< T >( current, currentLevel, n.getNextNeighborIndex() ) );
						current.setPosition( neighbor );
						currentLevel.set( neighborLevel );
	
						// go to 3, i.e.:
						componentStack.push( new ComponentInfo< T >( currentLevel ) );
						n.reset();
					}
				}
			}
			
			// step 5
			ComponentInfo< T > component = componentStack.peek();
			component.addPosition( current );
			
			// step 6
			if ( boundaryPixels.isEmpty() )
			{
				System.out.println("done");
				return;
			}
			
			BoundaryPixel< T > p = boundaryPixels.poll();
			if ( p.get().compareTo( currentLevel ) != 0 )
			{
				// step 7
				processStack( p.get() );
			}
			current.setPosition( p );
			currentLevel.set( p.get() );
			n.setNextNeighborIndex( p.getNextNeighborIndex() );
		}
	}
	
	protected void processStack( T value )
	{
		while (true)
		{
			// process component on top of stack
			ComponentInfo< T > component = componentStack.pop();
			emit( component );
			
			// get level of second component on stack
			ComponentInfo< T >  secondComponent = componentStack.peek();
			final int c = value.compareTo( secondComponent.get() );
			if ( c < 0 )
			{
				component.setValue( value );
				componentStack.push( component );
			}
			else
			{
				secondComponent.merge( component );
				if ( c > 0 )
					continue;
			}
			return;
		}
	} 
	
	protected void emit( ComponentInfo< T > component )
	{
		System.out.println( "new component " + component );
	}
	
	// -------------------------------------------------------------------------------

	public static final int[][] testData = new int[][] {
		{ 4, 1, 0, 1, 4 },
		{ 2, 1, 3, 4, 5 },
		{ 1, 0, 2, 1, 2 },
		{ 3, 1, 2, 0, 1 },
		{ 3, 3, 3, 3, 2 } };

	public static void main( String[] args )
	{
		final long[] dimensions = new long[] { testData[ 0 ].length, testData.length };
		ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
		Img< IntType > input = imgFactory.create( dimensions, new IntType() );

		// fill input image with test data
		int[] pos = new int[ 2 ];
		Cursor< IntType > c = input.localizingCursor();
		while ( c.hasNext() )
		{
			c.fwd();
			c.localize( pos );
			c.get().set( testData[ pos[ 1 ] ][ pos[ 0 ] ] );
		}
	}

}
