package net.imglib2.converter.sampler;

import net.imglib2.Sampler;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

public final class RealDoubleSamplerConverter< R extends RealType< R > > implements SamplerConverter< R, DoubleType >
{
	@Override
	public DoubleType convert( Sampler< R > sampler )
	{
		return new DoubleType( new RealConvertingDoubleAccess< R >( sampler ) );
	}

	private static final class RealConvertingDoubleAccess< R extends RealType< R > > implements DoubleAccess
	{
		private final Sampler< R > sampler;

		private RealConvertingDoubleAccess( final Sampler< R > sampler )
		{
			this.sampler = sampler;
		}

		@Override
		public void close() {}

		/**
		 * This is only intended to work with DoubleType!
		 * We ignore index!!!
		 */
		@Override
		public double getValue( int index )
		{
			return sampler.get().getRealDouble();
		}

		/**
		 * This is only intended to work with DoubleType!
		 * We ignore index!!!
		 */
		@Override
		public void setValue( int index, double value )
		{
			sampler.get().setReal( value );
		}
	}
}
