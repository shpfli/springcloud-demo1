package pers.hubery.filecomponent.util;

import com.qcloud.cos.utils.CRC64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CRC64Util {

    public static long calculateCRC64(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            CRC64 crc64 = new CRC64();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                crc64.update(buffer, 0, bytesRead);
            }
            return crc64.getValue();
        }
    }
}