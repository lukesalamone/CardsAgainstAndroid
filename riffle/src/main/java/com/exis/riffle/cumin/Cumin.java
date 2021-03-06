package com.exis.riffle.cumin;

import com.exis.riffle.Riffle;

/**
 * Created by damouse on 2/11/2016.
 */
public class Cumin {

    // The final state of Cuminicated methods. These are ready to fire as needed
    public interface Wrapped {
        Object invoke(Object... args);
    }


    /**
     * Converts arbitrary object b to be of type A. Constructs the type if needed.
     */
    static <A> A convert(Class<A> a, Object b) {
        if (a.isInstance(b))
            return a.cast(b);

        if (a == Integer.class) {
            if (b instanceof Double) {
                return (A) Integer.valueOf(((Double) b).intValue());
            }
        }

        Riffle.error("PRIMITIVE CONVERSTION FALLTHROUGH. Want: " + a.toString() + ", received: " + b.getClass() + ", value: " + b.toString());
        return null;
    }


    //
    //
    // Start Generic Shotgun
    public static  Wrapped cuminicate(Handler.ZeroZero fn) {
        return (q) -> { fn.run(); return null; };
    }

    public static <A> Wrapped cuminicate(Class<A> a, Handler.OneZero<A> fn) {
        return (q) -> { fn.run(convert(a, q[0])); return null; };
    }

    public static <A, B> Wrapped cuminicate(Class<A> a, Class<B> b, Handler.TwoZero<A, B> fn) {
        return (q) -> { fn.run(convert(a, q[0]), convert(b, q[1])); return null; };
    }

    public static <A, B, C> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Handler.ThreeZero<A, B, C> fn) {
        return (q) -> { fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2])); return null; };
    }

    public static <A, B, C, D> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Handler.FourZero<A, B, C, D> fn) {
        return (q) -> { fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3])); return null; };
    }

    public static <A, B, C, D, E> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<E> e, Handler.FiveZero<A, B, C, D, E> fn) {
        return (q) -> { fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3]), convert(e, q[4])); return null; };
    }

    public static <A, B, C, D, E, F> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<E> e, Class<F> f, Handler.SixZero<A, B, C, D, E, F> fn) {
        return (q) -> { fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3]), convert(e, q[4]), convert(f, q[5])); return null; };
    }

    public static <R> Wrapped cuminicate(Class<R> r, Handler.ZeroOne<R> fn) {
        return (q) -> { return fn.run(); };
    }

    public static <A, R> Wrapped cuminicate(Class<A> a, Class<R> r, Handler.OneOne<A, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0])); };
    }

    public static <A, B, R> Wrapped cuminicate(Class<A> a, Class<B> b, Class<R> r, Handler.TwoOne<A, B, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0]), convert(b, q[1])); };
    }

    public static <A, B, C, R> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<R> r, Handler.ThreeOne<A, B, C, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2])); };
    }

    public static <A, B, C, D, R> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<R> r, Handler.FourOne<A, B, C, D, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3])); };
    }

    public static <A, B, C, D, E, R> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<E> e, Class<R> r, Handler.FiveOne<A, B, C, D, E, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3]), convert(e, q[4])); };
    }

    public static <A, B, C, D, E, F, R> Wrapped cuminicate(Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<E> e, Class<F> f, Class<R> r, Handler.SixOne<A, B, C, D, E, F, R> fn) {
        return (q) -> { return fn.run(convert(a, q[0]), convert(b, q[1]), convert(c, q[2]), convert(d, q[3]), convert(e, q[4]), convert(f, q[5])); };
    }
    // End Generic Shotgun
}