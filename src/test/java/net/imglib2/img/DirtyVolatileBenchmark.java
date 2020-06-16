package net.imglib2.img;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@State( Scope.Thread )
@Fork( 1 )
public class DirtyVolatileBenchmark
{
	interface Dirty
	{
		boolean isDirty();

		void setValue( final int index, final byte value );
	}

	public static class DirtyByteArrayUnsafe implements Dirty
	{
		private final byte[] data;

		private boolean dirty = false;

		public DirtyByteArrayUnsafe( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty;
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			dirty = true;
			data[ index ] = value;
		}
	}

	public static class ByteArray implements Dirty
	{
		private final byte[] data;

		public ByteArray( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return true;
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayVolatile implements Dirty
	{
		private final byte[] data;

		private volatile boolean dirty = false;

		public DirtyByteArrayVolatile( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty;
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			dirty = true;
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayVolatileRead implements Dirty
	{
		private final byte[] data;

		private volatile boolean dirty = false;

		public DirtyByteArrayVolatileRead( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty;
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			if ( !dirty )
				dirty = true;
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayAtomic implements Dirty
	{
		private final byte[] data;

		private final AtomicBoolean dirty = new AtomicBoolean();

		public DirtyByteArrayAtomic( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty.get();
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			dirty.set( true );
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayAtomicPlain implements Dirty
	{
		private final byte[] data;

		private final AtomicBoolean dirty = new AtomicBoolean();

		public DirtyByteArrayAtomicPlain( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty.getAcquire();
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			if ( !dirty.getPlain() )
				dirty.setRelease( true );
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayAtomicAcquire implements Dirty
	{
		private final byte[] data;

		private final AtomicBoolean dirty = new AtomicBoolean();

		public DirtyByteArrayAtomicAcquire( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty.getAcquire();
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			if ( !dirty.getAcquire() )
				dirty.setRelease( true );
			data[ index ] = value;
		}
	}

	public static class DirtyByteArrayCountDownLatch implements Dirty
	{
		private final byte[] data;

		private final CountDownLatch dirty = new CountDownLatch( 1 );

		public DirtyByteArrayCountDownLatch( final byte[] data )
		{
			this.data = data;
		}

		@Override
		public boolean isDirty()
		{
			return dirty.getCount() == 0;
		}

		@Override
		public void setValue( final int index, final byte value )
		{
			if ( dirty.getCount() != 0 )
				dirty.countDown();
			data[ index ] = value;
		}
	}

	private static final int numEntities = 67108864; // 268435456;

	public Dirty bytes;

	public Dirty bytesUnsafe;

	public Dirty bytesVolatile;

	public Dirty bytesVolatileRead;

	public Dirty bytesAtomic;

	public Dirty bytesAtomicPlain;

	public Dirty bytesAtomicAcquire;

	public Dirty bytesCounDownLatch;

	@Setup
	public void allocate()
	{
		final byte[] data = new byte[ numEntities ];
		bytes = new ByteArray( data );
		bytesUnsafe = new DirtyByteArrayUnsafe( data );
		bytesVolatile = new DirtyByteArrayVolatile( data );
		bytesVolatileRead = new DirtyByteArrayVolatileRead( data );
		bytesAtomic = new DirtyByteArrayAtomic( data );
		bytesAtomicPlain = new DirtyByteArrayAtomicPlain( data );
		bytesAtomicAcquire = new DirtyByteArrayAtomicAcquire( data );
		bytesCounDownLatch = new DirtyByteArrayCountDownLatch( data );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAll()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytes.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllUnsafe()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesUnsafe.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllVolatile()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesVolatile.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllVolatileRead()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesVolatileRead.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllAtomic()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesAtomic.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllAtomicPlain()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesAtomicPlain.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllAtomicAcquire()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesAtomicAcquire.setValue( i, ( byte ) 2 );
	}

	@Benchmark
	@BenchmarkMode( Mode.AverageTime )
	@OutputTimeUnit( TimeUnit.MILLISECONDS )
	public void touchAllCountDownLatch()
	{
		for ( int i = 0; i < numEntities; i++ )
			bytesCounDownLatch.setValue( i, ( byte ) 2 );
	}

	public static void main( final String... args ) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
				.include( DirtyVolatileBenchmark.class.getSimpleName() )
				.warmupIterations( 4 )
				.measurementIterations( 8 )
				.warmupTime( TimeValue.milliseconds( 200 ) )
				.measurementTime( TimeValue.milliseconds( 200 ) )
				.build();
		new Runner( opt ).run();
	}
}
