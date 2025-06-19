#!/bin/sh

RAND_HEX=$(openssl rand -hex 512)
jinja2 ./app/src/main/resources/application.yaml.jinja2 -D random_hex="$RAND_HEX" > ./app/src/main/resources/application.yaml