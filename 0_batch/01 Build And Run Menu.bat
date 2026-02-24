@echo off
set JAVA_HOME=C:\PROGRA~1\JAVA\JDK-25
set M2_HOME=c:\tools\apache-maven
set JARFILE=target\Miscellany.jar
set MVN_BLD_SWITCH=ON

:menu
set KEY=
set LABEL=
echo TIME[%TIME%], MVN Building [%MVN_BLD_SWITCH%]
echo - - - - - - - - - - - - - - -
echo [A] Basics
echo [B] Collections
echo [C] Dates and Times
echo - - - - - - - - - - - - - - -
echo [D] Files
echo [E] Methods
echo [F] Processes
echo - - - - - - - - - - - - - - -
echo [G] Reactive Streams
echo [H] Records
echo [I] Regex
echo - - - - - - - - - - - - - - -
echo [J] Sorting
echo [K] Streams Collecting
echo [L] Streams Fragmentation
echo - - - - - - - - - - - - - - -
echo [M] Streams Gathering
echo [N] Streams Teeing
echo [O] Synchronizers
echo - - - - - - - - - - - - - - -
echo [P] Tasks
echo [Q] Web Client
echo [R] Web Server
echo - - - - - - - - - - - - - - -
echo [S] Web Sockets
echo - - - - - - - - - - - - - - -
echo.
echo - - - - - - - - - - - - - - -
echo [X] 'mvn clean install'
echo [Y] Build and Run All Together
echo [Z] Switch MVN Building OFF/ON
echo - - - - - - - - - - - - - - -
echo Press any other key to quit
set /P KEY="Select an option: "
if /i "%KEY:~0,1%"=="A" (
  set LABEL=[A] Basics
  call :Basics
) else if /i "%KEY:~0,1%"=="B" (
  set LABEL=[B] Collections
  call :Collections
) else if /i "%KEY:~0,1%"=="C" (
  set LABEL=[C] Dates and Times
  call :DatesAndTimes
) else if /i "%KEY:~0,1%"=="D" (
  set LABEL=[D] Files
  call :Files
) else if /i "%KEY:~0,1%"=="E" (
  set LABEL=[E] Methods
  call :Methods
) else if /i "%KEY:~0,1%"=="F" (
  set LABEL=[F] Processes
  call :Processes
) else if /i "%KEY:~0,1%"=="G" (
  set LABEL=[G] Reactive Streams
  call :ReactiveStreams
) else if /i "%KEY:~0,1%"=="H" (
  set LABEL=[H] Records
  call :Records
) else if /i "%KEY:~0,1%"=="I" (
  set LABEL=[I] Regex
  call :Regex
) else if /i "%KEY:~0,1%"=="J" (
  set LABEL=[J] Sorting
  call :Sorting
) else if /i "%KEY:~0,1%"=="K" (
  set LABEL=[K] Streams Collecting
  call :StreamsCollecting
) else if /i "%KEY:~0,1%"=="L" (
  set LABEL=[L] Streams Fragmentation
  call :StreamsFragmentation
) else if /i "%KEY:~0,1%"=="M" (
  set LABEL=[M] Streams Gathering
  call :StreamsGathering
) else if /i "%KEY:~0,1%"=="N" (
  set LABEL=[N] Streams Teeing
  call :StreamsTeeing
) else if /i "%KEY:~0,1%"=="O" (
  set LABEL=[O] Synchronizers
  call :Synchronizers
) else if /i "%KEY:~0,1%"=="P" (
  set LABEL=[P] Tasks
  call :Tasks
) else if /i "%KEY:~0,1%"=="Q" (
  set LABEL=[Q] Web Client
  call :WebClient
) else if /i "%KEY:~0,1%"=="R" (
  set LABEL=[R] Web Server
  call :WebServer
) else if /i "%KEY:~0,1%"=="S" (
  set LABEL=[S] Web Sockets
  call :WebSockets
) else if /i "%KEY:~0,1%"=="X" (
  set LABEL=[X] mvn clean install
  call :OnlyMavenBuild
) else if /i "%KEY:~0,1%"=="Y" (
  set LABEL=[Y] Build and Run All Together
  call :AllTogether
) else if /i "%KEY:~0,1%"=="Z" (
  set LABEL=[Z] Switch MVN Building OFF/ON
  call :MavenBuildSwitching 
) else (
  goto :eof
)
goto menu

