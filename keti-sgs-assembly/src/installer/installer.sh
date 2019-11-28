
declare -x INSTALL_TYPE='local'

################################################################################
# Check an existence of unzip, or exit
function ensure-unzip() {
  message-on "Checking unzip..."
  which unzip &>/dev/null
  if [ "$?" -ne 0 ]; then
    warning "Please install unzip!!"
    exit 1
  else
    success "OK"
  fi # [ "$?" -ne 0 ]
}

function ensure-java() {
  message-on "Checking java..."
  which java &>/dev/null
  if [ "$?" -ne 0 ]; then
    warning "Please install java!!"
    exit 1
  else
    success "OK"
  fi # [ "$?" -ne 0 ]
}

function ensure-install-type() {
  case "$1" in
    "local" | "system" | "service")
      success "Installation type: $1"
      ;;
    *)
      error "Unknown installation type: $1"
      ;;
  esac
}

################################################################################
# Install locally
function install-local() {
  ensure-unzip
  ensure-java

  message-on "Unarchiving to $TARGET..."
  echo $TARGET
  echo $0
  unzip -o -d $TARGET $0 &>/dev/null
  success "OK"
}

################################################################################
function print-banner() {
  if [ `tput cols` -le 162 ]; then
    print-small-banner
  else
    print-big-banner
  fi

  # horizontal separator
  echo ""
}

################################################################################
# Main process

if [ -z "$TARGET" ]; then
  case "$INSTALL_TYPE" in
    "local")
      TARGET=`pwd`
      ;;
    "system" | "service")
      TARGET=/usr/share
      ;;
    *)
      error "Unknown installation type: $1"
      ;;
  esac
fi
export TARGET
export KETI_HOME="$TARGET/keti-sgs-api-server-$VERSION"

print-banner

ensure-install-type $INSTALL_TYPE

install-$INSTALL_TYPE

if [ `tput cols` -le 162 ]; then
  cat $KETI_HOME/doc/welcome-install-small.txt
else
  cat $KETI_HOME/doc/welcome-install-big.txt
fi

success "Success to install the keti iot!!"
echo ""

# Don't remove next statement
# It makes the separation with zip part
exit 0
