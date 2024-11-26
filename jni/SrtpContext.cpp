#include "SrtpContext.h"

srtp_crypto_policy_t SrtpContext::Builder::getSrtpCryptoPolicyT(const std::string& srtpAuth, const std::string& srtpCipher) {
    srtp_crypto_policy_t srtpCryptoPolicy = {};
    if (srtpCipher == "aes-128-icm") {
        if (srtpAuth == "hmac-sha1-32") {
            srtp_crypto_policy_set_aes_cm_128_hmac_sha1_32(&srtpCryptoPolicy);
        } else if (srtpAuth == "hmac-sha1-80") {
            srtp_crypto_policy_set_rtp_default(&srtpCryptoPolicy);
        } else {
            throw std::runtime_error("Unsupported srtpAuth: " + srtpAuth + " with srtpCipher: " + srtpCipher);
        }
    } else if (srtpCipher == "aes-256-icm") {
        if (srtpAuth == "hmac-sha1-32") {
            srtp_crypto_policy_set_aes_cm_256_hmac_sha1_32(&srtpCryptoPolicy);
        } else if (srtpAuth == "hmac-sha1-80") {
            srtp_crypto_policy_set_aes_cm_256_hmac_sha1_80(&srtpCryptoPolicy);
        } else {
            throw std::runtime_error("Unsupported srtpAuth: " + srtpAuth + " with srtpCipher: " + srtpCipher);
        }
    } else {
        throw std::runtime_error("Unsupported srtpCipher: " + srtpCipher + " with srtpAuth: " + srtpAuth);
    }

    return srtpCryptoPolicy;
}

std::vector<std::string> split(const std::string& str, char delimiter) {
    std::vector<std::string> tokens;
    std::string token;
    std::istringstream tokenStream(str);
    while (std::getline(tokenStream, token, delimiter)) {
        tokens.push_back(token);
    }
    return tokens;
}

std::string SrtpContext::Builder::getSrtpAuth(const std::vector<std::string>& cipherSplit) {
    std::string s = cipherSplit[0].substr(11);
    std::transform(s.begin(), s.end(), s.begin(), [](unsigned char c) {
        if (c == '_') return '-';
        return static_cast<char>(std::tolower(c));
    });
    return s;
    //return cipherSplit[0].substr(11).replace("_", "-").toLowerCase();
}

std::string SrtpContext::Builder::getSrtpCipher(const std::vector<std::string>& cipherSplit) {
    std::string s = cipherSplit[0].substr(0, 10);
    std::transform(s.begin(), s.end(), s.begin(), [](unsigned char c) {
        if (c == '_') return '-';
        return static_cast<char>(std::tolower(c));
    });
    if (s == "aes-cm-128") { s = "aes-128-cm"; }
    size_t pos = s.find("-cm");
    if (pos != std::string::npos) {
        s.replace(pos, 3, "-icm");
    }
    return s;
}

std::string SrtpContext::Builder::base64Decode(const std::string& encoded) {
    return base64::from_base64(encoded);
}

std::vector<unsigned char> SrtpContext::Builder::getDecodedSrtpKey(const std::vector<std::string>& cipherSplit) {
    std::string base64EncodedKey = split(cipherSplit[1], ':')[1];
    base64EncodedKey = split(base64EncodedKey, '|')[0];
    std::string decodedKey = base64Decode(base64EncodedKey);
    return std::vector<unsigned char>(decodedKey.begin(), decodedKey.end());
}

// Build method for SrtpContext
SrtpContext SrtpContext::Builder::build() {
    std::vector<std::string> cipherSplit = split(cipher, ' ');
    std::string srtpAuth = getSrtpAuth(cipherSplit);
    std::string srtpCipher = getSrtpCipher(cipherSplit);

    srtp_policy_t srtpPolicy = {};
    srtpPolicy.rtp = getSrtpCryptoPolicyT(srtpAuth, srtpCipher);
    srtpPolicy.rtp.sec_serv = srtp_sec_serv_t::sec_serv_conf_and_auth;
    srtpPolicy.rtcp = {};
    srtpPolicy.rtcp.sec_serv = srtp_sec_serv_t::sec_serv_none;

    auto srtpKey = getDecodedSrtpKey(cipherSplit);
    srtpPolicy.key = new unsigned char[srtpKey.size()];
    std::memcpy(srtpPolicy.key, srtpKey.data(), srtpKey.size());

    srtpPolicy.ssrc = {};
    srtpPolicy.ssrc.type = srtp_ssrc_type_t::ssrc_any_inbound;
    srtpPolicy.ssrc.value = 0;

    srtpPolicy.window_size = 0;

    srtp_t srtpReceiver;
    if (srtp_create(&srtpReceiver, &srtpPolicy) != 0) {
        throw std::runtime_error("Failed to create SRTP session");
    }

    return SrtpContext(srtpPolicy, srtpReceiver);
}