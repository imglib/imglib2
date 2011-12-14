package net.imglib2.converter.sampler;

import net.imglib2.Sampler;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

public final class RealFloatSamplerConverter< R extends RealType< R > > implements SamplerConverter< R, FloatType >
{
	@Override
	public FloatType convert( Sampler< R > sampler )
	{
		return new FloatType( new RealConvertingFloatAccess< R >( sampler ) );
	}

	private static final class RealConvertingFloatAccess< R extends RealType< R > > implements FloatAccess
	{
		private final Sampler< R > sampler;

		private RealConvertingFloatAccess( final Sampler< R > sampler )
		{
			this.sampler = sampler;
		}

		@Override
		public void close() {}

		/**
		 * This is only intended to work with FloatType!
		 * We ignore index!!!
		 */
		@Override
		public float getValue( int index )
		{
			return sampler.get().getRealFloat();
		}

		/**
		 * This is only intended to work with FloatType!
		 * We ignore index!!!
		 */
		@Override
		public void setValue( int index, float value )
		{
			sampler.get().setReal( value );
		}
	}
}
