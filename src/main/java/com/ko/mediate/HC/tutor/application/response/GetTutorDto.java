package com.ko.mediate.HC.tutor.application.response;

import com.ko.mediate.HC.tutor.domain.Tutor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ApiModel(description = "튜터 정보")
@Builder
public class GetTutorDto {
  @ApiModelProperty(value = "튜터 이름")
  private String name;

  @ApiModelProperty(value = "학교 이름")
  private String school;

  @ApiModelProperty(value = "학과 이름")
  private String major;

  @ApiModelProperty(value = "학년")
  private String grade;

  @ApiModelProperty(value = "주소")
  private String address;

  public static GetTutorDto fromEntity(Tutor tutor) {
    return GetTutorDto.builder()
        .name(tutor.getAccount().getName())
        .school(tutor.getAcademicInfo().getSchool())
        .major(tutor.getAcademicInfo().getMajor())
        .grade(tutor.getAcademicInfo().getGrade())
        .address(tutor.getAddress())
        .build();
  }
}
