//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by jni4net. See http://jni4net.sourceforge.net/ 
//     Runtime Version:4.0.30319.42000
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace ClassLib {
    
    
    #region Component Designer generated code 
    public partial class MessagePriorityManager_ {
        
        public static global::java.lang.Class _class {
            get {
                return global::ClassLib.@__MessagePriorityManager.staticClass;
            }
        }
    }
    #endregion
    
    #region Component Designer generated code 
    [global::net.sf.jni4net.attributes.JavaProxyAttribute(typeof(global::ClassLib.MessagePriorityManager), typeof(global::ClassLib.MessagePriorityManager_))]
    [global::net.sf.jni4net.attributes.ClrWrapperAttribute(typeof(global::ClassLib.MessagePriorityManager), typeof(global::ClassLib.MessagePriorityManager_))]
    internal sealed partial class @__MessagePriorityManager : global::java.lang.Object {
        
        internal new static global::java.lang.Class staticClass;
        
        private @__MessagePriorityManager(global::net.sf.jni4net.jni.JNIEnv @__env) : 
                base(@__env) {
        }
        
        private static void InitJNI(global::net.sf.jni4net.jni.JNIEnv @__env, java.lang.Class @__class) {
            global::ClassLib.@__MessagePriorityManager.staticClass = @__class;
        }
        
        private static global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> @__Init(global::net.sf.jni4net.jni.JNIEnv @__env, global::java.lang.Class @__class) {
            global::System.Type @__type = typeof(__MessagePriorityManager);
            global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod> methods = new global::System.Collections.Generic.List<global::net.sf.jni4net.jni.JNINativeMethod>();
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "ListenForMessages", "ListenForMessages0", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "AddMessageToList", "AddMessageToList1", "(Ljava/lang/String;Lclasslib/BusMessage;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "ProcessCriticalMessage", "ProcessCriticalMessage2", "()V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "CreateNewMessageList", "CreateNewMessageList3", "(Ljava/lang/String;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "RemoveMessageList", "RemoveMessageList4", "(Ljava/lang/String;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "GetLowestPriorityLevel", "GetLowestPriorityLevel5", "()Ljava/lang/String;"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "CheckPriority", "CheckPriority6", "(Ljava/lang/String;)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "CheckNumberOfPriorityLevels", "CheckNumberOfPriorityLevels7", "(I)V"));
            methods.Add(global::net.sf.jni4net.jni.JNINativeMethod.Create(@__type, "__ctorMessagePriorityManager0", "__ctorMessagePriorityManager0", "(Lnet/sf/jni4net/inj/IClrProxy;ILclasslib/ICommunicationManagerCallbacks;)V"));
            return methods;
        }
        
        private static void ListenForMessages0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__real.ListenForMessages();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void AddMessageToList1(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle priorityOfTheList, global::net.sf.jni4net.utils.JniLocalHandle message) {
            // (Ljava/lang/String;Lclasslib/BusMessage;)V
            // (LSystem/String;LClassLib/BusMessage;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__real.AddMessageToList(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, priorityOfTheList), global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.BusMessage>(@__env, message));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void ProcessCriticalMessage2(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()V
            // ()V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__real.ProcessCriticalMessage();
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void CreateNewMessageList3(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle priority) {
            // (Ljava/lang/String;)V
            // (LSystem/String;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__real.CreateNewMessageList(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, priority));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void RemoveMessageList4(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj, global::net.sf.jni4net.utils.JniLocalHandle priorityId) {
            // (Ljava/lang/String;)V
            // (LSystem/String;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__real.RemoveMessageList(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, priorityId));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static global::net.sf.jni4net.utils.JniHandle GetLowestPriorityLevel5(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__obj) {
            // ()Ljava/lang/String;
            // ()LSystem/String;
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            global::net.sf.jni4net.utils.JniHandle @__return = default(global::net.sf.jni4net.utils.JniHandle);
            try {
            global::ClassLib.MessagePriorityManager @__real = global::net.sf.jni4net.utils.Convertor.StrongJp2C<global::ClassLib.MessagePriorityManager>(@__env, @__obj);
            @__return = global::net.sf.jni4net.utils.Convertor.StrongC2JString(@__env, @__real.GetLowestPriorityLevel());
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
            return @__return;
        }
        
        private static void CheckPriority6(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle priority) {
            // (Ljava/lang/String;)V
            // (LSystem/String;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager.CheckPriority(global::net.sf.jni4net.utils.Convertor.StrongJ2CString(@__env, priority));
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void CheckNumberOfPriorityLevels7(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, int numberOfPriorityLevels) {
            // (I)V
            // (I)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager.CheckNumberOfPriorityLevels(numberOfPriorityLevels);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        private static void @__ctorMessagePriorityManager0(global::System.IntPtr @__envp, global::net.sf.jni4net.utils.JniLocalHandle @__class, global::net.sf.jni4net.utils.JniLocalHandle @__obj, int numberOfPriorityLevels, global::net.sf.jni4net.utils.JniLocalHandle callbacksInterface) {
            // (ILclasslib/ICommunicationManagerCallbacks;)V
            // (ILClassLib/ICommunicationManagerCallbacks;)V
            global::net.sf.jni4net.jni.JNIEnv @__env = global::net.sf.jni4net.jni.JNIEnv.Wrap(@__envp);
            try {
            global::ClassLib.MessagePriorityManager @__real = new global::ClassLib.MessagePriorityManager(numberOfPriorityLevels, global::net.sf.jni4net.utils.Convertor.FullJ2C<global::ClassLib.ICommunicationManagerCallbacks>(@__env, callbacksInterface));
            global::net.sf.jni4net.utils.Convertor.InitProxy(@__env, @__obj, @__real);
            }catch (global::System.Exception __ex){@__env.ThrowExisting(__ex);}
        }
        
        new internal sealed class ContructionHelper : global::net.sf.jni4net.utils.IConstructionHelper {
            
            public global::net.sf.jni4net.jni.IJvmProxy CreateProxy(global::net.sf.jni4net.jni.JNIEnv @__env) {
                return new global::ClassLib.@__MessagePriorityManager(@__env);
            }
        }
    }
    #endregion
}
