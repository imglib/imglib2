package mpicbg.imglib.type.numeric;

public interface AdvancedMathType< T extends AdvancedMathType<T> > extends NumericType<T>
{
	public void exp();
	public void log();
	public void sin();
	public void cos();
	public void tan();
	public void round();
}
