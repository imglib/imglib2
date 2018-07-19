package net.imglib2.loops;

import java.util.Iterator;
import java.util.LinkedList;

import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * An easy yet relatively high performance way to perform pixel-wise math
 * on one or more {@link RandomAccessibleInterval} instances.
 * 
 * <pre>
 * {@code
 * RandomAccessibleInterval<A> img1 = ...
 * RandomAccessibleInterval<B> img2 = ...
 * RandomAccessibleInterval<C> img3 = ...
 * 
 * RandomAccessibleInterval<O> result = ...
 * 
 * LoopMath.compute( result, Div( Max( Max( img1, img2 ), img3 ), 3.0 ) );
 * }
 * </pre>
 * 
 * @author Albert Cardona
 *
 */
public class LoopMath
{

	private LoopMath() {}
	
	static public < O extends RealType< O > > void compute( final RandomAccessibleInterval< O > target,  final Function< O > function ) throws Exception 
	{
		// Check compatible iteration order
		
		// Recursive copy: initializes interval iterators
		final Function< O > f = function.copy();
		// Set temporary computation holders
		final O scrap = target.randomAccess().get().createVariable();
		f.setScrap( scrap );
		
		final LinkedList< RandomAccessibleInterval< ? > > images = findImages( f );
		
		checkCompatibility( images );
		
		// Evaluate function for every pixel
		for ( final O output : Views.iterable( target ) )
		{
			f.eval( output );
		}
	}
	
	@SuppressWarnings("rawtypes")
	static public LinkedList< RandomAccessibleInterval< ? > > findImages(final Function< ? > f)
	{
		final LinkedList< Object > ops = new LinkedList<>();
		ops.add( f );
		
		final LinkedList< RandomAccessibleInterval< ? > > images = new LinkedList<>();
		
		// Find images
		while ( ! ops.isEmpty() )
		{
			final Object op = ops.removeFirst();
			
			if ( op instanceof IterableOp )
			{
				images.addLast( ( ( IterableOp )op ).rai );
			} else if ( op instanceof UnaryOp )
			{
				ops.addLast( ( ( UnaryOp )op ).a );
			}
			else if ( op instanceof BinaryOp )
			{
				ops.addLast( ( ( BinaryOp )op ).a );
				ops.addLast( ( ( BinaryOp )op ).b );
			}
		}
		
		return images;
	}
	
	/**
	 * Returns true if images have the same dimensions and iterator order.
	 * Returns false when the iteration order is incompatible.
	 * 
	 * @param f
	 * @return
	 * @throws Exception When images have different dimensions.
	 */
	static private boolean checkCompatibility( final LinkedList< RandomAccessibleInterval< ? > > images ) throws Exception
	{
		if ( images.isEmpty() )
		{
			// Purely numeric operations
			return true;
		}
		
		for ( final RandomAccessibleInterval< ? > rai : images )
		{
			if ( ! ( rai instanceof IterableRealInterval ) )
			{
				// Can't flat-iterate
				return false;
			}
		}
		
		final Iterator< RandomAccessibleInterval< ? > > it = images.iterator();
		final RandomAccessibleInterval< ? > first = it.next();
		final Object order = ( (IterableRealInterval< ? >)first ).iterationOrder();
		
		
		while ( it.hasNext() )
		{
			final RandomAccessibleInterval< ? > other = it.next();
			if ( other.numDimensions() != first.numDimensions() )
			{
				throw new Exception( "Images have different number of dimensions" );
			}
			
			for ( int d = 0; d < first.numDimensions(); ++d )
			{
				if ( first.realMin( d ) != other.realMin( d ) || first.realMax( d ) != other.realMax( d ) )
				{
					throw new Exception( "Images have different sizes" );
				}
			}
			
			if ( ! order.equals( ( (IterableRealInterval< ? >) other ).iterationOrder() ) )
			{
				return false;
			}
		}
		
		return true;
	}
	
	static public interface Function< O extends RealType< O > >
	{
		public void eval( O output );
		
		public Function< O > copy();
		
		public void setScrap( O output );
	}
	
	static protected class IterableOp< I extends RealType< I >, O extends RealType< O > > implements Function< O >
	{
		private final RandomAccessibleInterval< I > rai;
		private final Iterator<I> it;

		public IterableOp( final RandomAccessibleInterval< I > rai )
		{
			this.rai = rai;
			this.it = Views.iterable( rai ).iterator();
		}

		@Override
		public void eval( final O output ) {
			output.setReal( this.it.next().getRealDouble() );
		}

		@Override
		public IterableOp< I, O > copy()
		{
			return new IterableOp< I, O >( this.rai );
		}

		@Override
		public void setScrap(O output) {}
	}
	
	static protected class NumberOp< O extends RealType< O > > implements Function< O >
	{
		private final double number;
		
		public NumberOp( final Number number ) {
			this.number = number.doubleValue();
		}

		@Override
		public void eval( final O output ) {
			output.setReal( this.number );
		}

