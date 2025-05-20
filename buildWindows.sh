./gradlew :core:build :desktop:fatJar
cp desktop/build/libs/desktop-fat-1.0.jar .
rm -rf dist/windows64/*
java -jar packr-all-4.0.0.jar packr-windows.json
cd dist/windows64/
lutris
