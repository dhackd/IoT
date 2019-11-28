#!/bin/bash
# resolve links - $0 may be a softlink
if [ -z "$PROJECT_HOME" ];then
  PRG="$0"
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=`dirname "$PRG"`/"$link"
    fi
  done
  
  cd $(dirname $PRG)
  export PROJECT_HOME=`pwd`
  cd -&>/dev/null
fi

################################################################################
# Print color
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

################################################################################
# Definition
VERSION=$(grep ' version ' $PROJECT_HOME/build.gradle | cut -d"'" -f2)
BUILD_WORKSPACE=$PROJECT_HOME/build
APP_NAME=keti-sgs
APP_FULLNAME="$APP_NAME-api-server-$VERSION"
INSTALLER_NAME="$APP_FULLNAME-installer.bin"

if [ -z "$VERSION" ]; then
  echo "The version not detected. Check build script or environment."
  exit 1
fi

###############################################################################
# Banner
BIG_BANNER_FUNC=$(cat <<EOF
function print-big-banner() {
  cat <<EOF1
    `cat $PROJECT_HOME/$APP_NAME-assembly/src/big-logo.txt`
EOF1
}
EOF
)

SMALL_BANNER_FUNC=$(cat <<EOF
function print-small-banner() {
  cat <<EOF1
    `cat $PROJECT_HOME/$APP_NAME-assembly/src/small-logo.txt`
EOF1
}
EOF
)

################################################################################
# Inject a file contaianing version information
function inject-version() {
  TARGET_DIR=$1
  cat <<EOF >$TARGET_DIR/VERSION
$VERSION
EOF
}

################################################################################
# Copy test script files to a target directory
function inject-testscripts() {
  TARGET_DIR=$1
  mkdir "$TARGET_DIR/lib/test"
  cp -R "$PROJECT_HOME/$APP_NAME-test/function-test/" "$TARGET_DIR/lib/test/function-test"
}

################################################################################
# Download open source licenses
function rake-licenses() {
  LICENSE_XML=$1
  TARGET_DIR=$2
  LICENSE_URLS=`cat $LICENSE_XML |grep '<license' |grep 'url=' | sed -E "s/(.*url=')|('>)//g" | sed -E "s/\/$//g" | sort | uniq`
  mkdir -p $TARGET_DIR
  cd $TARGET_DIR
  for LICENSE_URL in $LICENSE_URLS
  do
    echo "Downloading $LICENSE_URL..."
    curl -sJLO $LICENSE_URL
  done
  cd - >/dev/null
}

################################################################################
# build
echo "Version: $VERSION"

echo "Building java modules..."
cd $PROJECT_HOME
$PROJECT_HOME/gradlew clean build distZip || exit 1 #downloadLicenses

echo "Unarchiving assembly..."
cd $PROJECT_HOME/$APP_NAME-assembly/build/distributions
unzip -q "$PROJECT_HOME/$APP_NAME-assembly/build/distributions/$APP_FULLNAME.zip"
inject-version "$PROJECT_HOME/$APP_NAME-assembly/build/distributions/$APP_FULLNAME"
mv "$PROJECT_HOME/$APP_NAME-assembly/build/distributions/$APP_FULLNAME" "$PROJECT_HOME/$APP_NAME-assembly/build/distributions/$APP_NAME" 

echo "Reassembling..."
rm -rf $BUILD_WORKSPACE
mkdir $BUILD_WORKSPACE
cd $PROJECT_HOME/$APP_NAME-assembly/build/distributions
unzip -q $APP_FULLNAME.zip -d $BUILD_WORKSPACE
inject-version $BUILD_WORKSPACE/$APP_FULLNAME

cd $BUILD_WORKSPACE
tar -czf ${APP_FULLNAME}.tar.gz $APP_FULLNAME
( which zip || sudo yum install -y zip ) &&  zip -r ${APP_FULLNAME}.zip $APP_FULLNAME
rm -rf $APP_FULLNAME

cat <<EOF | cat - $PROJECT_HOME/$APP_NAME-assembly/src/assembly/bin/keti-sgs-common $PROJECT_HOME/$APP_NAME-assembly/src/installer/installer.sh $BUILD_WORKSPACE/${APP_FULLNAME}.zip > $BUILD_WORKSPACE/$INSTALLER_NAME
#!/bin/bash
$BIG_BANNER_FUNC
$SMALL_BANNER_FUNC
declare -x VERSION=$VERSION
EOF
chmod 755 $BUILD_WORKSPACE/$INSTALLER_NAME