		@Override
		public NumberOp< O > copy()
		{
			return new NumberOp< O >( this.number );
		}

		@Override
		public void setScrap(O output) {}
	}
	
	static abstract public class Op< O extends RealType< O> >
	{
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Function< O > wrap( final Object o )
		{
			if ( o instanceof RandomAccessibleInterval< ? > )
			{
				return new IterableOp( (RandomAccessibleInterval) o );
			}
			else if ( o instanceof Number )
			{
				return new NumberOp( ( (Number) o ).doubleValue() );
			}
			else if ( o instanceof Function )
			{
				return ( (Function) o ).copy();
			}
			
			// Make it fail
			return null;
		}
	}
	
	static abstract public class UnaryOp< O extends RealType< O > > extends Op< O > implements Function< O >
	{
		protected final Function< O > a;
		
		protected O scrap;
		
		@SuppressWarnings("unchecked")
		public UnaryOp( final Object o ){
			this.a = this.wrap( o );
		}
		
		public void setScrap( final O output )
		{
			if ( null == output ) return;
			this.scrap = output.copy();
			this.a.setScrap( output );
		}
	}
	
	static public class Neg< O extends RealType< O > > extends UnaryOp< O >
	{
		public Neg( final Object o)
		{
			super( o );
		}

		@Override
		public void eval( final O output ) {
			output.setReal( - output.getRealDouble() );
		}

		@Override
		public Neg<O> copy() {
			return new Neg< O >( this.a.copy() );
		}
	}
	
	static public class Abs< O extends RealType< O > > extends UnaryOp< O >
	{
		public Abs( final Object o)
		{
			super( o );
		}

		@Override
		public void eval( final O output ) {
			output.setReal( Math.abs( output.getRealDouble() ) );
		}

		@Override
		public Abs<O> copy() {
			final Abs< O > f = new Abs< O >( this.a.copy() );
			f.setScrap( this.scrap );
			return f;
		}
	}

	static abstract public class BinaryOp< O extends RealType< O > > extends Op< O > implements Function< O >
	{
		protected final Function< O > a, b;

		protected O scrap1, scrap2;
		
		public BinaryOp( final Object o1, final Object o2 )
		{
			this.a = this.wrap( o1 );
			this.b = this.wrap( o2 );
		}
		
		public void setScrap( final O output )
		{
			if ( null == output ) return; 
			this.scrap1 = output.copy();
			this.scrap2 = output.copy();
			this.a.setScrap( output );
			this.b.setScrap( output );
		}
	}
	
	static public class Mul< O extends RealType< O > > extends BinaryOp< O >
	{

		public Mul( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval(this.scrap1);
			this.b.eval(this.scrap2);
			output.set( this.scrap1 );
			output.mul( this.scrap2 );
		}

		@Override
		public Mul< O > copy() {
			final Mul< O > f = new Mul< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
	
	static public class Div< O extends RealType< O > > extends BinaryOp< O > implements Function< O >
	{

		public Div( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval(this.scrap1);
			this.b.eval(this.scrap2);
			output.set( this.scrap1 );
			output.div( this.scrap2 );
		}

		@Override
		public Div< O > copy() {
			final Div< O > f = new Div< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
	
	static public class Max< O extends RealType< O > > extends BinaryOp< O > implements Function< O >
	{

		public Max( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval( this.scrap1 );
			this.b.eval( this.scrap2 );
			output.set( 1 == this.scrap1.compareTo( this.scrap2 ) ? this.scrap1 : this.scrap2 );
		}

		@Override
		public Max< O > copy() {
			final Max< O > f = new Max< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
	
	static public class Min< O extends RealType< O > > extends BinaryOp< O >
	{

		public Min( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval( this.scrap1 );
			this.b.eval( this.scrap2 );
			output.set( -1 == this.scrap1.compareTo( this.scrap2 ) ? this.scrap1 : this.scrap2 );
		}

		@Override
		public Min< O > copy() {
			final Min< O > f = new Min< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
	
	static public class Add< O extends RealType< O > > extends BinaryOp< O >
	{

		public Add( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval( this.scrap1 );
			this.b.eval( this.scrap2 );
			output.set( this.scrap1 );
			output.add( this.scrap2 );
		}

		@Override
		public Add< O > copy() {
			final Add< O > f = new Add< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
	
	static public class Sub< O extends RealType< O > > extends BinaryOp< O >
	{

		public Sub( final Object o1, final Object o2 )
		{
			super( o1, o2 );
		}

		@Override
		public void eval( final O output ) {
			this.a.eval( this.scrap1 );
			this.b.eval( this.scrap2 );
			output.set( this.scrap1 );
			output.sub( this.scrap2 );
		}

		@Override
		public Sub< O > copy() {
			final Sub< O > f = new Sub< O >( this.a.copy(), this.b.copy() );
			f.setScrap( this.scrap1 );
			return f;
		}
	}
}