REM =================================================================================================================================================
:Basics
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.about.basics.ApplicationForBasics
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Collections
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.collections.ApplicationForCollections
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:DatesAndTimes
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.dates.ApplicationForDatesAndTimes
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Files
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.files.ApplicationForFiles
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Methods
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.methods.ApplicationForMethods
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Processes
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.processes.ApplicationForProcesses
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:ReactiveStreams
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.reactive.streams.ApplicationForReactiveStreams
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Records
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.records.ApplicationForRecords
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Regex
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.regex.ApplicationForRegex
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Sorting
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.sorting.ApplicationForSorting
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:StreamsCollecting
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.streams.collecting.ApplicationForStreamsCollecting
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:StreamsFragmentation
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.streams.fragmentation.ApplicationForStreamsFragmentation
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:StreamsGathering
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.streams.gathering.ApplicationForGathering
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:StreamsTeeing
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.streams.teeing.ApplicationForCollectorsTeeing
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Synchronizers
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.synchronizers.ApplicationForSynchronizers
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:Tasks
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.tasks.ApplicationForTasks
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:WebClient
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.web.httpclient.ApplicationForWebClient
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:WebServer
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
pushd %cd%
cd ..
start %JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -cp %JARFILE% kp.web.httpserver.ApplicationForWebServer
popd
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:WebSockets
cls
if "%MVN_BLD_SWITCH%"=="ON" (
	call :MvnCleanInstall
)
set MAIN_CLASS=kp.web.sockets.ApplicationForSockets
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:OnlyMavenBuild
cls
call :MvnCleanInstall
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:AllTogether
cls
call :MvnCleanInstall
set MAIN_CLASS=kp.about.basics.ApplicationForBasics
call :RunApplication
set MAIN_CLASS=kp.collections.ApplicationForCollections
call :RunApplication
set MAIN_CLASS=kp.dates.ApplicationForDatesAndTimes
call :RunApplication
set MAIN_CLASS=kp.files.ApplicationForFiles
call :RunApplication
set MAIN_CLASS=kp.methods.ApplicationForMethods
call :RunApplication
set MAIN_CLASS=kp.processes.ApplicationForProcesses
call :RunApplication
set MAIN_CLASS=kp.reactive.streams.ApplicationForReactiveStreams
call :RunApplication
set MAIN_CLASS=kp.records.ApplicationForRecords
call :RunApplication
set MAIN_CLASS=kp.regex.ApplicationForRegex
call :RunApplication
set MAIN_CLASS=kp.sorting.ApplicationForSorting
call :RunApplication
set MAIN_CLASS=kp.streams.collecting.ApplicationForStreamsCollecting
call :RunApplication
set MAIN_CLASS=kp.streams.fragmentation.ApplicationForStreamsFragmentation
call :RunApplication
set MAIN_CLASS=kp.streams.gathering.ApplicationForGathering
call :RunApplication
set MAIN_CLASS=kp.streams.teeing.ApplicationForCollectorsTeeing
call :RunApplication
set MAIN_CLASS=kp.synchronizers.ApplicationForSynchronizers
call :RunApplication
set MAIN_CLASS=kp.tasks.ApplicationForTasks
call :RunApplication
set MAIN_CLASS=kp.web.httpclient.ApplicationForWebClient
call :RunApplication
set MAIN_CLASS=kp.web.sockets.ApplicationForSockets
call :RunApplication
call :RedLabelAndPause
cls
goto :eof

REM =================================================================================================================================================
:MavenBuildSwitching 
if "%MVN_BLD_SWITCH%"=="ON" (
   set MVN_BLD_SWITCH=OFF
) else (
   set MVN_BLD_SWITCH=ON
)
cls
goto :eof

REM =================================================================================================================================================
:MvnCleanInstall
pushd %cd%
cd ..
call %M2_HOME%\bin\mvn clean install
popd
goto :eof

REM =================================================================================================================================================
:RunApplication
pushd %cd%
cd ..
chcp 65001 > nul 2>&1
%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -cp %JARFILE% %MAIN_CLASS%
popd
goto :eof

REM =================================================================================================================================================
:RedLabelAndPause
powershell -Command Write-Host "FINISH %LABEL%" -foreground "Red"
pause
goto :eof

REM =================================================================================================================================================
:quit
echo.&pause
