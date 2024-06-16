package pers.hubery.filecomponent.listener;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OssFileUploadProgressListener implements ProgressListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(OssFileUploadProgressListener.class);

    private long bytesWritten = 0;
    private long totalBytes = -1;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Start to upload......");
                }
                break;
            case REQUEST_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(this.totalBytes + " bytes in total will be upload to OSS");
                }
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(bytes + " bytes have been written at this time, upload progress: " + percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")");
                    }
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(bytes + " bytes have been written at this time, upload ratio: unknown" + "(" + this.bytesWritten + "/...)");
                    }
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Succeed to upload, " + this.bytesWritten + " bytes have been transferred in total");
                }
                break;
            case TRANSFER_FAILED_EVENT:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Failed to upload, " + this.bytesWritten + " bytes have been transferred");
                }
                break;
            default:
                break;
        }
    }
}