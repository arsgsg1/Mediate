package com.ko.mediate.HC.homework.application;

import com.ko.mediate.HC.common.exception.MediateNotFoundException;
import com.ko.mediate.HC.homework.application.request.CreateHomeworkDto;
import com.ko.mediate.HC.homework.application.request.UpdateHomeworkDto;
import com.ko.mediate.HC.homework.domain.Homework;
import com.ko.mediate.HC.homework.domain.HomeworkContent;
import com.ko.mediate.HC.homework.infra.JpaHomeworkRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeworkCommandExecutor {
  private final JpaHomeworkRepository homeworkRepository;

  @Transactional
  public void createHomework(CreateHomeworkDto dto) {
    Homework homework = new Homework(dto.getTutorId(), dto.getTuteeId(), dto.getTitle(), false);
    dto.getContents().stream().forEach(c -> homework.giveHomework(c.getContent(), c.isCompleted()));
    homeworkRepository.save(homework);
  }

  @Transactional
  public void modifyHomework(Long homeworkId, UpdateHomeworkDto dto) {
    Homework homework =
        homeworkRepository
            .findById(homeworkId)
            .orElseThrow(() -> new MediateNotFoundException("ID가 없습니다."));
    homework.changeHomework(
        dto.getTitle(),
        dto.getContents().stream()
            .map(c -> new HomeworkContent(c.getContent(), c.isCompleted()))
            .collect(Collectors.toList()));
    homeworkRepository.save(homework);
  }

  @Transactional
  public void deleteHomework(long homeworkId) {
    homeworkRepository.deleteById(homeworkId);
  }
}
