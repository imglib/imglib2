package net.imglib2.ops.operation.binary.real;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;

public class CombineToComplex<T extends RealType<T>, V extends RealType<V>, C extends ComplexType<C>>
                implements BinaryOperation<T, V, C> {

        @Override
        public C compute(T input1, V input2, C output) {
                output.setReal(input1.getRealDouble());
                output.setImaginary(input2.getRealDouble());
                return output;
        }

        @Override
        public BinaryOperation<T, V, C> copy() {
                return new CombineToComplex<T, V, C>();
        }

}
