package mpicbg.imglib.function.operations;

import mpicbg.imglib.function.Function;
import mpicbg.imglib.function.operations.op.Op;
import mpicbg.imglib.type.numeric.NumericType;

public interface Operation< A extends NumericType<A> > extends Op<A>, Function< A, A, A > {}