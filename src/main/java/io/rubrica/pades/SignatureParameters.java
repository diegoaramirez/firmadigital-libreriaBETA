package io.rubrica.pades;

import java.security.cert.Certificate;

public class SignatureParameters {

	/** The signature creation reason */
	private String reason;

	/** The contact info */
	private String contactInfo;

	/** The signer's location */
	private String location;

	private String signerName;

	public String getReason() {
		return this.reason;
	}

	public void setReason(final String reason) {
		this.reason = reason;
	}

	public String getContactInfo() {
		return this.contactInfo;
	}

	public void setContactInfo(final String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSignerName() {
		return signerName;
	}

	public void setSignerName(final String signerName) {
		this.signerName = signerName;
	}

	public Certificate[] getChain() {
		return null;
	}

	public String getTsaUrl() {
        return null;
    }

    public String getTsaUsername() {
        return null;
    }

    public String getTsaPassword() {
        return null;
    }
}
