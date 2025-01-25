package kp.methods;

import kp.methods.arity.Arity;
import kp.methods.arity.EngineBothWays;
import kp.methods.arity.EngineReceiving;
import kp.methods.arity.EngineSending;
import kp.methods.calling.Contrasting;
import kp.methods.composing.FunctionComposer;
import kp.methods.functional.FunctionalInterfaceWrapper;
import kp.methods.invoking.FindHandleAndInvoke;
import kp.methods.proxy.ProxyAndInvoke;
import kp.utils.Printer;

/**
 * The main launcher for methods research.
 */
public class ApplicationForMethods {

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        Arity.methodWithFiveFunctionParameters(//
                EngineReceiving.TERNARY_FUNCTION, EngineReceiving.QUATERNARY_FUNCTION, //
                EngineReceiving.QUINARY_FUNCTION, EngineReceiving.SEPTENARY_FUNCTION);
        Arity.methodWithConsumerParameter(EngineSending.QUATERNARY_CONSUMER);
        Arity.methodWithFunctionParameter(EngineBothWays.FUNCTION);

        Contrasting.usingLocalClassVersusAnonymousClassVersusLambdaExpression();
        Contrasting.lambdaAccessingVersusCallingDirectly();

        FunctionComposer.composeFunctions();

        FunctionalInterfaceWrapper.launchFunctionalInterface();

        final FindHandleAndInvoke findHandleAndInvoke = new FindHandleAndInvoke();
        findHandleAndInvoke.invokeObjectMethods();
        findHandleAndInvoke.invokeFirstSetterThenGetter();
        findHandleAndInvoke.replaceArrayElement();

        ProxyAndInvoke.invokeMethod();
    }

}
