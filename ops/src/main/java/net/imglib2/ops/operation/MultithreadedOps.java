package net.imglib2.ops.operation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MultithreadedOps
{

	public static < A, B, C > void run( final BinaryOperation< A, B, C > op, final A[] in1, final B[] in2, final C[] out ) throws InterruptedException, ExecutionException
	{
		run( op, in1, in2, out, null );
	}

	public static < A, B, C > void run( final BinaryOperation< A, B, C > op, final A[] in1, final B[] in2, final C[] out, ExecutorService service ) throws InterruptedException, ExecutionException
	{
		compute( new TaskProvider()
		{

			@Override
			public Runnable taskAt( int i )
			{
				return new BinaryOperationTask< A, B, C >( op.copy(), in1[ i ], in2[ i ], out[ i ] );
			}

			@Override
			public int numTasks()
			{
				return in1.length;
			}
		}, service );
	}

	public static < A, B > void run( final UnaryOperation< A, B > op, final A[] in, final B[] out ) throws InterruptedException, ExecutionException
	{
		run( op, in, out, null );
	}

	public static < A, B > void run( final UnaryOperation< A, B > op, final A[] in, final B[] out, ExecutorService service ) throws InterruptedException, ExecutionException
	{
		compute( new TaskProvider()
		{

			@Override
			public Runnable taskAt( int i )
			{
				return new UnaryOperationTask< A, B >( op.copy(), in[ i ], out[ i ] );
			}

			@Override
			public int numTasks()
			{
				return in.length;
			}
		}, service );
	}

	private static void compute( TaskProvider tasks, ExecutorService service ) throws InterruptedException, ExecutionException
	{

		Future< ? >[] futures = new Future< ? >[ tasks.numTasks() ];

		for ( int i = 0; i < tasks.numTasks(); i++ )
		{

			if ( Thread.interrupted() )
				return;

			if ( service != null )
			{
				if ( service.isShutdown() )
					return;
				else
					futures[ i ] = service.submit( tasks.taskAt( i ) );
			}
			else
			{
				tasks.taskAt( i ).run();
			}
		}

		if ( service != null )
		{
			for ( Future< ? > f : futures )
			{
				if ( f.isCancelled() ) { return; }
				f.get();
			}
		}
	}

	private static class UnaryOperationTask< A, B > implements Runnable
	{

		private final UnaryOperation< A, B > m_op;

		private final A m_in;

		private final B m_out;

		public UnaryOperationTask( final UnaryOperation< A, B > op, final A in, final B out )
		{
			m_in = in;
			m_out = out;
			m_op = op.copy();
		}

		@Override
		public void run()
		{
			m_op.compute( m_in, m_out );
		}
	}

	private static class BinaryOperationTask< A, B, C > implements Runnable
	{

		private final BinaryOperation< A, B, C > m_op;

		private final A m_in1;

		private final B m_in2;

		private final C m_out;

		public BinaryOperationTask( final BinaryOperation< A, B, C > op, final A in1, final B in2, final C out )
		{
			m_in1 = in1;
			m_in2 = in2;
			m_out = out;
			m_op = op.copy();
		}

		@Override
		public void run()
		{
			m_op.compute( m_in1, m_in2, m_out );
		}
	}

	private static interface TaskProvider
	{
		Runnable taskAt( int i );

		int numTasks();
	}
}
