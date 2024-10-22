#!/bin/bash

# Wait for PostgreSQL to become available
until psql $DATABASE_URL -c '\q'; do
    >&2 echo "Postgres is unavailable - sleeping"
    sleep 1
done

# Run migrations
diesel migration run

# Start the Rust API
exec "$@"
