package net.imglib2.blocks;

import net.imglib2.blocks.Extension.Type;
import net.imglib2.outofbounds.OutOfBoundsFactory;

class ExtensionImpl
{
	static final Extension border = new DefaultExtension( Type.BORDER );

	static final Extension mirrorSingle = new DefaultExtension( Type.MIRROR_SINGLE );

	static final Extension mirrorDouble = new DefaultExtension( Type.MIRROR_DOUBLE );

	static class DefaultExtension implements Extension
	{
		private final Type type;

		DefaultExtension( final Type type )
		{
			this.type = type;
		}

		@Override
		public Type type()
		{
			return type;
		}

		@Override
		public String toString()
		{
			return "Extension{" + type + '}';
		}
	}

	static class UnknownExtension< T, F > extends DefaultExtension
	{
		private final OutOfBoundsFactory< T, F > oobFactory;

		UnknownExtension( final OutOfBoundsFactory< T, F > oobFactory )
		{
			super( Type.UNKNOWN );
			this.oobFactory = oobFactory;
		}

		public OutOfBoundsFactory< T, F > getOobFactory()
		{
			return oobFactory;
		}

		@Override
		public String toString()
		{
			return "Extension{" + type() + ", oobFactory=" + oobFactory.getClass().getSimpleName() + '}';
		}
	}

	static class ConstantExtension< T > extends DefaultExtension
	{
		private final T value;

		ConstantExtension( T value )
		{
			super( Type.CONSTANT );
			this.value = value;
		}

		public T getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return "Extension{" + type() + ", value=" + value.getClass().getSimpleName() + "(" + value + ")}";
		}
	}
}
