package mpicbg.imglib.function.operations;

import mpicbg.imglib.function.operations.op.Op;
import mpicbg.imglib.type.numeric.RealType;

public interface Operation< R extends RealType<R> >
	extends Op<R>, FunctionReal< R > {}
