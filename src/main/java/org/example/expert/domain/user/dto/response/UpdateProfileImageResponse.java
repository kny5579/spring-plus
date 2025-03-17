package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UpdateProfileImageResponse {

    private final Long userId;
    private final String profileImage;

    public UpdateProfileImageResponse(Long userId, String profileImage) {
        this.userId = userId;
        this.profileImage = profileImage;
    }
}
