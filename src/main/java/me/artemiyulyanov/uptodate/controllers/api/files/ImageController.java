package me.artemiyulyanov.uptodate.controllers.api.files;

import com.amazonaws.services.s3.AmazonS3;
import me.artemiyulyanov.uptodate.controllers.AuthenticatedController;
import me.artemiyulyanov.uptodate.controllers.api.files.responses.FileUploadResponse;
import me.artemiyulyanov.uptodate.minio.MinioMediaFile;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.web.RequestService;
import me.artemiyulyanov.uptodate.web.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class ImageController {
    @Autowired
    private MinioService minioService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AmazonS3 amazonS3;

    @GetMapping("/get")
    public ResponseEntity<?> getImage(@RequestParam String path, Model model) {
        String url = amazonS3.getUrl("photos", "articles/1/icon.png").toString();
        return requestService.executeEntityResponse(HttpStatus.OK, "", url);
//        MinioMediaFile mediaFile = minioService.getMediaFile(path);
//
//        try (InputStream inputStream = mediaFile.getInputStream()) {
//            return requestService.executeMediaResponse(HttpStatus.OK, mediaFile.getMediaType(), inputStream.readAllBytes());
//        } catch (Exception e) {
//            return requestService.executeApiResponse(HttpStatus.BAD_REQUEST, "Unable to return image!");
//        }
    }
}