#!/bin/bash
set -e

# Patch pg_hba.conf: replace scram-sha-256 with trust for all host connections
HBA_FILE="/var/lib/postgresql/data/pg_hba.conf"

if grep -q "scram-sha-256" "$HBA_FILE"; then
    sed -i 's/scram-sha-256/trust/g' "$HBA_FILE"
    echo "[init] Patched pg_hba.conf: scram-sha-256 -> trust"
    # Reload pg_hba.conf without restart
    psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT pg_reload_conf();" || true
fi

# Register worker nodes into coordinator (idempotent)
echo "[init] Registering Citus worker nodes..."
psql -v ON_ERROR_STOP=0 -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<-EOSQL
    SELECT citus_add_node('citus-worker-1', 5432) WHERE NOT EXISTS (
        SELECT 1 FROM pg_dist_node WHERE nodename = 'citus-worker-1' AND nodeport = 5432
    );
    SELECT citus_add_node('citus-worker-2', 5432) WHERE NOT EXISTS (
        SELECT 1 FROM pg_dist_node WHERE nodename = 'citus-worker-2' AND nodeport = 5432
    );
EOSQL
echo "[init] Workers registered successfully."
