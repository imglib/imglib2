package net.imglib2.script.math.fn;

import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class CursorFunction implements IFunction
{
	private final Cursor<? extends RealType<?>> c;

	public CursorFunction(final Cursor<? extends RealType<?>> c) {
		this.c = c;
	}

	@Override
	public double eval() {
		c.fwd();
		return c.get().getRealDouble();
	}

	@Override
	public void findCursors(Collection<Cursor<?>> cursors) {
		cursors.add(this.c);
	}

	@Override
	public IFunction duplicate() throws Exception {
		return new CursorFunction(c.copyCursor());
	}

	@Override
	public void findImgs(Collection<Img<?>> imgs) {}
}