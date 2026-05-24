package io.hirecore.hirecorememberserver.modules.profile.application.usecase;

import io.hirecore.hirecorememberserver.modules.profile.application.port.in.LoadProfileNicknameUseCase;
import io.hirecore.hirecorememberserver.modules.profile.application.port.out.LoadProfileNicknamePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoadProfileNicknameUseCaseImpl implements LoadProfileNicknameUseCase {

    private final LoadProfileNicknamePort loadProfileNicknamePort;

    /*
     * todo: nickname을 불러오지 못했을 때 오류를 발생시키는 것이 맞는가?
     *  1. 사용자가 포트폴리오를 조회하는 상황에
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<String> execute(Long accountId) {
        return loadProfileNicknamePort.findNickname(accountId);
    }
}
