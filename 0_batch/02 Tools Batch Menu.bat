@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-24
set SOURCEPATH=../src/main/java

:menu
set KEY=
set LABEL=
echo TIME[%TIME%]
echo.
echo [A] javap - 'ClassForClassFileDisassembler'
echo [B] javap - 'ClassForClassFileDisassembler' verbose
echo [C] Unified JVM Logging GC
echo [D] Unified JVM Logging all
echo [E] keytool - list keystore 'key_store'
echo [F] keytool - list truststore 'trust_store'
echo any other key quits
set /P KEY="Select the key: "
if /i "%KEY:~0,1%"=="A" (
  set LABEL=[A] javap - ClassForClassFileDisassembler
  call :AAA
) else if /i "%KEY%"=="B" (
  set LABEL=[B] javap - ClassForClassFileDisassembler verbose
  call :BBB
) else if /i "%KEY%"=="C" (
  set LABEL=[C] Unified JVM Logging - GC
  call :CCC
) else if /i "%KEY%"=="D" (
  set LABEL=[D] Unified JVM Logging - all
  call :DDD
) else if /i "%KEY%"=="E" (
  set LABEL=[E] keytool - list keystore 'key_store'
  call :EEE
) else if /i "%KEY%"=="F" (
  set LABEL=[F] keytool - list truststore 'trust_store'
  call :FFF
) else (
  goto :eof
)
goto menu

REM =================================================================================================================================================
:AAA
cls
%JAVA_HOME%\bin\javac -encoding UTF-8 -d ..\target -sourcepath %SOURCEPATH% %SOURCEPATH%/kp/methods/disassembling/ClassForClassFileDisassembler.java
:: The option '-c' prints disassembled code.
set OPTIONS=-classpath . -c
%JAVA_HOME%\bin\javap %OPTIONS% ..\target\kp\methods\disassembling\ClassForClassFileDisassembler.class
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:BBB
cls
%JAVA_HOME%\bin\javac -encoding UTF-8 -d ..\target -sourcepath %SOURCEPATH% %SOURCEPATH%/kp/methods/disassembling/ClassForClassFileDisassembler.java
:: The option '-verbose' prints all the information from other options together.
set OPTIONS=-classpath . -verbose
%JAVA_HOME%\bin\javap %OPTIONS% ..\target\kp\methods\disassembling\ClassForClassFileDisassembler.class
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:CCC
cls
%JAVA_HOME%\bin\java -Xlog:gc*=debug -version
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:DDD
cls
%JAVA_HOME%\bin\java -Xlog:all=debug -version
::%JAVA_HOME%\bin\java -Xlog:all=debug:file=application.log -version
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:EEE
cls
%JAVA_HOME%\bin\keytool -list -v -keystore ..\src\main\resources\security\key_store -storepass passphrase
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:FFF
cls
%JAVA_HOME%\bin\keytool -list -v -keystore ..\src\main\resources\security\trust_store -storepass passphrase -alias localhost
echo.
%JAVA_HOME%\bin\keytool -list -v -keystore ..\src\main\resources\security\trust_store -storepass passphrase -alias "verisignclass3g4ca [jdk]"
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:RedLabelAndPause
powershell -Command Write-Host "FINISH %LABEL%" -foreground "Red"
pause
goto :eof

REM =================================================================================================================================================
:quit
echo.&pause