# 🛠️ System Maintenance Guide

## Routine Maintenance Tasks

### Dependency Updates (Monthly)
1. **Frontend**: Run `npm outdated` to identify vulnerable or stale packages. Use `npm audit fix` for security patches. Test the application locally before committing `package-lock.json` changes.
2. **Backend**: Periodically review the `pom.xml` for Spring Boot version bumps. Ensure compatibility with the current Java 17 runtime.

### Security Patches (Immediate)
- Subscribe to CVE alerts for Spring Framework, React, and Vite.
- If a critical vulnerability is announced, branch from `main`, apply the patch, run the CI/CD pipeline, and hotfix production immediately.

### Database Backup & Verification (Weekly)
- Ensure the managed database provider (e.g., AWS RDS, Render PostgreSQL) is successfully capturing automated daily snapshots.
- Once a week, perform a dry-run restoration of a snapshot to a staging database to verify backup integrity.

### Log Rotation & Auditing (Weekly)
- Verify that backend application logs are being correctly rotated and aggregated (e.g., to AWS CloudWatch or Datadog).
- Audit logs for unusual spikes in `401 Unauthorized` or `403 Forbidden` responses, which may indicate a brute-force attack on the authentication endpoints.

## Disaster Recovery & Incident Response

### Tier 1 Incident: Database Corruption
1. **Isolate**: Put the application into "Maintenance Mode" via the frontend deployment settings to halt new transactions.
2. **Restore**: Spin up a new database instance using the most recent verified snapshot.
3. **Re-route**: Update the backend production environment variables to point to the restored database URI.
4. **Communicate**: Notify affected users of potential data loss since the last snapshot.

### Tier 2 Incident: Backend Server Crash (OOM / CPU Spike)
1. The cloud provider (Render/Railway) should automatically restart the Docker container.
2. Review the aggregated logs to identify the trigger (e.g., an un-paginated DB query causing an OutOfMemoryError).
3. Scale the service vertically (increase RAM) temporarily to handle the load while a code-level fix is developed and deployed.

### Tier 3 Incident: Compromised JWT Secret
1. Immediately generate a new cryptographic secret.
2. Update the backend environment variables and restart the service.
3. **Note**: This will instantly invalidate *all* currently active Access and Refresh tokens globally, forcing every user to log back in.
