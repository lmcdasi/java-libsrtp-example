#!/bin/sh -xf

CIPHER="AES_CM_128_HMAC_SHA1_80 inline:9cff72ce50f4c75e8c9ebfa7201c08d009f08490|2^31"
#"AES_CM_128_HMAC_SHA1_80 inline:Uh5H/RGebppNN03gV3At/uAHYDhp7nxQJ3n1xt|2^31"
DST_HOST="localhost"
DST_PORT="16384"

urlencode() {
    local raw="$1"
    printf "%s" "$raw" | sed 's/[^a-zA-Z0-9]/\0\0/g' | tr ' ' '+'
}

encoded_dst_port=$(urlencode "$DST_PORT")
encoded_cipher=$(urlencode "$CIPHER")

echo "Run recording"
curl "http://localhost:8080/executeTask?udpPort=${encoded_dst_port}&cipher=${encoded_cipher}"

