package mpicbg.imglib.scripting.math.fn;

import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.type.numeric.RealType;

public interface Operation< R extends RealType<R> >
	extends Op<R>, FunctionReal< R > {}
