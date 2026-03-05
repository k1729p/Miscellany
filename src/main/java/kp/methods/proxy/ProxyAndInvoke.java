package kp.methods.proxy;

import kp.utils.Printer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Proxy and invoke.
 */
public interface ProxyAndInvoke {

    /**
     * Invokes method.
     */
    static void invokeMethod() {

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Class<?>[] interfacesArr = new Class[]{Duck.class};
        final InvocationHandler invocationHandler = (proxyInstance, methodInstance, arguments) -> {
            final MethodHandle methodHandle = MethodHandles.lookup().findSpecial(
                    Duck.class,
                    methodInstance.getName(),
                    MethodType.methodType(void.class, new Class[0]),
                    Duck.class);
            methodHandle.bindTo(proxyInstance).invokeWithArguments();
            Printer.printf("proxy[%s], method[%s], arguments[%s]", proxyInstance.getClass().getName(),
                    methodInstance.getName(), arguments);
            return null;
        };
        final Duck duckProxy = (Duck) Proxy.newProxyInstance(classLoader, interfacesArr, invocationHandler);
        duckProxy.quack();
        Printer.printf("Is proxy class: Duck[%s], Duck proxy[%s]", Proxy.isProxyClass(Duck.class),
                Proxy.isProxyClass(duckProxy.getClass()));
        Printer.printHor();
    }
}

/**
 * Example interface 'Duck'.
 */
interface Duck {
    /**
     * Example default method 'quack'.
     */
    default void quack() {

        final String elements = Stream.of(new Throwable().getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.joining("\n█\t"));
        Printer.print("Stack trace elements from the 'quack' method invocation:");
        Printer.printLowLine();
        Printer.printf("█\t%s", elements);
        Printer.printOverline();

        Printer.printf("class[%s]", this.getClass().getName());
    }
}
