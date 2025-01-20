package dev.batugokce.sign;

import dev.batugokce.ImzaSonucu;
import dev.batugokce.base.CadesSampleBase;
import dev.batugokce.smartcardmanager.SmartCardManager;
import tr.gov.tubitak.uekae.esya.api.asn.x509.ECertificate;
import tr.gov.tubitak.uekae.esya.api.cmssignature.CMSSignatureException;
import tr.gov.tubitak.uekae.esya.api.cmssignature.ISignable;
import tr.gov.tubitak.uekae.esya.api.cmssignature.SignableByteArray;
import tr.gov.tubitak.uekae.esya.api.cmssignature.attribute.EParameters;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.BaseSignedData;
import tr.gov.tubitak.uekae.esya.api.cmssignature.signature.ESignatureType;
import tr.gov.tubitak.uekae.esya.api.common.crypto.BaseSigner;
import tr.gov.tubitak.uekae.esya.api.common.util.Base64;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import static dev.batugokce.constant.Constants.IS_QUALIFIED;


public class BesSign extends CadesSampleBase {

    public ImzaSonucu signCadesBes(String filepathToSign, String pin) throws Exception {
        byte[] imzalanacakIcerik = Files.readAllBytes(Paths.get(filepathToSign));
        BaseSignedData baseSignedData = new BaseSignedData();
        ISignable content = new SignableByteArray(imzalanacakIcerik);
        baseSignedData.addContent(content);

        HashMap<String, Object> params = createParameters();

        ECertificate cert = SmartCardManager.getInstance().getSignatureCertificate(IS_QUALIFIED);
        BaseSigner signer = SmartCardManager.getInstance().getSigner(pin, cert);
        String imzaciBilgileri = cert.getSubject().getCommonNameAttribute() + " - " + cert.getSubject().getSerialNumberAttribute();

        baseSignedData.addSigner(ESignatureType.TYPE_BES, cert, signer, null, params);

        SmartCardManager.getInstance().logout();

        byte[] imzaVerisi = baseSignedData.getEncoded();
        byte[] imzaliVeri = attachExternalContent(imzaVerisi, imzalanacakIcerik);

        String outputFilePath = "".concat(String.format("./BES-%s.txt", UUID.randomUUID()));
        Path path = Files.write(Paths.get(outputFilePath), Base64.encode(imzaliVeri).getBytes(StandardCharsets.UTF_8));

        LOGGER.info("İmzalama işlemi tamamlandı. İmza dosyası şu konumda oluşturuldu: {}", outputFilePath);
        return ImzaSonucu.builder().imzaliVeriDosyaYolu(path.toAbsolutePath().toString()).imzaciAdSoyad(imzaciBilgileri).build();
    }

    private HashMap<String, Object> createParameters() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(EParameters.P_VALIDATE_CERTIFICATE_BEFORE_SIGNING, false);
        //params.put(EParameters.P_CERT_VALIDATION_POLICY, getPolicy());
        return params;
    }

    private byte[] attachExternalContent(byte[] imza, byte[] icerik) throws CMSSignatureException {
        BaseSignedData bsd = new BaseSignedData(imza);
        bsd.attachExternalContent(new SignableByteArray(icerik));
        return bsd.getEncoded();
    }

}
