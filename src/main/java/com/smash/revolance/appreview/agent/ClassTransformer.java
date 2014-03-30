package com.smash.revolance.appreview.agent;

import javassist.*;
import javassist.bytecode.MethodInfo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: wsmash
 * Date: 28/03/14
 * Time: 22:06
 */
public class ClassTransformer implements ClassFileTransformer
{

    private final static Logger logger = Logger.getLogger(ClassTransformer.class.getName());

    private final Set<String> packages = new HashSet<String>();

    private ClassPool classPool;

    private boolean   preventExec = false;

    public void preventExec(boolean b)
    {
        this.preventExec = b;
    }

    public ClassTransformer()
    {
        classPool = new ClassPool();
        classPool.appendSystemPath();
        try
        {
            classPool.appendPathList(System.getProperty("java.class.path"));
            classPool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setPackages(final Set<String> packages)
    {
        this.packages.addAll( packages );
    }

    public byte[] transform(final ClassLoader loader,
                            final String fullyQualifiedClassName,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] classBytes) throws IllegalClassFormatException
    {
        final String className = fullyQualifiedClassName.replace("/", ".");

        if(!preventExec)
        {

            if( AgentHelper.isInPackages(className, packages) )
            {
                classPool.appendClassPath(new ByteArrayClassPath(className, classBytes));

                logger.log( Level.INFO, "Instrumenting class: " + className );

                try
                {
                    final CtClass ctClass = classPool.get(className);
                    if (ctClass.isFrozen())
                    {
                        logger.log( Level.INFO, "Skipping class: "+ className +" is frozen");
                        return null;
                    }

                    if (ctClass.isPrimitive()
                            || ctClass.isArray()
                            || ctClass.isAnnotation()
                            || ctClass.isEnum()
                            || ctClass.isInterface())
                    {
                        logger.log( Level.INFO, "Skipping class: " + className + " because it's not a class" );
                        return null;
                    }


                    boolean isClassModified = false;
                    for(CtMethod method: ctClass.getDeclaredMethods())
                    {
                        try
                        {
                            final MethodInfo methodInfo = method.getMethodInfo();
                            if (methodInfo.getCodeAttribute() == null)
                            {
                                logger.log( Level.INFO, "Skipping method " + method.getLongName() );
                                continue;
                            }

                            logger.log(Level.INFO, "Instrumenting method " + method.getLongName());

                            method.insertBefore("com.smash.revolance.appreview.agent.Notifier.getInstance().notify(new String(\"method begin\"), new String(\"" + method.getLongName() + "\"));");
                            method.insertAfter ("com.smash.revolance.appreview.agent.Notifier.getInstance().notify(new String(\"method end\"),   new String(\""+method.getLongName()+"\"));");

                            logger.log( Level.INFO, "Instrumenting method " + method.getLongName() + " [Done]" );

                            isClassModified = true;
                        }
                        catch (Exception e)
                        {
                            logger.log( Level.SEVERE, "Instrumenting method " + method.getLongName() + " [Failed]", e );
                        }
                    }

                    logger.log( Level.INFO, "Instrumenting class: " + className + " [Done]");

                    if (isClassModified)
                    {
                        return ctClass.toBytecode();
                    }
                }
                catch (Exception e)
                {
                    logger.log( Level.SEVERE, "Instrumenting class: " + className + " [Failed]", e );
                }
                return classBytes;

            }
            else
            {
                logger.log(Level.INFO, "Skipping class: " + className + " in filtered packages");
                return classBytes;
            }
        }
        else
        {
            logger.log(Level.INFO, "Execution is prevented. Skipping class: " + className);
            return classBytes;
        }
    }


}
