// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package classlib;

@net.sf.jni4net.attributes.ClrType
public class InvalidNumberPriorityLevelsException extends system.Exception {
    
    //<generated-proxy>
    private static system.Type staticType;
    
    protected InvalidNumberPriorityLevelsException(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrConstructor("()V")
    public InvalidNumberPriorityLevelsException() {
            super(((net.sf.jni4net.inj.INJEnv)(null)), 0);
        classlib.InvalidNumberPriorityLevelsException.__ctorInvalidNumberPriorityLevelsException0(this);
    }
    
    @net.sf.jni4net.attributes.ClrConstructor("(I)V")
    public InvalidNumberPriorityLevelsException(int numberOfPriorityLevels) {
            super(((net.sf.jni4net.inj.INJEnv)(null)), 0);
        classlib.InvalidNumberPriorityLevelsException.__ctorInvalidNumberPriorityLevelsException1(this, numberOfPriorityLevels);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("()V")
    private native static void __ctorInvalidNumberPriorityLevelsException0(net.sf.jni4net.inj.IClrProxy thiz);
    
    @net.sf.jni4net.attributes.ClrMethod("(I)V")
    private native static void __ctorInvalidNumberPriorityLevelsException1(net.sf.jni4net.inj.IClrProxy thiz, int numberOfPriorityLevels);
    
    public static system.Type typeof() {
        return classlib.InvalidNumberPriorityLevelsException.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        classlib.InvalidNumberPriorityLevelsException.staticType = staticType;
    }
    //</generated-proxy>
}
