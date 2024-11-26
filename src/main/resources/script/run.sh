#!/bin/sh -xf

AUDIO_FILE=""
CIPHER="AES_CM_128_HMAC_SHA1_80 inline:9cff72ce50f4c75e8c9ebfa7201c08d009f08490|2^31"
ENCODED_CIPHER="f5c7dfef671ee747f873be5ef1cf5e6df6bbdb4d5cd3c774d3d7f4f38f74"
#"AES_CM_128_HMAC_SHA1_80 inline:Uh5H/RGebppNN03gV3At/uAHYDhp7nxQJ3n1xt|2^31"
DST_HOST="localhost"
DST_PORT="16384"

encoded_dst_port=$(echo -n "$DST_PORT" | jq -sRr @uri)
encoded_cipher=$(echo -n "$CIPHER" | jq -sRr @uri)

echo "Starting srtp receiver - default JNA"
java --enable-preview --enable-native-access=ALL-UNNAMED -Dspring.profiles.active=production \
     -Dapplication.ipAddress="${DST_HOST}" -jar ../target/demo-0.0.1-SNAPSHOT.jar &

#example using builtin-native
#java --enable-preview --enable-native-access=ALL-UNNAMED -Dspring.profiles.active=production \
#     -Dapplication.ipAddress="${DST_HOST}" -Dapplication.use-jna=false \
#     -Dapplication.use-builtin-native=true -jar ./target/demo-0.0.1-SNAPSHOT.jar

echo "Run recording"
curl "http://localhost:8080/StartRecording?udpPort=${encoded_dst_port}&cipher=${encoded_cipher}"

echo "Starting srtp sender"
gst-launch-1.0.exe -v filesrc location="./playMono.wav" ! wavparse ! mulawdec ! mulawenc ! rtppcmupay ! "application/x-rtp, payload=(int)0, ssrc=(uint)6694999238" ! srtpenc key=${ENCODED_CIPHER} rtp-cipher=aes-128-icm rtp-auth=hmac-sha1-80 ! udpsinkhost=${DST_HOST} port=${DST_PORT}
