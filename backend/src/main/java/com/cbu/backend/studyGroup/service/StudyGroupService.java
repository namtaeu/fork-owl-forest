package com.cbu.backend.studyGroup.service;

import com.cbu.backend.studyGroup.dto.request.CreateStudyGroupRequestDTO;
import com.cbu.backend.studyGroup.dto.response.StudyGroupResponseDTO;
import com.cbu.backend.studyGroup.entity.StudyGroup;
import com.cbu.backend.studyGroup.mapper.StudyGroupMapper;
import com.cbu.backend.studyGroup.repository.StudyGroupRepository;
import com.cbu.backend.studyJournal.service.StudyJournalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMapper studyGroupMapper;
    private final StudyJournalService studyJournalService;

    public Long registerStudyGroup(CreateStudyGroupRequestDTO createStudyGroupRequestDTO) { // 스터디 등록
        StudyGroup studyGroup = studyGroupMapper.toEntity(createStudyGroupRequestDTO);
        if(studyGroup.isMemberDuplicated(studyGroup)){ // 중복 스터디원 존재 여부
            throw new RuntimeException(); // Exception 수정 필요 (생성 후 적용)
        }
        studyGroupRepository.save(studyGroup);
        return studyGroup.getId();
    }

    public StudyGroupResponseDTO searchById(Long id) { // 스터디 ID로 조회
        Optional<StudyGroup> studyGroup = studyGroupRepository.findById(id);
        if(!studyGroup.isPresent()) { // 조회 실패
            throw new EntityNotFoundException();
        }
        return studyGroupMapper.toResponseDTO(studyGroup.get());
    }

    public void updateLike(Long studyGroupId, Integer like) { // 스터디 좋아요 업데이트(Integer like는 -1 or +1)
        studyGroupRepository
                .findById(studyGroupId)
                .ifPresent(studyGroup -> studyGroup.updateLike(like));
    }

    public List<StudyGroupResponseDTO> searchStudyOrderByCreatedAt() { // 스터디 일지 최신순 조회
        return sort(studyJournalService::searchLatestJournalCreatedAt);
    }

    public List<StudyGroupResponseDTO> searchStudyOrderByJournalsNum() { // 스터디 일지 개수순 조회
        return sort(studyGroup -> studyGroup.getJournals().size());
    }

    public List<StudyGroupResponseDTO> searchStudyOrderByLike() { // 스터디 좋아요순(많은 순서) 조회
        return sort(StudyGroup::getLike);
    }

    private List<StudyGroupResponseDTO> sort(Function<StudyGroup, Comparable> function) {
        return studyGroupRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(function, Comparator.reverseOrder())
                .thenComparing(StudyGroup::getSeason, Comparator.reverseOrder()))
                .map(studyGroupMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

}