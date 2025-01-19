package dev.batugokce.smartcardmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.LoginException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartCardException;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.SmartOp;

import java.security.cert.CertificateEncodingException;
import java.security.spec.AlgorithmParameterSpec;


public class SmartCardManager extends SmartCardManagerBase {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartCardManager.class);

    private static Object lockObject = new Object();
    private static SmartCardManager mSCManager;


    public SmartCardManager() throws SmartCardException {
        super();
    }

    public static SmartCardManager getInstance() throws SmartCardException {

        synchronized (lockObject) {
            if (mSCManager == null) {
                mSCManager = new SmartCardManager();
                return mSCManager;
            } else {
                try {

                    if (mSCManager.getSlotCount() < SmartOp.getCardTerminals().length) {
                        LOGGER.debug("New card pluged in to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    String availableSerial = null;
                    try {
                        availableSerial = StringUtil.toString(mSCManager.getBasicSmartCard().getSerial());
                    } catch (SmartCardException ex) {
                        LOGGER.debug("Card removed");
                        mSCManager = null;
                        return getInstance();
                    }
                    if (!mSCManager.getSelectedSerialNumber().equals(availableSerial)) {
                        LOGGER.debug("Serial number changed. New card is placed to system");
                        mSCManager = null;
                        return getInstance();
                    }

                    return mSCManager;
                } catch (SmartCardException e) {
                    mSCManager = null;
                    throw e;
                }
            }
        }
    }

    public static void reset() throws SmartCardException {
        synchronized (lockObject) {
            mSCManager = null;
        }
    }

    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate());
    }

    public synchronized BaseSigner getSigner(String aCardPIN, ECertificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {
        return getSignerBase(aCardPIN, aCert.asX509Certificate(), aSigningAlg, aParams);
    }


    public synchronized ECertificate getSignatureCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {

        byte[] encodedX509Cert = null;
        try {
            encodedX509Cert = getSignatureCertificateBase(checkIsQualified, checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }

        return new ECertificate(encodedX509Cert);
    }

    public synchronized ECertificate getSignatureCertificate(boolean isQualified) throws ESYAException {
        return getSignatureCertificate(isQualified, !isQualified);
    }

    public synchronized ECertificate getEncryptionCertificate(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {
        byte[] encodedX509Cert = null;
        try {
            encodedX509Cert = getEncryptionCertificateBase(checkIsQualified, checkBeingNonQualified).getEncoded();
        } catch (CertificateEncodingException e) {
            throw new ESYAException("Error in encoding X509 Certificate");
        }
        return new ECertificate(encodedX509Cert);
    }
}
