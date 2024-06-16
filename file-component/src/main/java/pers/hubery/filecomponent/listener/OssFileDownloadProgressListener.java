package pers.hubery.filecomponent.listener;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OssFileDownloadProgressListener implements ProgressListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OssFileDownloadProgressListener.class);

    private long bytesRead = 0;
    private long totalBytes = -1;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Start to download......");
                }

                break;
            case RESPONSE_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(this.totalBytes + " bytes in total will be downloaded to a local file");
                }
                break;
            case RESPONSE_BYTE_TRANSFER_EVENT:
                this.bytesRead += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesRead * 100.0 / this.totalBytes);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(bytes + " bytes have been read at this time, download progress: " +
                                percent + "%(" + this.bytesRead + "/" + this.totalBytes + ")");
                    }
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(bytes + " bytes have been read at this time, download ratio: unknown" +
                                "(" + this.bytesRead + "/...)");
                    }
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Succeed to download, " + this.bytesRead + " bytes have been transferred in total");
                }
                break;
            case TRANSFER_FAILED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Failed to download, " + this.bytesRead + " bytes have been transferred");
                }
                break;
            default:
                break;
        }
    }
}
