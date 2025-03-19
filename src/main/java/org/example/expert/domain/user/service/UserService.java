package org.example.expert.domain.user.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UpdateProfileImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = getUserById(userId);
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    public List<UserResponse> getUsers(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        User user = getUserById(userId);

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    @Transactional
    public UpdateProfileImageResponse updateProfileImage(long userId, MultipartFile profileImageFile) throws IOException {
        User user = getUserById(userId);

        String profileImageFileUrl = uploadImageToS3(profileImageFile);

        user.updateProfileImage(profileImageFileUrl);

        userRepository.save(user);

        return new UpdateProfileImageResponse(userId,profileImageFileUrl);
    }

    @Transactional
    public void deleteProfileImage(long userId) throws MalformedURLException, UnsupportedEncodingException {
        User user = getUserById(userId);

        String profileImageUrl = user.getProfileImage();

        // s3에 저장된 이미지 삭제
        String key = getKeyFromProfileImageUrl(profileImageUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName,key));

        user.deleteProfileImage();

        userRepository.save(user);
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명
        String s3FileName = "image/"+UUID.randomUUID()+"_" + originalFilename; //변경된 파일 명

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());
        InputStream inputStream = image.getInputStream();
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, s3FileName, inputStream, metadata));
        } catch (AmazonServiceException e) {
            log.error("AWS S3 서비스 오류: {}", e.getErrorMessage());
            throw new RuntimeException("S3 서비스 오류", e);
        } catch (SdkClientException e) {
            log.error("AWS S3 클라이언트 오류: {}", e.getMessage());
            throw new RuntimeException("S3 클라이언트 오류", e);
        }
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    private String getKeyFromProfileImageUrl(String imageUrl) throws MalformedURLException, UnsupportedEncodingException {
        URL url = new URL(imageUrl);
        String decodingKey = URLDecoder.decode(url.getPath(),"UTF-8");
        return decodingKey.substring(1);
    }

}
