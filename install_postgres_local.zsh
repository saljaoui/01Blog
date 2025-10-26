#!/usr/bin/env zsh

# ================================================================
# 🐘 Local PostgreSQL Installer (No sudo required)
# Works in Zsh — installs PostgreSQL into ~/pgsql
# ================================================================

set -e

# --- Configuration ---
PG_VERSION="15.4"
INSTALL_DIR="$HOME/pgsql"
DATA_DIR="$INSTALL_DIR/data"
SRC_DIR="$HOME/postgres_src"
TARBALL="postgresql-$PG_VERSION.tar.bz2"
URL="https://ftp.postgresql.org/pub/source/v$PG_VERSION/$TARBALL"

echo "🔧 Using PostgreSQL $PG_VERSION"
echo "📦 Install prefix: $INSTALL_DIR"
echo "🗃️  Data dir: $DATA_DIR"
echo

# --- Create folders ---
mkdir -p "$SRC_DIR"
cd "$SRC_DIR"

# --- Download source if missing ---
if [ ! -f "$TARBALL" ]; then
  echo "⬇️  Downloading PostgreSQL source..."
  curl -O "$URL"
else
  echo "✅ Found tarball: $TARBALL"
fi

# --- Extract ---
echo "📂 Extracting..."
tar -xjf "$TARBALL"
cd "postgresql-$PG_VERSION"

# --- Configure without readline ---
echo "⚙️  Configuring PostgreSQL (no readline, no ICU)..."
./configure --prefix="$INSTALL_DIR" --without-readline --without-zlib > configure.log 2>&1

# --- Build ---
echo "🏗️  Building PostgreSQL (this may take a few minutes)..."
make -j$(nproc) > build.log 2>&1

# --- Install ---
echo "📦 Installing to $INSTALL_DIR..."
make install > install.log 2>&1

# --- Initialize database ---
echo "🧰 Initializing database cluster..."
mkdir -p "$DATA_DIR"
"$INSTALL_DIR/bin/initdb" -D "$DATA_DIR"

# --- Environment setup ---
echo "🪄 Add these lines to your ~/.zshrc:"
echo "export PATH=\"$INSTALL_DIR/bin:\$PATH\""
echo "export PGDATA=\"$DATA_DIR\""
echo
echo "Then restart your shell or run:"
echo "  source ~/.zshrc"
echo
echo "🎉 PostgreSQL $PG_VERSION installed locally!"
echo "Run: pg_ctl -D \"$DATA_DIR\" start"
echo "Then connect: psql -U $(whoami)"
