####### Why to use Multipart uploads
https://medium.com/@pra4mesh/uploading-inputstream-to-aws-s3-using-multipart-upload-java-add81b57964e

``` java

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.Constants;
import com.amazonaws.services.s3.model.*;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for saving InputStream to S3 Bucket without Content-Length using Multipart Upload.
 * <p>
 * Links:
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/mpuoverview.html
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/qfacts.html
 * <p>
 * Example:
 * https://docs.aws.amazon.com/AmazonS3/latest/dev/llJavaUploadFile.html
 *
 * @author pramesh-bhattarai
 */
@Log4j2
public class S3MultipartUpload {

    private static final int AWAIT_TIME = 2; // in seconds
    private static final int DEFAULT_THREAD_COUNT = 4;

    private final String destBucketName;
    private final String filename;

    private final ThreadPoolExecutor executorService;
    private final AmazonS3 s3Client;

    private String uploadId;

    // uploadPartId should be between 1 to 10000 inclusively
    private final AtomicInteger uploadPartId = new AtomicInteger(0);

    private final List<Future<PartETag>> futuresPartETags = new ArrayList<>();

    public S3MultipartUpload(String destBucketName, String filename, AmazonS3 s3Client) {
        this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        this.destBucketName = destBucketName;
        this.filename = filename;
        this.s3Client = s3Client;
    }

    /**
     * We need to call initialize upload method before calling any upload part.
     */
    public boolean initializeUpload() {

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(destBucketName, filename);
        initRequest.setObjectMetadata(getObjectMetadata()); // if we want to set object metadata in S3 bucket
        initRequest.setTagging(getObjectTagging()); // if we want to set object tags in S3 bucket

        uploadId = s3Client.initiateMultipartUpload(initRequest).getUploadId();

        return false;
    }

    public void uploadPartAsync(ByteArrayInputStream inputStream) {
        submitTaskForUploading(inputStream, false);
    }

    public void uploadFinalPartAsync(ByteArrayInputStream inputStream) {
        try {
            submitTaskForUploading(inputStream, true);

            // wait and get all PartETags from ExecutorService and submit it in CompleteMultipartUploadRequest
            List<PartETag> partETags = new ArrayList<>();
            for (Future<PartETag> partETagFuture : futuresPartETags) {
                partETags.add(partETagFuture.get());
            }

            // Complete the multipart upload
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(destBucketName, filename, uploadId, partETags);
            s3Client.completeMultipartUpload(completeRequest);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // finally close the executor service
            this.shutdownAndAwaitTermination();
        }

    }

    /**
     * This method is used to shutdown executor service
     */
    private void shutdownAndAwaitTermination() {
        log.debug("executor service await and shutdown");
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(AWAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.debug("Interrupted while awaiting ThreadPoolExecutor to shutdown");
        }
        this.executorService.shutdownNow();
    }

    private void submitTaskForUploading(ByteArrayInputStream inputStream, boolean isFinalPart) {
        if (uploadId == null || uploadId.isEmpty()) {
            throw new IllegalStateException("Initial Multipart Upload Request has not been set.");
        }

        if (destBucketName == null || destBucketName.isEmpty()) {
            throw new IllegalStateException("Destination bucket has not been set.");
        }

        if (filename == null || filename.isEmpty()) {
            throw new IllegalStateException("Uploading file name has not been set.");
        }

        submitTaskToExecutorService(() -> {
            int eachPartId = uploadPartId.incrementAndGet();
            UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(destBucketName)
                    .withKey(filename)
                    .withUploadId(uploadId)
                    .withPartNumber(eachPartId) // partNumber should be between 1 and 10000 inclusively
                    .withPartSize(inputStream.available())
                    .withInputStream(inputStream);

            if (isFinalPart) {
                uploadRequest.withLastPart(true);
            }

            log.info(String.format("Submitting uploadPartId: %d of partSize: %d", eachPartId, inputStream.available()));

            UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);

            log.info(String.format("Successfully submitted uploadPartId: %d", eachPartId));
            return uploadResult.getPartETag();
        });
    }

    private void submitTaskToExecutorService(Callable<PartETag> callable) {
        // we are submitting each part in executor service and it does not matter which part gets upload first
        // because in each part we have assigned PartNumber from "uploadPartId.incrementAndGet()"
        // and S3 will accumulate file by using PartNumber order after CompleteMultipartUploadRequest
        Future<PartETag> partETagFuture = this.executorService.submit(callable);
        this.futuresPartETags.add(partETagFuture);
    }

    private ObjectTagging getObjectTagging() {
        // create tags list for uploading file
        return new ObjectTagging(new ArrayList<>());
    }

    private ObjectMetadata getObjectMetadata() {
        // create metadata for uploading file
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("application/zip");
        return objectMetadata;
    }

    public static void main(String... args) {

        final int UPLOAD_PART_SIZE = 10 * Constants.MB; // Part Size should not be less than 5 MB while using MultipartUpload
        final String destBucketName = "_destination_bucket_name_";
        final String filename = "_filename_";

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        S3MultipartUpload multipartUpload = new S3MultipartUpload(destBucketName, filename, s3Client);
        multipartUpload.initializeUpload();

        URL url = null;
        HttpURLConnection connection = null;

        try {

            url = new URL("_remote_url_of_uploading_file_");

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            int bytesRead, bytesAdded = 0;
            byte[] data = new byte[UPLOAD_PART_SIZE];
            ByteArrayOutputStream bufferOutputStream = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {

                bufferOutputStream.write(data, 0, bytesRead);

                if (bytesAdded < UPLOAD_PART_SIZE) {
                    // continue writing to same output stream unless it's size gets more than UPLOAD_PART_SIZE
                    bytesAdded += bytesRead;
                    continue;
                }
                multipartUpload.uploadPartAsync(new ByteArrayInputStream(bufferOutputStream.toByteArray()));
                bufferOutputStream.reset(); // flush the bufferOutputStream
                bytesAdded = 0; // reset the bytes added to 0
            }

            // upload remaining part of output stream as final part
            // bufferOutputStream size can be less than 5 MB as it is the last part of upload
            multipartUpload.uploadFinalPartAsync(new ByteArrayInputStream(bufferOutputStream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

```


