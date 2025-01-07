#!/usr/bin/env bash

#
# Oppretter environmentvariabler for brukernavn/passord som lagres i Vault.
#

echo "Leser secrets fra disk til environment"

SERVICEUSER_PATH=/secrets/serviceuser/srv_oebs

if [ -f $SERVICEUSER_PATH/username ]; then
    export SERVICEUSER_USERNAME=$(cat $SERVICEUSER_PATH/username)
    echo "Eksporterer variabel SERVICEUSER_USERNAME"
fi

if [ -f $SERVICEUSER_PATH/password ]; then
    export SERVICEUSER_PASSWORD=$(cat $SERVICEUSER_PATH/password)
    echo "Eksporterer variabel SERVICEUSER_PASSWORD"
fi

APPSUSER_PATH=/var/run/secrets/nais.io/vault

if [ -f $APPSUSER_PATH/apps-username ]; then
    export APPS_USERNAME=$(cat $APPSUSER_PATH/apps-username)
    echo "Eksporterer variabel APPSUSER_PATH"
fi

if [ -f $APPSUSER_PATH/apps-password ]; then
    export APPS_PASSWORD=$(cat $APPSUSER_PATH/apps-password)
    echo "Eksporterer variabel APPS_PASSWORD"
fi