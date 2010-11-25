package mpicbg.imglib.scripting.math.fn;


import java.util.Collection;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class UnaryOperation implements IFunction
{
	private final IFunction a;

	public UnaryOperation(final Image<? extends RealType<?>> img) {
		this.a = new ImageFunction(img);
	}

	public UnaryOperation(final IFunction fn) {
		this.a = fn;
	}

	public UnaryOperation(final Number val) {
		this.a = new NumberFunction(val);
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		a.findCursors(cursors);
	}
	
	public final IFunction a() { return a; }
}