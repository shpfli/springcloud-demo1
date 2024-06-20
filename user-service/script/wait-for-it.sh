#!/bin/sh

set -euo pipefail

host="$1"
shift
cmd="$@"

until curl --silent --fail "$host"; do
  printf '.'
  sleep 1
done

echo "$host is up"
exec "$cmd"