package vn.vissoft.dashboard.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.vissoft.dashboard.controller.VttPositionController;

import java.io.*;

public class DownloadFileFromSystem {

    private static final Logger LOGGER = LogManager.getLogger(DownloadFileFromSystem.class);

    /**
     * download file tu he thong
     *
     * @param pstrPath
     * @param pstrFileName
     * @return stream file
     * @throws IOException
     * @author DatNT
     * @version 1.0
     * @since 2019/09/13
     */
    public ByteArrayInputStream downloadFile(String pstrPath, String pstrFileName) throws IOException {
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(pstrPath + File.separator + pstrFileName);
            out = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            byte[] buffer = new byte[4096];

            int readNum;
            while ((readNum = in.read(buffer)) > 0) {
                out.write(buffer, 0, readNum); //no doubt here is 0
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
