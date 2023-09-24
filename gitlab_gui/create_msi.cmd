@echo off

setlocal EnableDelayedExpansion

for /f "tokens=1,2 delims==" %%a in (src\main\resources\info.config) do (
  if "%%a"=="version" (
    set VERSION=%%b
  )
)

echo %VERSION%

set JAR=gitlab_gui-%VERSION%.jar

jpackage --input target\ ^
  --name Gitlab_GUI ^
  --main-jar %JAR% ^
  --main-class org.gitlab_gui.Main ^
  --type msi   ^
  --win-shortcut ^
  --win-menu ^
  --app-version %VERSION% ^
  --vendor "Alexandros Antonakakis" ^
  --copyright "Copyright 2023 Alexandros Antonakakis" ^
  --verbose ^
  --java-options '--enable-preview'
