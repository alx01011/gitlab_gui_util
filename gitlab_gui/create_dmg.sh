# get version from src/main/resources/info.config
VERSION=$(grep 'version=' src/main/resources/info.config | awk -F'=' '{print $2}')
echo "Creating DMG for version $VERSION"

# for ARM or Intel
ARCH=$1

JAR="gitlab_gui-$VERSION.jar"


jpackage --input target/ \
  --name Gitlab_GUI_$ARCH \
  --main-jar $JAR \
  --main-class org.gitlab_gui.Main \
  --type dmg \
  --app-version $VERSION \
  --vendor "Alexandros Antonakakis" \
  --copyright "Copyright 2023 Alexandros Antonakakis" \
  --mac-package-name "Gitlab_GUI" \
  --mac-package-identifier "org.gitlab_gui" \
  --verbose \
  --java-options '--enable-preview'
