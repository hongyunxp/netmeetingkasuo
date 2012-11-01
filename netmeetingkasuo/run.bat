@echo off

set LIBDIR=web/WEB-INF/lib
set RUN=jre6/bin/javaw
setlocal enabledelayedexpansion

for %%i in (%LIBDIR%\*.jar,%LIBDIR%\*.dll) do ( 
	set EPATH=!EPATH!%%i^;
) 

start %RUN% -cp .;%EPATH% com.meeting "%cd%"