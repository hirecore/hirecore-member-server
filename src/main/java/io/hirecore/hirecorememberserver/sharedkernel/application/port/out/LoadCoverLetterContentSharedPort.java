package io.hirecore.hirecorememberserver.sharedkernel.application.port.out;

import io.hirecore.hirecorememberserver.sharedkernel.domain.vo.Visibility;

import java.util.Optional;

public interface LoadCoverLetterContentSharedPort {

    /**
     * 식별자로 자기소개서의 메타와 본문을 한 번에 적재한다.
     * 자원이 존재하지 않거나 삭제된 경우 {@link Optional#empty()} 를 반환한다.
     */
    Optional<Result> findById(Long coverLetterId);

    /**
     * 다른 BC 가 자기소개서의 본문/메타데이터를 한 번에 합성하기 위해 받는 결과.
     *
     * <p>{@code memberAccountId} 와 {@code visibility} 를 함께 노출해 소비자가
     * "자원 자체의 가시성 + viewer 일치 여부" 기반으로 본문 노출 정책을 적용할 수 있도록 한다.</p>
     */
    record Result(
            Long id,
            String title,
            Long memberAccountId,
            Visibility visibility,
            String contentJson,
            String contentHtml
    ) {
    }
}
