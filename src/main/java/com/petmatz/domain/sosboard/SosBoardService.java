package com.petmatz.domain.sosboard;

import com.petmatz.api.pet.dto.PetResponse;
import com.petmatz.domain.pet.entity.Pet;
import com.petmatz.domain.pet.repository.PetRepository;
import com.petmatz.domain.sosboard.dto.PageResponseDto;
import com.petmatz.domain.sosboard.dto.SosBoardPetDto;
import com.petmatz.domain.sosboard.dto.SosBoardServiceDto;
import com.petmatz.domain.sosboard.exception.SosBoardErrorCode;
import com.petmatz.domain.sosboard.exception.SosBoardServiceException;
import com.petmatz.domain.user.entity.User;
import com.petmatz.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SosBoardService {

    private final SosBoardRepository sosBoardRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    // 전체 조회 (지역 필터링 + 인덱스 기반 페이지네이션)
    public PageResponseDto<SosBoardServiceDto> getAllSosBoards(String region, int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<SosBoard> sosBoardPage;
        if (region == null || region.isEmpty()) {
            sosBoardPage = sosBoardRepository.findAll(pageable);
        } else {
            sosBoardPage = sosBoardRepository.findByUserRegion(region, pageable);
        }

        // SosBoard → SosBoardServiceDto 변환
        List<SosBoardServiceDto> serviceDtos = sosBoardPage.getContent().stream()
                .map(sosBoard -> {
                    List<PetResponse> petResponses = sosBoard.getPetSosBoards().stream()
                            .map(PetSosBoard::getPet)
                            .map(PetResponse::of)
                            .collect(Collectors.toList());
                    return SosBoardServiceDto.from(sosBoard, petResponses);
                })
                .collect(Collectors.toList());

        // 페이지 응답 생성
        return new PageResponseDto<>(
                serviceDtos,
                sosBoardPage.getTotalElements(),
                sosBoardPage.getTotalPages(),
                pageNum + 1
        );
    }

    // 게시글 작성
    public SosBoardServiceDto createSosBoard(SosBoardServiceDto serviceDto) {
        User user = userRepository.findById(serviceDto.userId())
                .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.USER_NOT_FOUND));

        SosBoard sosBoard = serviceDto.toEntity(user);

        List<PetSosBoard> petSosBoards = serviceDto.petIds().stream()
                .map(petId -> {
                    Pet pet = petRepository.findById(petId)
                            .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.PET_NOT_FOUND));
                    return PetSosBoard.builder()
                            .sosBoard(sosBoard)
                            .pet(pet)
                            .build();
                })
                .collect(Collectors.toList());

        sosBoard.addPetSosBoards(petSosBoards);

        SosBoard savedBoard = sosBoardRepository.save(sosBoard);
        // ServiceDto를 생성할 때 PetResponse를 포함
        List<PetResponse> petResponses = petSosBoards.stream()
                .map(PetSosBoard::getPet)
                .map(PetResponse::of) // Pet → PetResponse 변환
                .collect(Collectors.toList());

        return SosBoardServiceDto.from(savedBoard, petResponses);
    }


    // 특정 게시물 조회
    public SosBoardServiceDto getSosBoardById(Long id) {
        SosBoard sosBoard = sosBoardRepository.findById(id)
                .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.BOARD_NOT_FOUND));

        // PetResponse 리스트 생성
        List<PetResponse> petResponses = sosBoard.getPetSosBoards().stream()
                .map(PetSosBoard::getPet)
                .map(PetResponse::of) // Pet → PetResponse 변환
                .collect(Collectors.toList());

        // SosBoardServiceDto 반환
        return SosBoardServiceDto.from(sosBoard, petResponses);
    }

        // 펫 정보 업데이트
        public SosBoardServiceDto updateSosBoard(Long id, SosBoardServiceDto serviceDto, User user) {
            // 기존 게시글 조회
            SosBoard existingSosBoard = sosBoardRepository.findById(id)
                    .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.BOARD_NOT_FOUND));

            // 사용자 권한 확인
            if (!existingSosBoard.getUser().getId().equals(user.getId())) {
                throw new SosBoardServiceException(SosBoardErrorCode.UNAUTHORIZED_ACCESS);
            }

            // 기존 PetSosBoard 관계 삭제
            existingSosBoard.clearPetSosBoards();

            // 요청된 petIds 기반으로 새로운 PetSosBoard 설정
            List<PetSosBoard> updatedPetSosBoards = serviceDto.petIds().stream()
                    .map(petId -> {
                        Pet pet = petRepository.findById(petId)
                                .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.PET_NOT_FOUND));
                        return PetSosBoard.builder()
                                .sosBoard(existingSosBoard)
                                .pet(pet)
                                .build();
                    })
                    .collect(Collectors.toList());
            existingSosBoard.addPetSosBoards(updatedPetSosBoards);

            // 게시글의 기타 필드 업데이트
            existingSosBoard.updateFields(
                    serviceDto.title(),
                    serviceDto.paymentType(),
                    serviceDto.price(),
                    serviceDto.comment(),
                    serviceDto.startDate(),
                    serviceDto.endDate()
            );

            // 저장 후 업데이트된 데이터 반환
            SosBoard savedBoard = sosBoardRepository.save(existingSosBoard);
            List<PetResponse> petResponses = updatedPetSosBoards.stream()
                    .map(PetSosBoard::getPet)
                    .map(PetResponse::of)
                    .collect(Collectors.toList());

            return SosBoardServiceDto.from(savedBoard, petResponses);
        }

        //게시글 삭제
    public void deleteSosBoard(Long id, User user) {
        SosBoard sosBoard = sosBoardRepository.findById(id)
                .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.BOARD_NOT_FOUND));

        // 작성자 확인
        if (!sosBoard.getUser().getId().equals(user.getId())) {
            throw new SosBoardServiceException(SosBoardErrorCode.UNAUTHORIZED_ACCESS);
        }

        sosBoardRepository.delete(sosBoard);
    }

    // User의 Pet 정보 불러오기
    public List<PetResponse> getUserPets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SosBoardServiceException(SosBoardErrorCode.USER_NOT_FOUND));

        // Pet 엔티티를 DTO로 변환
        return petRepository.findByUserId(user.getId()).stream()
                .map(SosBoardPetDto::of) // Pet → SosBoardPetDto 변환
                .map(PetResponse::of)    // SosBoardPetDto → PetResponse 변환
                .collect(Collectors.toList());
    }

    // 해당 닉네임에 해당하는 글 불러오기
    public PageResponseDto<SosBoardServiceDto> getUserSosBoardsByNickname(String nickname, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<SosBoard> sosBoardPage = sosBoardRepository.findByUserNickname(nickname, pageable);

        // SosBoard → SosBoardServiceDto 변환
        List<SosBoardServiceDto> serviceDtos = sosBoardPage.getContent().stream()
                .map(sosBoard -> {
                    List<PetResponse> petResponses = sosBoard.getPetSosBoards().stream()
                            .map(PetSosBoard::getPet)
                            .map(PetResponse::of)
                            .collect(Collectors.toList());
                    return SosBoardServiceDto.from(sosBoard, petResponses);
                })
                .collect(Collectors.toList());

        // PageResponseDto 생성
        return new PageResponseDto<>(
                serviceDtos,
                sosBoardPage.getTotalElements(),
                sosBoardPage.getTotalPages(),
                page + 1
        );
    }
}



