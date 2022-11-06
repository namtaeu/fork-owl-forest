package com.cbu.backend.studyGroup.entity;

import com.cbu.backend.member.entity.Member;
import com.cbu.backend.studyJournal.entity.StudyJournal;
import com.cbu.backend.studyPlan.entity.StudyPlan;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "studyGroup")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyGroup {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String summary;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private Member teamLeader;

    @OneToMany(mappedBy = "member")
    private List<Member> teamMembers = new ArrayList<>();

    @OneToOne(mappedBy = "plan", fetch = FetchType.LAZY)
    private StudyPlan studyPlan;

    @OneToMany(mappedBy = "studyJournal")
    private List<StudyJournal> journals = new ArrayList<>();

    private Integer like = 0;

    private Integer season;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.parse(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.parse(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

    @Builder
    public StudyGroup(Long id, String name, String summary, Member teamLeader, List<Member> teamMembers,
                      StudyPlan studyPlan, List<StudyJournal> journals, Integer like, Integer season) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.teamLeader = teamLeader;
        this.teamMembers = teamMembers;
        this.studyPlan = studyPlan;
        this.journals = journals;
        this.like = like;
        this.season = season;
    }

    public StudyGroup update(StudyGroup studyGroup) { // 매개변수 request DTO로 수정 필요
        this.name = studyGroup.getName();
        this.summary = studyGroup.getSummary();
        this.teamLeader = studyGroup.getTeamLeader();
        this.teamMembers = studyGroup.getTeamMembers();

        return this;
    }

    public boolean isMemberDuplicated(StudyGroup studyGroup) {
        return studyGroup.getTeamMembers()
                .stream()
                .distinct()
                .mapToInt(i -> 1)
                .sum() == studyGroup.getTeamMembers().size();
    }

    public void updateLike(Integer like) { // like는 -1(좋아요 취소) or +1(좋아요)
        if(isPositive(like)){
            this.like += like;
        }
    }

    private boolean isPositive(Integer like) {
        return this.like + like >= 0;
    }

}