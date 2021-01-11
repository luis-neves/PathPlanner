@echo off
if not exist target mkdir target
if not exist target\classes mkdir target\classes


echo compile classes
javac -nowarn -d target\classes -sourcepath jvm -cp "c:\program files\jni\lib\jni4net.j-0.8.8.0.jar"; "jvm\classlib\ICommunicationManagerCallbacks.java" "jvm\classlib\ICommunicationManagerCallbacks_.java" "jvm\classlib\CommunicationManager.java" "jvm\classlib\MessagePriorityManager.java" "jvm\classlib\ServiceBusManager.java" "jvm\classlib\ClientTopicsInfo.java" "jvm\classlib\TopicsConfiguration.java" "jvm\classlib\MIMEType.java" "jvm\classlib\Util.java" "jvm\classlib\Worker.java" "jvm\classlib\BusMessage.java" "jvm\classlib\InvalidMessageTypeException.java" "jvm\classlib\InvalidNumberPriorityLevelsException.java" "jvm\classlib\InvalidPriorityException.java" "jvm\classlib\MessagePriorityManagerNotInitializedException.java" 
IF %ERRORLEVEL% NEQ 0 goto end


echo ClassLib.j4n.jar 
jar cvf ClassLib.j4n.jar  -C target\classes "classlib\ICommunicationManagerCallbacks.class"  -C target\classes "classlib\ICommunicationManagerCallbacks_.class"  -C target\classes "classlib\__ICommunicationManagerCallbacks.class"  -C target\classes "classlib\CommunicationManager.class"  -C target\classes "classlib\MessagePriorityManager.class"  -C target\classes "classlib\ServiceBusManager.class"  -C target\classes "classlib\ClientTopicsInfo.class"  -C target\classes "classlib\TopicsConfiguration.class"  -C target\classes "classlib\MIMEType.class"  -C target\classes "classlib\Util.class"  -C target\classes "classlib\Worker.class"  -C target\classes "classlib\BusMessage.class"  -C target\classes "classlib\InvalidMessageTypeException.class"  -C target\classes "classlib\InvalidNumberPriorityLevelsException.class"  -C target\classes "classlib\InvalidPriorityException.class"  -C target\classes "classlib\MessagePriorityManagerNotInitializedException.class"  > nul 
IF %ERRORLEVEL% NEQ 0 goto end


echo ClassLib.j4n.dll 
csc /nologo /warn:0 /t:library /out:ClassLib.j4n.dll /recurse:clr\*.cs  /reference:"C:\Aulas\I&D\ArwarePathFinder\target\lib\ClassLib.dll" /reference:"C:\Program Files\JNI\lib\jni4net.n-0.8.8.0.dll"
IF %ERRORLEVEL% NEQ 0 goto end


:end

