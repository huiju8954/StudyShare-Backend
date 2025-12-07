// package com.school.StudyShare.community.repository;

package com.school.StudyShare.community.repository;

import com.school.StudyShare.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 💡 [수정] 최신순으로 모든 게시글 조회
    List<Community> findAllByOrderByCreateDateDesc();

    // 💡 [수정] 유저 ID로 모든 게시글 찾기
    List<Community> findByUserId(Integer userId);

    // 💡 [수정] 카테고리별로 모든 게시글 찾기
    List<Community> findByCategory(String category);
}