/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops;

import static net.imglib2.ops.sandbox.OptimizeThis.add;
import static net.imglib2.ops.sandbox.OptimizeThis.floatConstant;
import static net.imglib2.ops.sandbox.OptimizeThis.floatTemp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import net.imglib2.Sampler;
import net.imglib2.ops.sandbox.OptimizeThis.Add;
import net.imglib2.ops.sandbox.OptimizeThis.Op;
import net.imglib2.ops.sandbox.OptimizeThis.PortForward;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Default {@link OpService} implementation.
 *
 * @author Johannes Schindelin
 */
public class DefaultOpService implements OpService {

	//@Override
	public <T> Sampler<T> optimize(Sampler<T> op) {
		final JavassistOptimizer<T> optimizer = new JavassistOptimizer<T>(op);
		try {
			return optimizer.optimize();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static<T> void main(final String... main) {
		final PortForward< FloatType > i1 = new PortForward< FloatType >();
		final PortForward< FloatType > i2 = new PortForward< FloatType >();
		final PortForward< FloatType > o = new PortForward< FloatType >();
		final FloatType a = new FloatType();
		final FloatType b = new FloatType();
		final FloatType c = new FloatType();
		i1.setConst(a);
		i2.setConst(b);
		o.setConst(c);
		a.set(23);
		b.set(17);
		c.set(-1);
		final Op<FloatType> op = add( add( floatConstant( 100 ), i1, floatTemp() ), i2, o );
		final Sampler<FloatType> optimizedOp = new DefaultOpService().optimize(op);
		System.err.println(optimizedOp.get().get());
	}

}

/**
 * Helps constructing a special-purpose Op.
 * 
 * @author Johannes Schindelin
 */
class JavassistOptimizer<T> {

	private final Sampler<T> op;
	private StringBuilder builder;
	private Map<Sampler<T>, String> variables;

	private static ClassPool pool = ClassPool.getDefault();
	private static Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	public JavassistOptimizer(final Sampler<T> op) {
		this.op = op;
	}

	@SuppressWarnings("unchecked")
	public synchronized Sampler<T> optimize() throws InstantiationException,
			IllegalAccessException, SecurityException, NoSuchFieldException,
			NotFoundException, CannotCompileException {
		builder = new StringBuilder();
		variables = new LinkedHashMap<Sampler<T>, String>();

		builder.setLength(0);
		handle(null, op);

		final Sampler<T> result = (Sampler<T>) getClass(builder.toString()).newInstance();
		for (final Sampler<T> variable : variables.keySet()) {
			final String name = variables.get(variable);
			final Field field = result.getClass().getField(name);
			field.set(result, variable);
		}
		return result;
	}

	private synchronized Class<?> getClass(final String methodBody) throws NotFoundException, CannotCompileException {
		final Class<?> cached = classes.get(methodBody);
		if (cached != null) return cached;

		final CtClass clazz = pool.makeClass("net.imglib2.ops.optimized.Op" + classes.size());
		final CtClass samplerClazz = pool.get(Sampler.class.getName());
		clazz.addInterface(samplerClazz);
		for (final String name : variables.values()) {
			final CtField field = new CtField(samplerClazz, name, clazz);
			field.setModifiers(Modifier.PUBLIC);
			clazz.addField(field);
		}
		final String methodString = "public java.lang.Object get() {\n"
				+ methodBody
				+ "return " + variables.get(op) + ";\n"
				+ "}";
System.err.println("methodString:\n" + methodString);
		clazz.addMethod(CtNewMethod.make(methodString, clazz));

		final Class<?> result = clazz.toClass();
		classes.put(methodBody, result);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized void handle(final Sampler<T> output, final Sampler<T> input) {
		if (op instanceof Add) {
			final Add add = (Add)op;
			final Sampler output2 = add.output().getSampler();
			final Sampler input1 = add.input1().getSampler();
			assign(output2, input1);
			final Sampler input2 = add.input1().getSampler();
			op("add", output2, input2);
			assign(output, output2);
		} else {
			assign(output, op);
		}
	}

	private synchronized void assign(final Sampler<T> output, final Sampler<T> input) {
		op("set", output, input);
	}

	private synchronized void op(final String op, final Sampler<T> output, final Sampler<T> input) {
		if (output != null) {
			builder.append(appendVariable(output)).append(".").append(op).append("(");
		}
		builder.append(appendVariable(input)).append(".get()");
		if (output != null) builder.append(")");
		builder.append(";\n");
	}

	private synchronized String appendVariable(final Sampler<T> sampler) {
		String name = variables.get(sampler);
		if (name != null) return name;
		name = "s" + variables.size();
		variables.put(sampler, name);
		return name;
	}

}