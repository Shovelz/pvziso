./gradlew :core:build :desktop:fatJar
cp desktop/build/libs/desktop-fat-1.0.jar .
rm -rf dist/mac/*
java -jar packr-all-4.0.0.jar packr-mac.json
cd dist/mac/
