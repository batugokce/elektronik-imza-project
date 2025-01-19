package dev.batugokce.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.gov.tubitak.uekae.esya.api.common.util.LicenseUtil;
import tr.gov.tubitak.uekae.esya.api.common.util.VersionUtil;

import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static dev.batugokce.constant.Constants.ROOT_DIR;

public class SampleBase {

    protected static Logger LOGGER = LoggerFactory.getLogger(SampleBase.class);

    static {

        try {
            URL resourceFolderUrl = SampleBase.class.getClassLoader().getResource("");

            if (resourceFolderUrl != null) {
                String resourceFolderPath = Paths.get(resourceFolderUrl.getPath()).toString();
                LOGGER.info("Resources folder full path: {}", resourceFolderPath);
                ROOT_DIR = resourceFolderPath;
            } else {
                LOGGER.error("Resources folder not found.");
            }

            LicenseUtil.setLicenseXml(SampleBase.class.getClassLoader().getResourceAsStream("lisans.xml"));

            Date expirationDate = LicenseUtil.getExpirationDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            LOGGER.info("License expiration date: {}", dateFormat.format(expirationDate));
            LOGGER.info("MA3 API version: {}", VersionUtil.getAPIVersion());
        } catch (Exception e) {
            LOGGER.error("Error in SampleBase", e);
        }
    }

}
