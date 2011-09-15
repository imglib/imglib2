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

public class ComponentTree< T extends Comparable< T > & Type< T >, C extends Component< T > >
{
	final RandomAccessibleInterval< T > input;

	final ComponentGenerator< T, C > componentGenerator;
	
	final ComponentHandler< C > componentOutput;
 
	final long[] dimensions;

	final RandomAccessible< BitType > accessiblePixels;

	final PriorityQueue< BoundaryPixel< T > > boundaryPixels;

	final Deque< C > componentStack;

	public ComponentTree( final RandomAccessibleInterval< T > input, final ComponentGenerator< T, C > componentGenerator, final ComponentHandler< C > componentOutput )
	{
		this.input = input;
		this.componentGenerator = componentGenerator;
		this.componentOutput = componentOutput;

		// final long[] dimensions = new long[ input.numDimensions() ];
		dimensions = new long[ input.numDimensions() ];
		input.dimensions( dimensions );

		ImgFactory< BitType > imgFactory = new ArrayImgFactory< BitType >();
		accessiblePixels = imgFactory.create( dimensions, new BitType() );

		boundaryPixels = new PriorityQueue< BoundaryPixel< T > >();

		componentStack = new ArrayDeque< C >();
		componentStack.push( componentGenerator.createMaxComponent() );
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
		private final int nBound;

		public Neighborhood()
		{
			this( 0 );
		}
		
		public Neighborhood( int nextNeighborIndex )
		{
			n = nextNeighborIndex;
			nBound = input.numDimensions() * 2;
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
			return n < nBound;
		}

		public boolean next( final Localizable current, final Positionable neighbor )
		{
			neighbor.setPosition( current );
			final int d = n / 2;
			if ( n % 2 == 0 )
			{
				neighbor.move( -1, d );
				++n;
				return current.getLongPosition( d ) - 1 >= 0;
			}
			else
			{
				neighbor.move( 1, d );				
				++n;
				return current.getLongPosition( d ) + 1 < dimensions[ d ];
			}
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
		componentStack.push( componentGenerator.createComponent( currentLevel ) );
		
		// step 4
		//for ( int i = 0; i < 3; ++i )
		while ( true )
		{
			//double pos[] = new double[2];
			//current.localize( pos ); System.out.println("current (" + pos[0] + ", " + pos[1] + ")"); System.out.println("currentLevel = " + currentLevel );
			while ( n.hasNext() )
			{
				if ( ! n.next( current, neighbor ) )
					continue;
				if ( ! isAccessible( neighbor ) )
				{
					markAccessible( neighbor );
					neighborLevel.set( neighbor.get() );
					//neighbor.localize( pos ); System.out.println("neighbor (" + pos[0] + ", " + pos[1] + ")"); System.out.println("neighborLevel = " + neighborLevel );
					if ( neighborLevel.compareTo( currentLevel ) >= 0 )
					{
						boundaryPixels.add( new BoundaryPixel< T >( neighbor, neighborLevel, 0 ) );
					}
					else
					{
						boundaryPixels.add( new BoundaryPixel< T >( current, currentLevel, n.getNextNeighborIndex() ) );
						current.setPosition( neighbor );
						currentLevel.set( neighborLevel );
						//current.localize( pos ); System.out.println("current (" + pos[0] + ", " + pos[1] + ")"); System.out.println("currentLevel = " + currentLevel );
	
						// go to 3, i.e.:
						componentStack.push( componentGenerator.createComponent( currentLevel ) );
						//System.out.println( " push new = " + componentStack.peek() );
						n.reset();
					}
				}
			}
			
			// step 5
			//showComponentStack("step 5");
			C component = componentStack.peek();
			component.addPosition( current );
			//System.out.println( "top component = " + component );
			
			// step 6
			if ( boundaryPixels.isEmpty() )
			{
				processStack( currentLevel );
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
	
	protected void showComponentStack( String msg )
	{
		System.out.println(msg);
		for ( C c : componentStack )
			System.out.println( c );
	}
	
	/**
	 * This is called whenever the current value is raised.
	 * 
	 * @param value
	 */
	protected void processStack( T value )
	{
		while (true)
		{
//			showComponentStack("stack before:");
			// process component on top of stack
			C component = componentStack.pop();
			componentOutput.emit( component );
			
			// get level of second component on stack
			C secondComponent = componentStack.peek();
			final int c = value.compareTo( secondComponent.getValue() );
			if ( c < 0 )
			{
//				System.out.println("(raise component " + ( ( PixelListComponent< T > ) component).id + " to level " + value + ")");
				component.setValue( value );
				componentStack.push( component );
			}
			else
			{
//				System.out.println("(merge component " + ( ( PixelListComponent< T > ) component).id + " into " + ( ( PixelListComponent< T > ) secondComponent).id + ")");
				secondComponent.merge( component );
				if ( c > 0 )
					continue;
			}
//			showComponentStack("stack after:");
			return;
		}
	}
	
	// -------------------------------------------------------------------------------

	public static final int[][] testData = new int[][] {
		{ 8, 7, 6, 7, 1 },
		{ 8, 8, 5, 8, 1 },
		{ 2, 3, 4, 3, 2 },
		{ 1, 8, 3, 8, 1 },
		{ 1, 2, 2, 2, 1 } };

//	public static final int[][] testData = new int[][] {
//		{ 4, 1, 0, 1, 4 },
//		{ 2, 1, 3, 4, 5 },
//		{ 1, 0, 2, 1, 2 },
//		{ 3, 1, 2, 0, 1 },
//		{ 3, 3, 3, 3, 2 } };

//	public static final int[][] testData = new int[][] {
//		{ 0, 9, 0, 1, 4, 0, 2 },
//		{ 8, 9, 3, 4, 5, 0, 3 },
//		{ 7, 0, 3, 1, 2, 0, 4 },
//		{ 5, 1, 5, 5, 5, 5, 5 },
//		{ 7, 1, 2, 3, 4, 5, 6 },
//		{ 3, 3, 1, 0, 1, 5, 1 },
//		{ 3, 3, 0, 8, 2, 1, 1 } };

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

		final PixelListComponentGenerator< IntType > generator = new PixelListComponentGenerator< IntType >( new IntType( Integer.MAX_VALUE ) );
		final PixelListComponentHandler< IntType > handler = new PixelListComponentHandler< IntType >( dimensions );
		final ComponentTree< IntType, PixelListComponent< IntType > > tree = new ComponentTree< IntType, PixelListComponent< IntType > >( input, generator, handler );
		tree.run();
	}

}
