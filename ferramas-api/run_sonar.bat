@echo off
.\mvnw.cmd clean verify sonar:sonar -Dsonar.projectKey=ferramas-api -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqp_d214322665542caffc2a35a5b14fe31384ea35d0
