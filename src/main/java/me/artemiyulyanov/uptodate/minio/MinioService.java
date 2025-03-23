package me.artemiyulyanov.uptodate.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class MinioService {
    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private String bucket;

    @PostConstruct
    public void init() {
        if (!amazonS3.doesBucketExistV2(bucket)) {
            amazonS3.createBucket(bucket);
        }
    }

    public String uploadFile(String objectKey, MultipartFile file) {
        if(amazonS3.doesObjectExist(bucket, objectKey)) deleteFile(objectKey);

        try (InputStream inputStream = file.getInputStream()) {
            long contentLength = file.getSize();
            String contentType = file.getContentType();

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucket,
                    objectKey,
                    inputStream,
                    new ObjectMetadata()
            );

            putObjectRequest.getMetadata().setContentType(contentType);
            putObjectRequest.getMetadata().setContentLength(contentLength);

            amazonS3.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3.getUrl(bucket, objectKey).toString().replace("http://minio:9000", "http://localhost:9000");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean fileExists(String objectKey) {
        return amazonS3.doesObjectExist(bucket, objectKey);
    }

    public boolean folderExists(String prefix) {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(bucket)
                .withPrefix(prefix)
                .withMaxKeys(1);

        ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
        return !objectListing.getObjectSummaries().isEmpty();
    }

    public void deleteFile(String objectKey) {
        if (amazonS3.doesObjectExist(bucket, objectKey)) amazonS3.deleteObject(new DeleteObjectRequest(bucket, objectKey));
    }

    public void deleteFolder(String prefix) {
        List<DeleteObjectsRequest.KeyVersion> files = getFolder(prefix)
                .stream()
                .map(DeleteObjectsRequest.KeyVersion::new)
                .toList();

        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucket)
                .withKeys(files)
                .withQuiet(true);
        amazonS3.deleteObjects(deleteRequest);
    }

    public MinioMediaFile getMediaFile(String objectKey) {
        try {
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, objectKey));

            return MinioMediaFile
                    .builder()
                    .inputStream(s3Object.getObjectContent())
                    .objectKey(objectKey)
                    .build();
        } catch (AmazonS3Exception e) {
            return null;
        }
    }

    public List<String> getFolder(String prefix) {
        ListObjectsV2Result result = amazonS3.listObjectsV2(
                new ListObjectsV2Request()
                        .withBucketName(bucket)
                        .withPrefix(prefix)
        );

        return result.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

//    public void saveArticleResources(Article article, List<MultipartFile> resources) {
//        resources.forEach(file -> uploadFile(getFolder(article) + File.separator + file.getOriginalFilename(), file));
//    }
//
//    public void saveArticleCommentResources(ArticleComment comment, List<MultipartFile> resources) {
//        resources.forEach(file -> uploadFile(getFolder(comment) + File.separator + file.getOriginalFilename(), file));
//    }
//
//    public void saveUserIcon(User user, MultipartFile iconFile) {
//        String objectKey = getFolder(user) + File.separator + iconFile.getOriginalFilename();
//        uploadFile(objectKey, iconFile);
//    }
//
//    public void deleteArticleResources(Article article) {
//        String resourcesFolder = getFolder(article);
//        System.out.println("The folder " + resourcesFolder + ": " + amazonS3.doesObjectExist(bucket, resourcesFolder));
//        if (amazonS3.doesObjectExist(bucket, resourcesFolder)) deleteFile(resourcesFolder);
//    }
//
//    public void deleteArticleCommentResources(ArticleComment comment) {
//        String resourcesFolder = getFolder(comment);
//        if (amazonS3.doesObjectExist(bucket, resourcesFolder)) deleteFile(resourcesFolder);
//    }
//
//    public void deleteUserIcon(User user) {
//        String objectKey = getFolder(user) + File.separator + user.getIcon();
//        if (amazonS3.doesObjectExist(bucket, objectKey)) deleteFile(objectKey);
//    }

//    public List<String> getArticleResources(Article article) {
//        return getResources(getFolder(article));
//    }
//
//    public List<String> getArticleCommentResources(ArticleComment comment) {
//        return getResources(getFolder(comment));
//    }
//
//    public List<String> getUserResources(User user) {
//        return getResources(getFolder(user));
//    }
//
//    private String getFolder(Article article) {
//        return String.format(ARTICLE_RESOURCES_FOLDER, article.getId());
//    }
//
//    private String getFolder(ArticleComment comment) {
//        return String.format(ARTICLE_COMMENT_RESOURCES_FOLDER, comment.getArticle().getId(), comment.getId());
//    }
//
//    private String getFolder(User user) {
//        return String.format(USER_RESOURCES_FOLDER, user.getId());
//    }
}