import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.Opcode;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.join;


public class MyAgent {
    public static String multiLines(String ...lines) {
        return join("\n", lines);
    }

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        int lastDot = args.lastIndexOf(".");
        String className = args.substring(0, lastDot);
        String methodName = args.substring(lastDot + 1);
        System.out.println("Instrument with " + args);
        ClassPool pool = ClassPool.getDefault();
        CtClass insClass = pool.get(className);

        CtMethod distance_f = CtMethod.make(multiLines(
                "public static void distance(float x) {",
                "   System.out.println(\"### branch type float, distance \" + x + \" ###\");",
                "}"
        ), insClass);
        insClass.addMethod(distance_f);

        CtMethod insMethod = insClass.getDeclaredMethod(methodName);

        // insMethod.insertBefore("System.out.println(System.currentTimeMillis());");
        // LineNumberAttribute code = (LineNumberAttribute)insMethod.getMethodInfo().getCodeAttribute().getAttribute(LineNumberAttribute.tag);
        CodeAttribute ca = insMethod.getMethodInfo().getCodeAttribute();
        CodeIterator it = ca.iterator();
        while (it.hasNext()) {
            int pos = it.next();
            int op = it.byteAt(pos);
            if (op == Opcode.FCMPL || op == Opcode.FCMPG) {
                // System.out.println("in " + op + " at " + pos);

                Bytecode bytecode = new Bytecode(ca.getConstPool());
                bytecode.add(Opcode.DUP2);
                bytecode.add(Opcode.FSUB);
                bytecode.addInvokestatic(
                        insClass,
                        "distance",
                        CtClass.voidType,
                        new CtClass[]{CtClass.floatType}
                        );
                bytecode.add(op);
                it.writeByte(Opcode.NOP, pos);
                it.insertEx(bytecode.get());
            }
        }

        byte[] bytes = insClass.toBytecode();
//        Files.write(Path.of("Triangle.class"), bytes);
//        System.out.println("Save.");

        instrumentation.redefineClasses(new ClassDefinition(insClass.toClass(), bytes));
        // instrumentation.
    }
}
