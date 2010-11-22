package mpicbg.imglib.function.operations;

import mpicbg.imglib.function.Function;
import mpicbg.imglib.function.operations.op.Op;
import mpicbg.imglib.type.numeric.RealType;

public interface Operation< A extends RealType<A> > extends Op<A>, Function< A, A, A > {}
