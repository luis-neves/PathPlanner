// ------------------------------------------------------------------------------
//  <autogenerated>
//      This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
// 
//      Changes to this file may cause incorrect behavior and will be lost if 
//      the code is regenerated.
//  </autogenerated>
// ------------------------------------------------------------------------------

package classlib;

@net.sf.jni4net.attributes.ClrTypeInfo
public final class ICommunicationManagerCallbacks_ {
    
    //<generated-static>
    private static system.Type staticType;
    
    public static system.Type typeof() {
        return classlib.ICommunicationManagerCallbacks_.staticType;
    }
    
    private static void InitJNI(net.sf.jni4net.inj.INJEnv env, system.Type staticType) {
        classlib.ICommunicationManagerCallbacks_.staticType = staticType;
    }
    //</generated-static>
}

//<generated-proxy>
@net.sf.jni4net.attributes.ClrProxy
class __ICommunicationManagerCallbacks extends system.Object implements classlib.ICommunicationManagerCallbacks {
    
    protected __ICommunicationManagerCallbacks(net.sf.jni4net.inj.INJEnv __env, long __handle) {
            super(__env, __handle);
    }
    
    @net.sf.jni4net.attributes.ClrMethod("(Z)V")
    public native void InitializationDoneEvent(boolean isSuccess);
    
    @net.sf.jni4net.attributes.ClrMethod("(ZLSystem/String;)V")
    public native void MessageSentEvent(boolean wasSent, java.lang.String messageId);
    
    @net.sf.jni4net.attributes.ClrMethod("(ZLSystem/String;LSystem/String;)V")
    public native void StreamMessageSentEvent(boolean wasSent, java.lang.String messageId, java.lang.String contentIndentification);
    
    @net.sf.jni4net.attributes.ClrMethod("(ZLSystem/String;LSystem/String;)V")
    public native void ContentSubscribedEvent(boolean isSuccess, java.lang.String contentIndentification, java.lang.String streamerID);
    
    @net.sf.jni4net.attributes.ClrMethod("(ZLSystem/String;LSystem/String;)V")
    public native void ContentUnsubscribedEvent(boolean isSuccess, java.lang.String contentIndentification, java.lang.String streamerID);
    
    @net.sf.jni4net.attributes.ClrMethod("(LClassLib/BusMessage;)V")
    public native void MessageToProcessEvent(classlib.BusMessage message);
}
//</generated-proxy>
