package com.petmatz.api.user.request;

import com.petmatz.domain.user.constant.Gender;
import com.petmatz.domain.user.constant.PreferredSize;
import com.petmatz.domain.user.info.EditKakaoProfileInfo;
import com.petmatz.domain.user.info.EditMyProfileInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EditKakaoProfileRequestDto {
    private String nickname;

    private List<PreferredSize> preferredSizes;

    private String introduction;

    private boolean isCareAvailable;

    private String profileImg;

    private Gender gender;

    private String mbti;

    private Double latitude;

    private Double longitude;


    public static EditKakaoProfileInfo of(EditKakaoProfileRequestDto reqDto) {
        return EditKakaoProfileInfo.builder()
                .nickname(reqDto.getNickname())
                .preferredSizes(reqDto.getPreferredSizes())
                .introduction(reqDto.getIntroduction())
                .isCareAvailable(reqDto.isCareAvailable())
                .profileImg(reqDto.getProfileImg())
                .gender(reqDto.gender)
                .mbti(reqDto.getMbti())
                .latitude(reqDto.getLatitude())
                .longitude(reqDto.getLongitude())
                .build();
    }
}
