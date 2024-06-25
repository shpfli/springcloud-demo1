package pers.hubery.filecomponent;

public class MyCOSClientTest {

    public static void main(String[] args) throws Exception {

        COSFileService client = new COSFileService();

        client.uploadFile("src/test/resources/to-upload.data", null, "20250605.data");
        client.downloadFile(null, "20250605.data", "src/test/resources/downloads/20250605.data");

    }

}
