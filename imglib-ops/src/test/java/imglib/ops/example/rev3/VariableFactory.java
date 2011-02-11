package imglib.ops.example.rev3;

import mpicbg.imglib.type.numeric.RealType;

public interface VariableFactory<T extends RealType<T>>
{
	T createVariable();
}
