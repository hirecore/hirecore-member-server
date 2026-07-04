package io.hirecore.hirecorememberserver.modules.portfolio.domain.vo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 참조 이미지 집합 VO (썸네일 ∪ 본문, 순서 보존/중복 제거, 차집합 계산)
public final class ReferencedImageIds {

    private final List<Long> values;

    private ReferencedImageIds(List<Long> values) {
        this.values = values;
    }

    public static ReferencedImageIds of(Long thumbnailImageId, List<Long> contentImageIds) {
        List<Long> collected = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        if (thumbnailImageId != null && seen.add(thumbnailImageId)) {
            collected.add(thumbnailImageId);
        }
        if (contentImageIds != null) {
            for (Long imageId : contentImageIds) {
                if (imageId != null && seen.add(imageId)) {
                    collected.add(imageId);
                }
            }
        }
        return new ReferencedImageIds(List.copyOf(collected));
    }

    // 차집합: this에서 other에 없는 것 = 더 이상 참조 안 되는 이미지 (순서 보존)
    public List<Long> minus(ReferencedImageIds other) {
        Set<Long> exclude = new HashSet<>(other.values);
        List<Long> released = new ArrayList<>();
        for (Long id : this.values) {
            if (!exclude.contains(id)) {
                released.add(id);
            }
        }
        return released;
    }

    public List<Long> values() {
        return values;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
