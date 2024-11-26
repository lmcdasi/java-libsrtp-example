package org.lmcdasi.demo.srtp.common;

public enum SrtpErrStatus {
	SRTP_ERR_STATUS_OK(0),				/** < nothing to report */
	SRTP_ERR_STATUS_FAIL(1),			/** < unspecified failure */
	SRTP_ERR_STATUS_BAD_PARAM(2),		/** < unsupported parameter */
	SRTP_ERR_STATUS_ALLOC_FAIL(3),		/** < couldn't allocate memory */
	SRTP_ERR_STATUS_DEALLOC_FAIL(4),	/** < couldn't deallocate properly */
	SRTP_ERR_STATUS_INIT_FAIL(5),		/** < couldn't initialize */
	SRTP_ERR_STATUS_TERMINUS(6),		/** < can't process as much data as requested */
	SRTP_ERR_STATUS_AUTH_FAIL(7),		/** < authentication failure */
	SRTP_ERR_STATUS_CIPHER_FAIL(8),	/** < cipher failure */
	SRTP_ERR_STATUS_REPLAY_FAIL(9),	/** < replay check failed (bad index) */
	SRTP_ERR_STATUS_REPLAY_OLD(10),	/** < replay check failed (index too old) */
	SRTP_ERR_STATUS_ALGO_FAIL(11),		/** < algorithm failed test routine */
	SRTP_ERR_STATUS_NO_SUCH_OP(12),	/** < unsupported operation */
	SRTP_ERR_STATUS_NO_CTX(13),		/** < no appropriate context found */
	SRTP_ERR_STATUS_CANT_CHECK(14),	/** < unable to perform desired validation */
	SRTP_ERR_STATUS_KEY_EXPIRED(15),	/** < can't use key anymore */
	SRTP_ERR_STATUS_SOCKET_ERR(16),	/** < error in use of socket */
	SRTP_ERR_STATUS_SIGNAL_ERR(17),	/** < error in use POSIX signals */
	SRTP_ERR_STATUS_NONCE_BAD(18),		/** < nonce check failed */
	SRTP_ERR_STATUS_READ_FAIL(19),		/** < couldn't read data */
	SRTP_ERR_STATUS_WRITE_FAIL(20),	/** < couldn't write data */
	SRTP_ERR_STATUS_PARSE_ERR(21),		/** < error parsing data */
	SRTP_ERR_STATUS_ENCODE_ERR(22),	/** < error encoding data */
	SRTP_ERR_STATUS_SEMAPHORE_ERR(23),	/** < error while using semaphores */
	SRTP_ERR_STATUS_PFKEY_ERR(24),		/** < error while using pfkey */
	SRTP_ERR_STATUS_BAD_MKI(25),		/** < error MKI present in packet is invalid */
	SRTP_ERR_STATUS_PKT_IDX_OLD(26),	/** < packet index is too old to consider */
	SRTP_ERR_STATUS_PKT_IDX_ADV(27);	/** < packet index advanced, reset needed */

	private final int value;

	SrtpErrStatus(final int newValue) {
		value = newValue;
	}

	public int getValue() {
		return value;
	}
}
