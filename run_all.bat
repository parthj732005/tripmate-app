@echo off
echo =============================
echo Starting TripMate Backend...
echo =============================
cd backend
start cmd /k "python app.py"
cd ..

timeout /t 3

echo =============================
echo Compiling Frontend...
echo =============================
cd frontend
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp "../lib/json-20210307.jar" Main.java

echo =============================
echo Starting Frontend...
echo =============================
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/json-20210307.jar" Main

pause
