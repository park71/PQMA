package project.gym.member.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gym.member.dto.LockerDTO;
import project.gym.member.entity.LockHistoryEntity;
import project.gym.member.entity.LockerEntity;
import project.gym.member.entity.MemberEntity;
import project.gym.member.repository.LockHistoryRepository;
import project.gym.member.repository.LockerRepository;
import project.gym.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class LockerService {

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LockHistoryRepository lockHistoryRepository;

    @PostConstruct
    public void initLockers() {
        if (lockerRepository.count() == 0) {
            List<LockerEntity> lockers = IntStream.rangeClosed(1, 322)
                    .mapToObj(i -> new LockerEntity(i, false))
                    .collect(Collectors.toList());
            lockerRepository.saveAll(lockers);
        }
    }

    // ID로 락커를 찾는 메서드
    public LockerEntity findById(Long lockerId) {
        Optional<LockerEntity> optionalLocker = lockerRepository.findById(lockerId);
        return optionalLocker.orElse(null); // 존재하지 않을 경우 null 반환
    }
    public LockHistoryEntity findByIds(Long lockerId) {
        Optional<LockHistoryEntity> optionalLocker = lockHistoryRepository.findById(lockerId);
        return optionalLocker.orElse(null); // 존재하지 않을 경우 null 반환
    }

    // 락커 상태를 업데이트하고 저장하는 메서드
    public void save(LockerEntity locker) {
        lockerRepository.save(locker);
    }
    // 락커 상태를 업데이트하고 저장하는 메서드
    public void saves(LockHistoryEntity locker) {
        lockHistoryRepository.save(locker);
    }
    //public List<LockerDTO> getAvailableLockers() {
   //     return lockerRepository.findAll().stream()
   //             .map(locker -> new LockerDTO(locker.getLocknum(), locker.getIsOccupied()))
   //             .collect(Collectors.toList());
   // }

   /* @Transactional
    public void createMemberAndLocker(LockerDTO lockerDTO) {
        log.info("락카값 들어왔나: {}", lockerDTO.getLocker());

        // lockerDTO의 필드가 null인지 확인
        if (lockerDTO.getLocknum() == null || lockerDTO.getLocker() == null) {
            log.warn("Locker number or locker name is null: locknum={}, locker={}", lockerDTO.getLocknum(), lockerDTO.getLocker());
            return; // 예외를 던지지 않고 메서드 종료
        }

        // lockerDTO의 locknum이 데이터베이스에 존재하는지 확인
        List<LockerEntity> lockers = lockerRepository.findByLocknum(lockerDTO.getLocknum());

        if (lockers.isEmpty()) {
            // 데이터베이스에 locknum이 존재하지 않는 경우, 새로운 락커를 생성하고 저장
            LockerEntity newLocker = new LockerEntity();
            newLocker.setLocknum(lockerDTO.getLocknum());
            newLocker.setLocker(lockerDTO.getLocker());
            newLocker.setLockstart(lockerDTO.getLockstart());
            newLocker.setLockend(lockerDTO.getLockend());
            newLocker.setLockpass(lockerDTO.getLockpass());
            newLocker.setIsOccupied(false); // 새로 생성된 락커는 기본적으로 비어있음
            lockerRepository.save(newLocker);
        } else {
            // locknum이 존재하는 경우, 업데이트를 수행 (기존 데이터가 존재하는 경우)
            LockerEntity existingLocker = lockers.get(0);

            if (existingLocker.getIsOccupied()) {
                log.warn("Locker is already occupied: locknum={}", lockerDTO.getLocknum());
                return; // 락커가 이미 차있는 경우, 메서드 종료
            }

            // 락커 상태 업데이트
            existingLocker.setName(lockerDTO.getName());
            existingLocker.setLocker(lockerDTO.getLocker());
            existingLocker.setLockstart(lockerDTO.getLockstart());
            existingLocker.setLockend(lockerDTO.getLockend());
            existingLocker.setLockpass(lockerDTO.getLockpass());
            existingLocker.setIsOccupied(true); // 락커를 현재 사용중으로 설정
            lockerRepository.save(existingLocker);
        }
    }*/



    public List<LockerEntity> getAllLockers() {
        return lockerRepository.findAll();
    }
    public List<LockHistoryEntity> getAllLocker() {
        List<LockHistoryEntity> lockers = lockHistoryRepository.findAll();

        // 각 락커에 대해 count 값을 계산
        lockers.forEach(locker -> locker.setCount(locker.getCount()));

        return lockers;
    }
    public LockerDTO findByName(String name) {
        Optional<LockerEntity> lockerEntityOptional = lockerRepository.findByName(name);
        if (lockerEntityOptional.isPresent()) {
            LockerEntity lockerEntity = lockerEntityOptional.get();
            LockerDTO lockerDTO = new LockerDTO();
            lockerDTO.setLocker(lockerEntity.getLocker());
            lockerDTO.setLocknum(lockerEntity.getLocknum());

            // NullPointerException 방지 코드 추가
            Integer lockpass = lockerEntity.getLockpass();
            if (lockpass != null) {
                lockerDTO.setLockpass(lockpass);
            } else {
                lockerDTO.setLockpass(0); // 기본값 설정 또는 예외 처리
                log.warn("Lockpass is null for locker with name: " + name);
            }

            lockerDTO.setLockstart(lockerEntity.getLockstart());
            lockerDTO.setLockend(lockerEntity.getLockend());
            return lockerDTO;
        } else {
            log.info("Locker not found for name: " + name);
            return null;
        }
    }
//    public void collectLocker(Long lockerId) {
//        LockerEntity locker = lockerRepository.findById(lockerId).orElse(null);
//        if (locker != null) {
//            // 먼저 회원 정보를 업데이트
//            if (locker.getMemnum() != null) {
//                // 회원 ID로 회원 조회
//                MemberEntity member = (MemberEntity) memberRepository.findById(Long.valueOf(locker.getMemnum())).orElse(null);
//                if (member != null) {
//                    // 회원의 락커 관련 정보 초기화
//                    member.setLocker(null);  // 락커 정보 초기화
//                    member.setLocknum(null);
//                    member.setLockstart(null);
//                    member.setLockend(null);
//                    memberRepository.save(member);  // 회원 정보 저장
//                }
//            }
//
//            // 회원 정보가 업데이트된 후 락커 정보 업데이트
//            locker.setLockstart(null);
//            locker.setLockend(null);
//            locker.setMemnum(null);
//            locker.setName(null);
//            locker.setIsOccupied(false);
//            locker.setLocker(null);
//            locker.setStatus("보관중");
//            //locker.setStatus(null); 일단 null 로 보관
//            lockerRepository.save(locker);  // 락커 정보 저장
//        }
//    }


    public void collectLocker(Long lockerId) {
        LockerEntity locker = lockerRepository.findById(lockerId).orElse(null);
        if (locker != null) {
            if (locker.getMemnum() != null) {
                MemberEntity member = (MemberEntity) memberRepository.findById(Long.valueOf(locker.getMemnum())).orElse(null);
                if (member != null) {
                    // 수거 이력에 저장
                    LockHistoryEntity history = new LockHistoryEntity();
                    history.setLockstart(locker.getLockstart());
                    history.setLockend(locker.getLockend());
                    history.setLockpass(locker.getLockpass());
                    history.setLocker(locker.getLocker());
                    history.setMemnum(String.valueOf(member.getId()));
                    history.setName(member.getName());
                    history.setLocknum(locker.getLocknum());
                    history.setStatus("보관중");
                    lockHistoryRepository.save(history);

                    // 회원의 락커 정보 초기화
                    member.setLocker(null);
                    member.setLocknum(null);
                    member.setLockstart(null);
                    member.setLockend(null);
                    memberRepository.save(member);
                }
            }
            // 락커 정보 초기화
            locker.setLockstart(null);
            locker.setLocker(null);
            locker.setLockend(null);
            locker.setMemnum(null);
            locker.setName(null);
            locker.setIsOccupied(false);
            locker.setStatus("보관중");
            lockerRepository.save(locker);
        }
    }
    public LockerDTO findByMemberId(String memnum) {
        LockerEntity locker = lockerRepository.findByMemnum(memnum);
        if (locker != null) {
            return new LockerDTO(locker);  // LockerDTO로 변환하는 로직
        }
        return null;
    }



}