# Chanko-Social

"Chanko" is a sumo-focused social app in which users can view related news, create forum posts, and create and participate in fantasy leagues.

# Running Locally

To run Chanko, run the following in the root directory:

1. Database: `mise run db:up`, then wait until `docker compose ps` shows healthy
2. Backend: `mise run api` (Migrations run automatically on startup)
3. Frontend: `mise run web`

Stop, in reverse order:

Frontend: Ctrl+C in its terminal
Backend: Ctrl+C in its terminal
Database: mise run db:down. This keeps your data

# Database Related Documentation

You never run migrations by hand. Every time the API starts, Flyway:

1. Reads its `flyway_schema_history` table to see which versions are already applied.
2. Finds any `V<n>\_\_\*.sql` files in `db/migration/` with a higher version.
3. Applies them in order, inside a transaction, and records each one.

Creating a new table

1. Create the next file in order, for example V2\_\_create_posts.sql.
2. Add the matching JPA entity in the same commit. Otherwise ddl-auto: validate fails on startup.
3. Restart the API and check the logs for Successfully applied 1 migration.

When to reset the DB

Reset only when you want to throw away the local data:

`mise run db:reset` # deletes the volume and starts an empty database
`mise run api` # Flyway rebuilds every table from V1 upward

Backing up and restoring data

Back up:

`docker compose exec db pg_dump -U chanko -d chanko -Fc > backups/chanko-$(date +%F).dump`

Restore, which replaces the current database contents:

`docker compose exec -T db pg_restore -U chanko -d chanko --clean --if-exists < backups/chanko-20`

Good reasons to reset:

You edited a migration that was already applied, and Flyway reports a checksum mismatch.
You changed POSTGRES\_\* credentials in .env.
