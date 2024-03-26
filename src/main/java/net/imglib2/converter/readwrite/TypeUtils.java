package net.imglib2.converter.readwrite;

import java.util.function.Supplier;

import net.imglib2.Sampler;

/**
 * Helper method for implementing Typed::getType
 */
class TypeUtils
{
	public static < A, B > B getConvertedType( A sourceType, Supplier< SamplerConverter< ? super A, B > > converterSupplier )
	{
		return converterSupplier.get().convert( new ConstantSampler<>( sourceType ) );
	}

	private static class ConstantSampler< T > implements Sampler< T >
	{

		private final T t;

		public ConstantSampler( T t )
		{
			this.t = t;
		}

		@Override
		public T get()
		{
			return t;
		}

		@Override
		public T getType() // TODO GET-TYPE: just use default implementation
		{
			return t;
		}

		@Override
		public Sampler< T > copy()
		{
			return new ConstantSampler<>( t );
		}
	}
}
