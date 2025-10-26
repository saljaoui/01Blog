#!/usr/bin/env zsh
set -e

# === PostgreSQL local paths ===
export PATH="$HOME/pgsql/bin:$PATH"
export PGDATA="$HOME/pgsql/data"

# === Database configuration ===
DB_NAME="blogdb"
DB_USER="saljaoui"
DB_PASS="123"

echo "🔧 Checking PostgreSQL server..."

# === Start PostgreSQL if not running ===
if ! pg_ctl status -D "$PGDATA" >/dev/null 2>&1; then
  echo "🚀 Starting PostgreSQL..."
  pg_ctl -D "$PGDATA" -l "$HOME/pgsql/logfile" start
  sleep 2
else
  echo "✅ PostgreSQL is already running."
fi

# === Use 'postgres' DB for admin tasks ===
echo "🧩 Creating database and user if needed..."

# Create user if not exists
USER_EXISTS=$(psql -d postgres -tAc "SELECT 1 FROM pg_roles WHERE rolname='$DB_USER';" || true)
if [[ "$USER_EXISTS" != "1" ]]; then
  echo "👤 Creating user '$DB_USER'..."
  createuser -s "$DB_USER"
  echo "✅ User '$DB_USER' created."
else
  echo "ℹ️ User '$DB_USER' already exists."
fi

# Set password for user
psql -d postgres -c "ALTER USER \"$DB_USER\" WITH PASSWORD '$DB_PASS';" >/dev/null
echo "🔐 Password set for user '$DB_USER'."

# Create DB if not exists
DB_EXISTS=$(psql -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME';" || true)
if [[ "$DB_EXISTS" != "1" ]]; then
  echo "🗃️ Creating database '$DB_NAME'..."
  createdb -O "$DB_USER" "$DB_NAME"
  echo "✅ Database '$DB_NAME' created."
else
  echo "ℹ️ Database '$DB_NAME' already exists."
fi

# Test connection
echo "🔎 Testing connection..."
if psql -U "$DB_USER" -d "$DB_NAME" -c "\q" >/dev/null 2>&1; then
  echo "🎉 PostgreSQL setup complete!"
  echo "-----------------------------"
  echo "Database: $DB_NAME"
  echo "User: $DB_USER"
  echo "Password: $DB_PASS"
  echo "Connection URL: jdbc:postgresql://localhost:5432/$DB_NAME"
else
  echo "❌ Connection test failed. Check your PostgreSQL server."
fi
