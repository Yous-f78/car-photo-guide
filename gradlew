#!/bin/sh
# Gradle wrapper bootstrap (minimal). For a real build, use the official wrapper
# jar: run `gradle wrapper` once if the jar is missing.
exec gradle "$@"
