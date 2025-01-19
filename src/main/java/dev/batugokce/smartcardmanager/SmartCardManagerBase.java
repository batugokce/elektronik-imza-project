package dev.batugokce.smartcardmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.common.ESYAException;
import tr.gov.tubitak.uekae.esya.api.common.crypto.Algorithms;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.StringUtil;
import tr.gov.tubitak.uekae.esya.api.common.util.bag.Pair;
import tr.gov.tubitak.uekae.esya.api.crypto.alg.SignatureAlg;
import tr.gov.tubitak.uekae.esya.api.crypto.util.ECUtil;
import tr.gov.tubitak.uekae.esya.api.smartcard.apdu.APDUSmartCard;
import tr.gov.tubitak.uekae.esya.api.smartcard.pkcs11.*;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SmartCardManagerBase {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartCardManagerBase.class);

    private static boolean useAPDU = false;
    protected BaseSmartCard bsc;
    protected BaseSigner mSigner;
    private int mSlotCount = 0;
    private String mSerialNumber;

    private X509Certificate mSignatureCert;
    private X509Certificate mEncryptionCert;


    /**
     * @throws SmartCardException
     */
    public SmartCardManagerBase() throws SmartCardException {

        try {
            LOGGER.debug("New SmartCardManager will be created");
            String[] terminals = SmartOp.getCardTerminals();
            String terminal;

            if (terminals == null || terminals.length == 0)
                throw new SmartCardException("No terminal found");

            LOGGER.debug("Terminal count : " + terminals.length);

            int index = 0;
            if (terminals.length == 1)
                terminal = terminals[index];
            else {
                index = askOption(null, null, terminals, "Terminal List", new String[]{"OK"});
                terminal = terminals[index];
            }

            boolean apduSupport = false;

            try {
                apduSupport = APDUSmartCard.isSupported(terminal);
            } catch (Exception ex) {
                LOGGER.error("APDU Smartcard cannot be created. Probably AkisCIF.jar is missing");
                apduSupport = false;
            }

            if (useAPDU && apduSupport) {
                LOGGER.debug("APDU Smartcard will be created");
                APDUSmartCard asc = new APDUSmartCard();
                CardTerminal ct = TerminalFactory.getDefault().terminals().getTerminal(terminal);
                asc.openSession(ct);
                bsc = asc;
            } else {
                LOGGER.debug("PKCS11 Smartcard will be created");
                Pair<Long, CardType> slotAndCardType = SmartOp.getSlotAndCardType(terminal);
                bsc = new P11SmartCard(slotAndCardType.getObject2());
                bsc.openSession(slotAndCardType.getObject1());
            }

            mSerialNumber = StringUtil.toString(bsc.getSerial());
            mSlotCount = terminals.length;
        } catch (SmartCardException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new SmartCardException("Smart Card IO exception", e);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new SmartCardException("General Exception", e);
        }
    }

    public static void useAPDU(boolean aUseAPDU) {
        useAPDU = aUseAPDU;
    }

    public BaseSmartCard getBasicSmartCard() {
        return bsc;
    }


    /**
     * Logouts from smart card.
     *
     * @throws SmartCardException
     */
    public synchronized void logout() throws SmartCardException {
        mSigner = null;
        bsc.logout();
    }

    public synchronized BaseSigner getSignerBase(String aCardPIN, X509Certificate aCert) throws SmartCardException, LoginException {

        if (mSigner == null) {
            bsc.login(aCardPIN);

            String algorithmType = aCert.getPublicKey().getAlgorithm();

            if (algorithmType.equals("RSA")) {
                mSigner = bsc.getSigner(aCert, Algorithms.SIGNATURE_RSA_SHA256);
            } else if (algorithmType.equals("EC")) {
                try {
                    SignatureAlg signatureAlg = ECUtil.getConvenientECSignatureAlgForECCertificate(new ECertificate(aCert.getEncoded()));
                    mSigner = bsc.getSigner(aCert, signatureAlg.getName());
                } catch (Exception e) {
                    throw new SmartCardException(e);
                }
            } else {
                throw new SmartCardException("Unknown algorithm type: " + algorithmType);
            }
        }
        return mSigner;
    }

    public synchronized BaseSigner getSignerBase(String aCardPIN, X509Certificate aCert, String aSigningAlg, AlgorithmParameterSpec aParams) throws SmartCardException, LoginException {

        if (mSigner == null) {
            bsc.login(aCardPIN);
            mSigner = bsc.getSigner(aCert, aSigningAlg, aParams);
        }
        return mSigner;
    }

    public synchronized X509Certificate getSignatureCertificateBase(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {

        if (mSignatureCert == null) {
            List<byte[]> allCerts = bsc.getSignatureCertificates();
            mSignatureCert = selectCertificate(checkIsQualified, checkBeingNonQualified, allCerts);
        }

        return mSignatureCert;
    }

    public synchronized X509Certificate getEncryptionCertificateBase(boolean checkIsQualified, boolean checkBeingNonQualified) throws ESYAException {
        if (mEncryptionCert == null) {
            List<byte[]> allCerts = bsc.getEncryptionCertificates();
            mEncryptionCert = selectCertificate(checkIsQualified, checkBeingNonQualified, allCerts);
        }

        return mEncryptionCert;
    }

    public String getSelectedSerialNumber() {
        return mSerialNumber;
    }

    public int getSlotCount() {
        return mSlotCount;
    }

    private X509Certificate selectCertificate(boolean checkIsQualified, boolean checkBeingNonQualified, List<byte[]> aCerts) throws ESYAException {

        if (aCerts != null && aCerts.isEmpty())
            throw new ESYAException("No certificate in smartcard");

        if (checkIsQualified && checkBeingNonQualified)
            throw new ESYAException("A certificate is either qualified or not, cannot be both");

        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X.509");
            String qcStatement = "1.3.6.1.5.5.7.1.3";
            for (byte[] bs : aCerts) {

                X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bs));

                if (!checkIsDateValid(cert))
                    continue;

                if (checkIsQualified) {
                    if (cert.getExtensionValue(qcStatement) != null)
                        certs.add(cert);
                } else if (checkBeingNonQualified) {
                    if (cert.getExtensionValue(qcStatement) == null)
                        certs.add(cert);
                } else {
                    certs.add(cert);
                }
            }
        } catch (CertificateException e) {
            throw new ESYAException(e);
        }

        X509Certificate selectedCert = null;

        if (certs.size() == 0) {
            if (checkIsQualified)
                throw new ESYAException("No qualified certificate in smartcard");
            else if (checkBeingNonQualified)
                throw new ESYAException("No non-qualified certificate in smartcard");

            throw new ESYAException("No certificate in smartcard");
        } else if (certs.size() == 1) {
            selectedCert = certs.get(0);
        } else {
            String[] optionList = new String[certs.size()];
            for (int i = 0; i < certs.size(); i++) {
                optionList[i] = certs.get(i).getSubjectDN().getName() + " " + StringUtil.toHexString(certs.get(i).getSerialNumber().toByteArray());
            }

            int result = askOption(null, null, optionList, "Certificate List", new String[]{"OK"});

            if (result < 0)
                selectedCert = null;
            else
                selectedCert = certs.get(result);
        }
        return selectedCert;
    }

    private boolean checkIsDateValid(X509Certificate cert) {
        Date certStartTime = cert.getNotBefore();
        Date certEndTime = cert.getNotAfter();
        Date now = Calendar.getInstance().getTime();

        return now.after(certStartTime) && now.before(certEndTime);
    }

    private int askOption(Component aParent, Icon aIcon, String[] aSecenekList, String aBaslik, String[] aOptions) {
        JComboBox combo = new JComboBox(aSecenekList);

        int cevap = JOptionPane.showOptionDialog(aParent, combo,
                aBaslik,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, aOptions, aOptions[0]);

        if (cevap == 1)
            return -1;
        return combo.getSelectedIndex();
    }
}
