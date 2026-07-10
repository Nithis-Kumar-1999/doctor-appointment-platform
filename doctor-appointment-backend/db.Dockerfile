FROM mysql:8.0

# Render requires a Dockerfile to deploy a private service via their Docker environment.
# This minimal Dockerfile simply wraps the official MySQL 8 image.
# Environment variables for root password, user, password, and database
# will be securely injected by the render.yaml blueprint.
