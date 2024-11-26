#ifndef SRTP_CONTEXT_H
#define SRTP_CONTEXT_H

#include <algorithm>
#include <iostream>
#include <sstream>
#include <memory>
#include <string>
#include <stdexcept>
#include <cstring>
#include <vector>
#include "srtp.h"
#include "base64.hpp"

class SrtpContext {
private:
    srtp_policy_t srtpPolicy;
    srtp_t srtpReceiver;

    SrtpContext(const srtp_policy_t& policy, const srtp_t& srtp_receiver) : srtpPolicy(policy), srtpReceiver(srtp_receiver) {}

public:
    ~SrtpContext() {
        if (srtpPolicy.key) {
            delete[] srtpPolicy.key;
        }
    }

    const srtp_policy_t& getPolicy() const { return srtpPolicy; }

    class Builder {
    private:
        std::string cipher;

        std::string base64Decode(const std::string&);
        std::vector<unsigned char> getDecodedSrtpKey(const std::vector<std::string>&);
        std::string getSrtpAuth(const std::vector<std::string>&);
        std::string getSrtpCipher(const std::vector<std::string>&);
        srtp_crypto_policy_t getSrtpCryptoPolicyT(const std::string&, const std::string&);

    public:
        Builder& withCipher(const std::string& cipherStr) {
            cipher = cipherStr;
            return *this;
        }

        SrtpContext build();
    };
};

#endif // SRTP_CONTEXT_H
