package imglib.ops.example;

import mpicbg.imglib.type.numeric.RealType;

public interface VariableFactory<T extends RealType<T>>
{
	T createVariable();
}
