package net.imglib2.util;

public final class Casts
{

	private Casts() {
		// prevent from instantiation
	}

	public static <T> T unchecked( final Object value ) {
		@SuppressWarnings( "unchecked" )
		T t = (T) value;
		return t;
	}
}
