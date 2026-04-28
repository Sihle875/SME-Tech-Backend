@echo off
mvnw.cmd test -Dtest="*IntegrationTest" -Dspring.profiles.active=test -Dsurefire.failIfNoSpecifiedTests=false
