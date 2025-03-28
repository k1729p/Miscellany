:: This Windows batch file could be used for refreshing key store and trust store.
:: ###################################################################################################

@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-24
:: goto :Recreate

:List
%JAVA_HOME%\bin\keytool ^
 -list -v ^
 -keystore .\key_store ^
 -storepass passphrase
echo.
pause
%JAVA_HOME%\bin\keytool ^
 -list -v ^
 -alias localhost ^
 -keystore .\trust_store ^
 -storepass passphrase
echo.
pause
%JAVA_HOME%\bin\keytool ^
 -list -v ^
 -alias "verisignclass3g4ca [jdk]" ^
 -keystore .\trust_store ^
 -storepass passphrase
pause
goto :eof

:Recreate
if exist .\server-cert.crt del .\server-cert.crt
if exist .\key_store        del .\key_store
if exist .\trust_store   del .\trust_store
copy "%JAVA_HOME%\lib\security\cacerts" .\trust_store
%JAVA_HOME%\bin\keytool ^
 -storepasswd ^
 -new passphrase ^
 -keystore .\trust_store ^
 -storepass changeit
pause
%JAVA_HOME%\bin\keytool ^
 -genkeypair ^
 -alias localhost ^
 -keyalg RSA ^
 -keysize 2048 ^
 -sigalg SHA256withRSA ^
 -dname "CN=localhost,OU=Widget Development Group,O=Ficticious Widgets Inc.,L=Sunnyvale,ST=CA,C=US" ^
 -validity 3650 ^
 -keystore .\key_store ^
 -storepass passphrase
pause
%JAVA_HOME%\bin\keytool ^
 -exportcert ^
 -alias localhost ^
 -file .\server-cert.crt ^
 -keystore .\key_store ^
 -storepass passphrase
pause
%JAVA_HOME%\bin\keytool ^
 -importcert ^
 -alias localhost ^
 -file .\server-cert.crt ^
 -noprompt ^
 -keystore .\trust_store ^
 -storepass passphrase
pause
goto :eof