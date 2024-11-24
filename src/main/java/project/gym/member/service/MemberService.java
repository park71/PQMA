package project.gym.member.service;

import com.google.zxing.WriterException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.gym.member.dto.MemberDTO;
import project.gym.member.dto.MembershipDTO;
import project.gym.member.dto.PTContractDTO;
import project.gym.member.dto.RestDTO;
import project.gym.member.entity.*;
import project.gym.member.repository.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MemberService {

    @Autowired
    private QRService qrService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private EntryRecordRepository entryRecordRepository;

    @Autowired
    private RestRepository restRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PTContractRepository ptContractRepository;

    @Autowired
    private DecrementRecordRepository decrementRecordRepository;

    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private TrasnferRepository trasnferRepository;
    @Autowired
    private InbodyRepository inbodyRepository;

    public UserEntity loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return userEntity;
    }


    public void memberProcess(MemberDTO memberDTO) {

        Optional<MemberEntity> existingMemberOptional = memberRepository.findByPhone(memberDTO.getPhone());

        if (existingMemberOptional.isPresent()) {
            // 이미 존재하는 전화번호 회원이면 정보를 업데이트
            MemberEntity existingMember = existingMemberOptional.get();
            existingMember.setCoach(memberDTO.getCoach());
            existingMember.setGender(memberDTO.getGender());
            existingMember.setPhone(memberDTO.getPhone());
            existingMember.setBirth(memberDTO.getBirth());
            existingMember.setAddress(memberDTO.getAddress());
            existingMember.setKakao(memberDTO.getKakao());
            existingMember.setPurpose(memberDTO.getPurpose());
            existingMember.setComein(memberDTO.getComein());
            existingMember.setMembership(memberDTO.getMembership());
            existingMember.setCredit(memberDTO.getCredit());
            existingMember.setMemstart(memberDTO.getMemstart());
            existingMember.setMemend(memberDTO.getMemend());
            existingMember.setRemainDays(memberDTO.getRemainDays());
            existingMember.setLocker(memberDTO.getLocker());
            existingMember.setLocknum(memberDTO.getLocknum());
            existingMember.setLockstart(memberDTO.getLockstart());
            existingMember.setLockend(memberDTO.getLockend());
            existingMember.setShirt(memberDTO.getShirt());
            existingMember.setShirtstart(memberDTO.getShirtstart());
            existingMember.setShirtend(memberDTO.getShirtend());
            existingMember.setRemainDays(memberDTO.getRemainDays());
            existingMember.setStatus(memberDTO.getStatus());
            existingMember.setApplicationDate(memberDTO.getApplicationDate());

            existingMember.setStatus("approved");

            memberRepository.save(existingMember); // 존재하는 회원 정보 업데이트
        } else {
            // 새로운 회원 정보 생성 및 저장
            MemberEntity newMember = new MemberEntity();
            newMember.setId(memberDTO.getId());
            newMember.setCoach(memberDTO.getCoach());
            newMember.setName(memberDTO.getName());
            newMember.setGender(memberDTO.getGender());
            newMember.setPhone(memberDTO.getPhone());
            newMember.setBirth(memberDTO.getBirth());
            newMember.setAddress(memberDTO.getAddress());
            newMember.setKakao(memberDTO.getKakao());
            newMember.setPurpose(memberDTO.getPurpose());
            newMember.setComein(memberDTO.getComein());
            newMember.setMembership(memberDTO.getMembership());
            newMember.setCredit(memberDTO.getCredit());
            newMember.setMemstart(memberDTO.getMemstart());
            newMember.setMemend(memberDTO.getMemend());
            newMember.setRemainDays(memberDTO.getRemainDays());
            newMember.setLocker(memberDTO.getLocker());
            newMember.setLocknum(memberDTO.getLocknum());
            newMember.setLockstart(memberDTO.getLockstart());
            newMember.setLockend(memberDTO.getLockend());
            newMember.setShirt(memberDTO.getShirt());
            newMember.setShirtstart(memberDTO.getShirtstart());
            newMember.setShirtend(memberDTO.getShirtend());
            newMember.setRemainDays(memberDTO.getRemainDays());
            newMember.setStatus(memberDTO.getStatus());
            newMember.setApplicationDate(memberDTO.getApplicationDate());

            newMember.setStatus("approved");

            memberRepository.save(newMember); // 새로운 회원 정보 저장
        }
    }

    @Transactional
    public void registerOrUpdateUser(MemberDTO userDTO) {
        // DTO를 Entity로 변환
        MemberEntity userEntity = new MemberEntity();
        userEntity.setPhone(userDTO.getPhone());
        userEntity.setAddress(userDTO.getAddress());
        userEntity.setBirth(userDTO.getBirth());
        userEntity.setCoach(userDTO.getCoach());
        userEntity.setComein(userDTO.getComein());
        userEntity.setCredit(userDTO.getCredit());
        userEntity.setGender(userDTO.getGender());
        userEntity.setLockend(userDTO.getLockend());
        userEntity.setLocker(userDTO.getLocker());
        userEntity.setLocknum(userDTO.getLocknum());
        userEntity.setLockstart(userDTO.getLockstart());
        userEntity.setMembership(userDTO.getMembership());
        userEntity.setMemend(userDTO.getMemend());
        userEntity.setMemstart(userDTO.getMemstart());
        userEntity.setName(userDTO.getName());
        userEntity.setPurpose(userDTO.getPurpose());
        userEntity.setShirt(userDTO.getShirt());
        userEntity.setShirtend(userDTO.getShirtend());
        userEntity.setShirtstart(userDTO.getShirtstart());

        // 카카오톡 ID 설정
        String phone = userDTO.getPhone();
        String kakao = "만리" + userDTO.getName() + phone.substring(phone.length() - 4);
        userEntity.setKakao(kakao);
        log.info("Kakao ID: " + kakao);

        // 남은 일수 설정
        if (userDTO.getMemend() != null) {
            long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), userDTO.getMemend());
            userEntity.setRemainDays(remainDays);
        } else {
            userEntity.setRemainDays(0L); // 기본값 설정
        }

        // 기존 사용자 검색 (예: 전화번호를 기준으로)
        Optional<MemberEntity> existingUserOpt = memberRepository.findByPhone(userDTO.getPhone());
        if (existingUserOpt.isPresent()) {
            // 기존 사용자가 있는 경우 업데이트
            MemberEntity existingUser = existingUserOpt.get();
            userEntity.setId(existingUser.getId()); // ID 설정 (중복 방지)
        }

        // 변환된 엔티티를 저장 (업데이트 또는 신규 저장)
        memberRepository.save(userEntity);

        // DTO 객체에 저장된 kakao 값 반영
        userDTO.setKakao(kakao);
    }


    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (MemberEntity memberEntity : memberEntityList) {
            memberDTOList.add(MemberDTO.MembershipDTO(memberEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return memberDTOList;
    }

    public List<MembershipDTO> findAlls() {
        List<MembershipEntity> membershipEntityList = membershipRepository.findAll();
        List<MembershipDTO> membershipDTOList = new ArrayList<>();
        for (MembershipEntity membershipEntity : membershipEntityList) {
            membershipDTOList.add(MembershipDTO.MembershipsDTO(membershipEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return membershipDTOList;
    }


    public List<PTContractDTO> findAlli() {
        List<PTContractEntity> ptContractEntities = ptContractRepository.findAll();
        List<PTContractDTO> ptContractDTOList = new ArrayList<>();
        for (PTContractEntity ptContractEntity : ptContractEntities) {
            ptContractDTOList.add(PTContractDTO.ptDTOs(ptContractEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return ptContractDTOList;
    }

    public List<RestDTO> findAllm() {
        List<RestEntity> restEntities = restRepository.findAll();
        List<RestDTO> restDTOList = new ArrayList<>();
        for (RestEntity restEntity : restEntities) {
            restDTOList.add(RestDTO.PauseResponseDTO(restEntity));
//            MemberDTO memberDTO = MemberDTO.toMemberDTO(memberEntity);
//            memberDTOList.add(memberDTO);
        }
        return restDTOList;
    }

    public String lockCheck(Integer locknum) { // 락카 유무 확인
        Optional<MemberEntity> byMemberLocker = memberRepository.findByLocknum(locknum);
        if (byMemberLocker.isPresent()) {
            // 조회결과 있음 -> 사용 x
            return null;
        } else {
            // 조회결과 없음 -> 사용 o
            return "ok";
        }
    }


    // 이름으로 회원 검색
    public List<MemberDTO> findByNaming(String name) {
        // 이름으로 검색하여 결과 반환
        return memberRepository.findByNameContaining(name)
                .stream()
                .map(this::convertDTO)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> findByNameship(String name) {
        // 이름으로 검색하여 결과 반환
        return membershipRepository.findByNameContaining(name)
                .stream()
                .map(this::convertvDTO)
                .collect(Collectors.toList());
    }

    public List<PTContractDTO> findByNamed(String name) {
        return ptContractRepository.findByNameContaining(name)
                .stream()
                .map(this::converDTO)
                .collect(Collectors.toList());
    }

    public List<RestDTO> findByNamem(String name) {
        return restRepository.findByNameContaining(name)
                .stream()
                .map(this::converz)
                .collect(Collectors.toList());
    }


    // Entity를 DTO로 변환
    private MemberDTO convertDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setCoach(memberEntity.getCoach());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setGender(memberEntity.getGender());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setBirth(memberEntity.getBirth());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setKakao(memberEntity.getKakao());
        memberDTO.setPurpose(memberEntity.getPurpose());
        memberDTO.setComein(memberEntity.getComein());
        memberDTO.setMembership(memberEntity.getMembership());
        memberDTO.setCredit(memberEntity.getCredit());
        memberDTO.setMemstart(memberEntity.getMemstart());
        memberDTO.setMemend(memberEntity.getMemend());
        memberDTO.setRemainDays(memberEntity.getRemainDays());
        memberDTO.setLocker(memberEntity.getLocker());
        memberDTO.setLocknum(memberEntity.getLocknum());
        memberDTO.setLockstart(memberEntity.getLockstart());
        memberDTO.setLockend(memberEntity.getLockend());
        memberDTO.setShirt(memberEntity.getShirt());
        memberDTO.setShirtstart(memberEntity.getShirtstart());
        memberDTO.setShirtend(memberEntity.getShirtend());
        memberDTO.setStatus(memberEntity.getStatus());
        memberDTO.setPrice(memberEntity.getPrice());
        memberDTO.setApplicationDate(memberEntity.getApplicationDate());
        memberDTO.setQrCodePath(memberEntity.getQrCodePath());
        memberDTO.setContent(memberEntity.getContent());
        memberDTO.setProfile(memberEntity.getProfile());
        memberDTO.setRestcount(memberEntity.getRestcount());
        memberDTO.setProfileImage(memberEntity.getProfileImage());

        memberDTO.setStatus("approved");
        return memberDTO;
    }

    private PTContractDTO converDTO(PTContractEntity ptContractEntity) {
        PTContractDTO ptContractDTO = new PTContractDTO();
        ptContractDTO.setId(ptContractEntity.getId());
        ptContractDTO.setCoach(ptContractEntity.getCoach());
        ptContractDTO.setName(ptContractEntity.getName());
        ptContractDTO.setGender(ptContractEntity.getGender());
        ptContractDTO.setPhone(ptContractEntity.getPhone());
        ptContractDTO.setBirth(ptContractEntity.getBirth());
        ptContractDTO.setAddress(ptContractEntity.getAddress());
        ptContractDTO.setKakao(ptContractEntity.getKakao());
        ptContractDTO.setPurpose(ptContractEntity.getPurpose());
        ptContractDTO.setPtmembership(ptContractEntity.getPtmembership());
        ptContractDTO.setCredit(ptContractEntity.getCredit());
        ptContractDTO.setPtstart(ptContractEntity.getPtstart());
        ptContractDTO.setCount(String.valueOf(ptContractEntity.getCount()));
        ptContractDTO.setStatus(ptContractEntity.getStatus());
        ptContractDTO.setApplicationDate(ptContractEntity.getApplicationDate());
        ptContractDTO.setPrice(ptContractEntity.getPrice());

        ptContractDTO.setStatus("approved");
        return ptContractDTO;
    }

    private RestDTO converz(RestEntity restEntity) {
        RestDTO restDTO = new RestDTO();
        restDTO.setId(Math.toIntExact(restEntity.getId()));
        restDTO.setReason(restEntity.getReason());
        restDTO.setName(restEntity.getName());
        restDTO.setPhone(restEntity.getPhone());
        restDTO.setDelayDays(restEntity.getDelayDays());
        restDTO.setDelayDaysForLocker(restEntity.getDelayDaysForLocker());
        restDTO.setDelayDaysForShirt(restEntity.getDelayDaysForShirt());
        restDTO.setLockprice(restEntity.getLockprice());
        restDTO.setApplicationDate(restEntity.getApplicationDate());

        return restDTO;
    }


    public List<LockerEntity> getAllLockers() {
        return lockerRepository.findAll();
    }

    public boolean registerEntry(String phoneSuffix, String birth) {
        // 생년월일이 null이거나 길이가 8이 아닌 경우 (예: 20000925)
        if (birth == null || birth.length() != 8) {
            System.out.println("생년월일의 형식이 유효하지 않음: " + birth);
            return false;
        }
        try {
            // 생년월일을 LocalDate로 변환하고 MMDD 형식으로 추출
            LocalDate birthDate = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
            String birthMMDD = birthDate.format(DateTimeFormatter.ofPattern("MMdd")); // MMDD 형식으로 변환

            // 전화번호 뒷자리와 생년월일(MMDD)로 회원을 찾음
            Optional<MemberEntity> memberOpt = memberRepository.findByPhoneEndingWithAndBirth(phoneSuffix, birthMMDD);

            if (memberOpt.isPresent()) {
                MemberEntity member = memberOpt.get();
                // 로그 추가: 찾은 회원 정보 확인
                System.out.println("찾은 회원 이름: " + member.getName());
                // 출입 기록 생성 및 저장
                ZonedDateTime entryTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
                LocalDateTime localDateTime = entryTime.toLocalDateTime();
                EntryRecordEntity entryRecord = new EntryRecordEntity(member, localDateTime, member.getName());
                entryRecordRepository.save(entryRecord);
                System.out.println("출입 기록 저장 성공");
                return true; // 등록 성공
            }
            System.out.println("해당 정보로 회원을 찾을 수 없음");
            return false; // 등록 실패
        } catch (DateTimeParseException e) {
            System.out.println("생년월일 변환 중 오류 발생: " + e.getMessage());
            return false;
        }
    }
    public boolean registerEntrys(String phoneSuffix, String birth) {

            //생년월일의 길이가 4인지 체크
            if (birth == null || birth.length() != 4) {
                System.out.println("생년월일의 형식이 유효하지 않음: " + birth);
                return false;
            }
            // 생년월일의 '월일' 부분을 직접 사용 ("MMDD" 형식으로 변환)
            String birthDay = birth; // 예: 0603
            // 전화번호 뒷자리와 생년월일의 '월일' 부분으로 회원을 찾음
            Optional<MemberEntity> memberOpt = memberRepository.findByPhoneEndingWithAndBirth(phoneSuffix, birthDay);


            if (memberOpt.isPresent()) {
                MemberEntity member = memberOpt.get();

                // 로그 추가: 찾은 회원 정보 확인
                System.out.println("찾은 회원 이름: " + member.getName());

                // 출입 기록 생성 및 저장
                ZonedDateTime entryTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
                LocalDateTime localDateTime = entryTime.toLocalDateTime(); // 시간대 없이 LocalDateTime으로 변환
                EntryRecordEntity entryRecord = new EntryRecordEntity(member, localDateTime, member.getName());
                entryRecordRepository.save(entryRecord);
                System.out.println("출입 기록 저장 성공");

                return true; // 등록 성공
            }

            System.out.println("해당 정보로 회원을 찾을 수 없음");
            return false; // 등록 실패
        }





    public MemberDTO findByName(String name) { //회원 개인정보 불러올때
        Optional<MemberEntity> memberOpt = memberRepository.findByName(name);

        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setName(member.getName());
            memberDTO.setCoach(member.getCoach());
            memberDTO.setGender(member.getGender());
            memberDTO.setPhone(member.getPhone());
            memberDTO.setAddress(member.getAddress());
            memberDTO.setMembership(member.getMembership());
            memberDTO.setMemstart(member.getMemstart());
            memberDTO.setMemend(member.getMemend());
            //memberDTO.setLocker(member.getLocker());
            // memberDTO.setLocknum(member.getLocknum());
            // memberDTO.setLockstart(member.getLockstart());
            // memberDTO.setLockend(member.getLockend());
            memberDTO.setShirt(member.getShirt());
            memberDTO.setShirtstart(member.getShirtstart());
            memberDTO.setShirtend(member.getShirtend());
            memberDTO.setRemainDays(member.getRemainDays());

            return memberDTO;
        }
        return null; // 또는 Optional.empty() 등 적절한 처리를 추가할 수 있습니다.
    }


    public MemberDTO findByNames(Integer id) { // 휴회
        Optional<MemberEntity> memberEntityOpt = memberRepository.findById(id);
        if (!memberEntityOpt.isPresent()) {
            throw new EntityNotFoundException("Member not found");
        }
        MemberDTO memberDTO = convertToDTO(memberEntityOpt.get());
        // 디버그 로그 추가
        System.out.println("MemberDTO: " + memberDTO);
        System.out.println("MemberDTO: " + memberDTO.getMemstart());
        System.out.println("MemberDTO: " + memberDTO.getLockstart());
        return memberDTO;
    }

    public MembershipEntity findByNamea(String name) {
        List<MembershipEntity> entities = membershipRepository.findByName(name);
        return entities.isEmpty() ? null : entities.get(0); // 첫 번째 엔티티 반환
    }


    // 전화번호로 회원 조회
    public MembershipEntity findByPhones(String phone) {
        return membershipRepository.findOptionalByPhone(phone);
    }

    public void updateRestCount(String phone, int newRestCount) {

        // 전화번호로 회원 정보 조회
        Optional<MemberEntity> optionalMember = memberRepository.findByPhone(phone);

        // 회원 정보가 존재할 경우 업데이트
        if (optionalMember.isPresent()) {
            MemberEntity member = optionalMember.get();
            member.setRestcount(newRestCount);
            memberRepository.save(member);
        }
    }
    public int getRestCount(String phone) {
        Optional<MemberEntity> member = memberRepository.findByPhone(phone);
        return member.map(MemberEntity::getRestcount).orElse(0);
    }

    public void updateMembershipEndDate(String phone, int delayDays) {
        System.out.println("delayDays: " + delayDays);
        // Phone으로 MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findAllByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Member not found with phone: " + phone));

        // Phone으로 MembershipEntity 조회
        MembershipEntity membershipEntity = membershipRepository.findByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with phone: " + phone));

        // MemberEntity의 종료일 업데이트
        LocalDate newEndDate = memberEntity.getMemend().plusDays(delayDays);
        memberEntity.setMemend(newEndDate);
        memberRepository.save(memberEntity);
        System.out.println("바뀐 값: " + newEndDate);

        // MembershipEntity의 종료일 업데이트
        LocalDate newMembershipEndDate = membershipEntity.getMemend().plusDays(delayDays);
        membershipEntity.setMemend(newMembershipEndDate);
        membershipRepository.save(membershipEntity);
        System.out.println("바뀐 값: " + newMembershipEndDate);
    }

    public void updateLockerEndDate(String phone, int delayDays) {
        // Phone으로 MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findAllByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Member not found with phone: " + phone));

        // Phone으로 MembershipEntity 조회
        MembershipEntity membershipEntity = membershipRepository.findByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with phone: " + phone));

        // 락카 종료일 업데이트
        LocalDate currentLockEndDate = memberEntity.getLockend();
        if (currentLockEndDate != null) {
            currentLockEndDate = currentLockEndDate.plusDays(delayDays);
            memberEntity.setLockend(currentLockEndDate);
        }
        memberRepository.save(memberEntity);

        // 락카 종료일 업데이트 (MembershipEntity에도 반영)
        LocalDate newLockerEndDate = membershipEntity.getLockend();
        if (newLockerEndDate != null) {
            newLockerEndDate = newLockerEndDate.plusDays(delayDays);
            membershipEntity.setLockend(newLockerEndDate);
        }
        membershipRepository.save(membershipEntity);
    }

    public void updateShirtEndDate(String phone, int delayDays) {
        // Phone으로 MemberEntity 조회
        MemberEntity memberEntity = memberRepository.findAllByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Member not found with phone: " + phone));

        // Phone으로 MembershipEntity 조회
        MembershipEntity membershipEntity = membershipRepository.findByPhone(phone)
                .stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with phone: " + phone));

        // 운동복 종료일 업데이트
        LocalDate currentShirtEndDate = memberEntity.getShirtend();
        if (currentShirtEndDate != null) {
            currentShirtEndDate = currentShirtEndDate.plusDays(delayDays);
            memberEntity.setShirtend(currentShirtEndDate);
        }
        memberRepository.save(memberEntity);

        // 운동복 종료일 업데이트 (MembershipEntity에도 반영)
        LocalDate newShirtEndDate = membershipEntity.getShirtend();
        if (newShirtEndDate != null) {
            newShirtEndDate = newShirtEndDate.plusDays(delayDays);
            membershipEntity.setShirtend(newShirtEndDate);
        }
        membershipRepository.save(membershipEntity);
    }


    private MemberDTO convertToDTO(MemberEntity memberEntity) {
        MemberDTO dto = new MemberDTO();

        dto.setName(memberEntity.getName());
        dto.setCredit(memberEntity.getCredit());
        dto.setBirth(memberEntity.getBirth());
        dto.setPrice(memberEntity.getPrice());
        dto.setPhone(memberEntity.getPhone());
        dto.setMemstart(memberEntity.getMemstart());
        dto.setMemend(memberEntity.getMemend());
        dto.setRemainDays(memberEntity.getRemainDays());
        dto.setLockend(memberEntity.getLockend());
        dto.setLockstart(memberEntity.getLockstart());
        dto.setLockend(memberEntity.getLockend());
        dto.setShirtstart(memberEntity.getShirtstart());
        dto.setShirtend(memberEntity.getShirtend());
        dto.setRestcount(memberEntity.getRestcount());
        // Set other fields as needed

        return dto;
    }

    private MemberEntity convertToEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setName(memberDTO.getName());
        memberEntity.setMemend(memberDTO.getMemend());
        memberEntity.setLockend(memberEntity.getLockend());
        memberEntity.setShirtend(memberEntity.getShirtend());
        // Set other fields as needed
        return memberEntity;
    }

    ////////////////////////////////////////////
    @Transactional
    public void subtractRemainingDays(Long memberId, int days) {
        MemberEntity member = (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.getRemainDays() < days) {
            throw new RuntimeException("Insufficient remaining days");
        }

        member.setRemainDays(member.getRemainDays() - days);
        memberRepository.save(member);
    }

    @Transactional
    public void addRemainingDays(Long id, int days) {
        MemberEntity member = (MemberEntity) memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setRemainDays(member.getRemainDays() + days);
        memberRepository.save(member);
    }

    public List<MemberEntity> getAllMembers() {

        return memberRepository.findAll();
    }

    // 모든 회원 정보와 남은 일수를 반환하는 메서드
    public List<MemberEntity> getAllMembersWithRemainDays() {
        List<MemberEntity> allMembers = memberRepository.findAll();
        for (MemberEntity member : allMembers) {
            LocalDate memend = member.getMemend();
            if (memend != null) {
                long remainDays = calculateRemainDays(memend);
                member.setRemainDays(remainDays);
            }
        }
        return allMembers;
    }

    // 개별 회원의 남은 일수를 계산하는 메서드
    private long calculateRemainDays(LocalDate memend) {
        LocalDate currentDate = LocalDate.now();
        return ChronoUnit.DAYS.between(currentDate, memend);
    }

    // 회원 이름으로 검색된 회원 목록을 반환하는 메서드
    public List<MemberEntity> searchMembersByName(String query) {
        List<MemberEntity> allMembers = getAllMembersWithRemainDays();
        return allMembers.stream()
                .filter(member -> member.getName() != null && member.getName().contains(query))
                .collect(Collectors.toList());
    }

    // 회원 ID로 회원을 찾아서 반환하는 메서드
    public MemberEntity getMemberById(Long memberId) {
        return (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
    }

    /////////////////////////////////////////////////////// 관리자모드 게시물 업로드하기
    // 글 작성 처리
    public void write(BoardEntity board, MultipartFile file) throws Exception {

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        board.setFileName(fileName);
        board.setFilePath("/files/" + fileName);

        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<BoardEntity> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }

    public Page<BoardEntity> boardSearchList(String searchKeyword, Pageable pageable) {

        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public BoardEntity boardView(Integer id) {

        return boardRepository.findById(id).get();
    }

    public MemberDTO findById(Long id) {
        MemberEntity memberEntity = (MemberEntity) memberRepository.findById(id).orElse(null);
        if (memberEntity != null) {
            // MemberEntity를 MemberDTO로 변환 (필요에 따라 변환 로직 추가)
            return MemberDTO.MembershipDTO(memberEntity);
        }
        return null;
    }
    // 회원 ID로 회원 조회
    public MemberEntity findByIdif(Long memberId) {
        // Optional을 사용하여 null 처리
        return (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
    }

    public Optional<MemberEntity> findByIda(Long id) {
        return memberRepository.findAllById(id);
    }

    public MemberEntity findByIdv(Long memberId) {
        return (MemberEntity) memberRepository.findById(memberId).orElse(null);
    }


    @Transactional
    public void PTregisterOrUpdateUser(PTContractEntity ptuser) {
        Optional<PTContractEntity> existingUserOpt = ptContractRepository.findByPhone(ptuser.getPhone());

        if (existingUserOpt.isPresent()) {
            // 기존 사용자가 존재하는 경우, 해당 ID를 사용하여 업데이트
            PTContractEntity existingUser = existingUserOpt.get();
            ptuser.setId(existingUser.getId());
            ptuser.setKakao(existingUser.getKakao()); // 기존 카카오톡 ID 사용

        } else {
            log.info("일로드어오지");
            // 새로운 사용자인 경우, 카카오톡 ID와 남은 일수 설정
            String phone = ptuser.getPhone();
            String kakao = "만리" + ptuser.getName() + phone.substring(phone.length() - 4);
            ptuser.setKakao(kakao);
        }
        ptContractRepository.save(ptuser);
    }

    public void PTmemberProcess(PTContractDTO ptContractDTO) {

        Optional<PTContractEntity> existingMemberOptional = ptContractRepository.findByPhone(ptContractDTO.getPhone());

        if (existingMemberOptional.isPresent()) {
            // 이미 존재하는 전화번호 회원이면 정보를 업데이트
            PTContractEntity existingMember = existingMemberOptional.get();
            existingMember.setCoach(ptContractDTO.getCoach());
            existingMember.setGender(ptContractDTO.getGender());
            existingMember.setPhone(ptContractDTO.getPhone());
            existingMember.setBirth(ptContractDTO.getBirth());
            existingMember.setAddress(ptContractDTO.getAddress());
            existingMember.setKakao(ptContractDTO.getKakao());
            existingMember.setPurpose(ptContractDTO.getPurpose());
            existingMember.setPtmembership(ptContractDTO.getPtmembership());
            existingMember.setPtstart(ptContractDTO.getPtstart());
            existingMember.setCount(Integer.valueOf(ptContractDTO.getCount()));
            existingMember.setCredit(ptContractDTO.getCredit());
            existingMember.setStatus(ptContractDTO.getStatus());
            existingMember.setApplicationDate(ptContractDTO.getApplicationDate());
            existingMember.setPrice(ptContractDTO.getPrice());

            existingMember.setStatus("approved");
            ptContractRepository.save(existingMember); // 존재하는 회원 정보 업데이트
        } else {
            // 새로운 회원 정보 생성 및 저장
            PTContractEntity newMember = new PTContractEntity();
            newMember.setId(ptContractDTO.getId());
            newMember.setCoach(ptContractDTO.getCoach());
            newMember.setName(ptContractDTO.getName());
            newMember.setGender(ptContractDTO.getGender());
            newMember.setPhone(ptContractDTO.getPhone());
            newMember.setBirth(ptContractDTO.getBirth());
            newMember.setAddress(ptContractDTO.getAddress());
            newMember.setKakao(ptContractDTO.getKakao());
            newMember.setPurpose(ptContractDTO.getPurpose());
            newMember.setPtmembership(ptContractDTO.getPtmembership());
            newMember.setPtstart(ptContractDTO.getPtstart());
            newMember.setCount(Integer.valueOf(ptContractDTO.getCount()));
            newMember.setCredit(ptContractDTO.getCredit());
            newMember.setStatus(ptContractDTO.getStatus());
            newMember.setApplicationDate(ptContractDTO.getApplicationDate());
            newMember.setPrice(ptContractDTO.getPrice());

            newMember.setStatus("approved");


            ptContractRepository.save(newMember); // 새로운 회원 정보 저장
        }
    }

    public MemberDTO findByIding(Long id) {
        MemberEntity memberEntity = (MemberEntity) memberRepository.findById(id).orElse(null);
        if (memberEntity != null) {
            return convertsDTO(memberEntity);
        }
        return null;
    }

    public List<MembershipDTO> findMembershipsByMemberId(Long id) {
        List<MembershipEntity> memberships = membershipRepository.findByMemberId(id);
        return memberships.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private MemberDTO convertsDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setName(memberEntity.getName());
        // 나머지 멤버 정보 설정
        return memberDTO;
    }

    public MembershipDTO convertToDTO(MembershipEntity membershipEntity) {
        MembershipDTO membershipDTO = new MembershipDTO();
        membershipDTO.setMembership(membershipEntity.getMembership());
        membershipDTO.setMember_id(Long.valueOf(membershipEntity.getMember().getId()));
        membershipDTO.setName(membershipEntity.getName());
        membershipDTO.setCredit(membershipEntity.getCredit());
        membershipDTO.setMemstart(membershipEntity.getMemstart());
        membershipDTO.setMemend(membershipEntity.getMemend());
        membershipDTO.setCoach(membershipEntity.getCoach());
        membershipDTO.setPhone(membershipEntity.getPhone());
        membershipDTO.setAddress(membershipEntity.getAddress());
        membershipDTO.setGender(membershipEntity.getGender());
        membershipDTO.setShirt(membershipEntity.getShirt());
        membershipDTO.setShirtstart(membershipEntity.getShirtstart());
        membershipDTO.setShirtend(membershipEntity.getShirtend());
        membershipDTO.setLocker(membershipEntity.getLocker());
        membershipDTO.setLocknum(membershipEntity.getLocknum());
        membershipDTO.setLockstart(membershipEntity.getLockstart());
        membershipDTO.setLockend(membershipEntity.getLockend());
        membershipDTO.setPrice(membershipEntity.getPrice());
        membershipDTO.setContent(membershipEntity.getContent());
        membershipDTO.setTotalprice(membershipEntity.getTotalprice());


        return membershipDTO;
    }

    public Optional<MemberEntity> findByUsername(String username) {
        return memberRepository.findByName(username);  // 반환 타입을 Optional로
    }

    public MemberEntity findAllByNames(String name) {
        return memberRepository.findOptionalByName(name);
    }

    public Optional<MemberEntity> findByPhonea(String phone) {
        return memberRepository.findByPhone(phone);  // 반환 타입을 Optional로
    }


    @Transactional
    public MemberEntity findOrCreateByUsername(String username) {
        return findByUsername(username).orElseGet(MemberEntity::new);
    }

    // 이름과 전화번호로 회원 조회
    public Optional<MemberEntity> findByNameAndPhone(String name, String phone) {
        return memberRepository.findByNameAndPhone(name, phone);
    }

    @Transactional
    public MemberEntity save(MemberEntity member) {
        return memberRepository.save(member);
    }
    public void updateMemberEntityFromDTO(MemberEntity member, MemberDTO memberDTO) {

        member.setPhone(memberDTO.getPhone());
        member.setAddress(memberDTO.getAddress());
        member.setBirth(memberDTO.getBirth());
        member.setCoach(memberDTO.getCoach());
        member.setContent(memberDTO.getContent());
        member.setComein(memberDTO.getComein());
        member.setCredit(memberDTO.getCredit());
        member.setGender(memberDTO.getGender());
        member.setPrice(memberDTO.getPrice());
        member.setLockend(memberDTO.getLockend());
        member.setLocker(memberDTO.getLocker());
        member.setLocknum(memberDTO.getLocknum());
        member.setLockstart(memberDTO.getLockstart());
        member.setMembership(memberDTO.getMembership());
        member.setMemend(memberDTO.getMemend());
        member.setMemstart(memberDTO.getMemstart());
        member.setName(memberDTO.getName());
        member.setPurpose(memberDTO.getPurpose());
        member.setShirt(memberDTO.getShirt());
        member.setShirtend(memberDTO.getShirtend());
        member.setShirtstart(memberDTO.getShirtstart());
        member.setStatus(memberDTO.getStatus());
        member.setApplicationDate(LocalDateTime.now());
        member.setSignature(memberDTO.getSignature());

        // 카카오톡 ID 생성 및 설정
        member.setStatus("approved");

        if(memberDTO.getMembership().equals("6개월권")){
            member.setRestcount(1);
        }
        else if (memberDTO.getMembership().equals("12개월권")){
            member.setRestcount(2);
        } else {
            member.setRestcount(0);
        }

        String phone = memberDTO.getPhone();
        String kakao = "만리" + memberDTO.getName() + phone.substring(phone.length() - 4);
        member.setKakao(kakao);
        log.info("Kakao ID: " + kakao);


        // 남은 일수 설정
        if (memberDTO.getMemend() != null) {
            long remainDays = ChronoUnit.DAYS.between(LocalDate.now(), memberDTO.getMemend());
            member.setRemainDays(remainDays);
        } else {
            member.setRemainDays(0L); // 기본값 설정
        }
    }

    public void saveMembership(MembershipEntity membership) {
        membershipRepository.save(membership);
    }

    public MembershipEntity createMembershipEntityFromDTO(MemberDTO memberDTO, MemberEntity member) {
        // 유효성 검사 추가 (예시)
        if (memberDTO.getPhone() == null || memberDTO.getName() == null) {
            throw new IllegalArgumentException("전화번호와 이름은 필수 입력 사항입니다.");
        }

        MembershipEntity membership = new MembershipEntity();
        membership.setPhone(memberDTO.getPhone());
        membership.setCoach(memberDTO.getCoach());
        membership.setName(memberDTO.getName());
        membership.setContent(memberDTO.getContent());
        membership.setGender(memberDTO.getGender());
        membership.setBirth(memberDTO.getBirth());
        membership.setAddress(memberDTO.getAddress());
        membership.setKakao(memberDTO.getKakao());
        membership.setPurpose(memberDTO.getPurpose());
        membership.setComein(memberDTO.getComein());
        membership.setMembership(memberDTO.getMembership());
        membership.setCredit(memberDTO.getCredit());
        membership.setMemstart(memberDTO.getMemstart());
        membership.setMemend(memberDTO.getMemend());
        membership.setLocker(memberDTO.getLocker());
        membership.setLocknum(memberDTO.getLocknum());
        membership.setPrice(memberDTO.getPrice());
        membership.setLockstart(memberDTO.getLockstart());
        membership.setLockend(memberDTO.getLockend());
        membership.setShirt(memberDTO.getShirt());
        membership.setShirtstart(memberDTO.getShirtstart());
        membership.setShirtend(memberDTO.getShirtend());

        // 카카오톡 ID 생성 및 설정
        String phone = memberDTO.getPhone();
        String kakao = "만리" + memberDTO.getName() + phone.substring(phone.length() - 4);
        member.setKakao(kakao);
        log.info("Kakao ID: " + kakao);


        membership.setMember(member); // MemberEntity와 연결

        return membership;
    }

    @Transactional
    public void saveMemberWithMembership(MemberDTO memberDTO) {
        // 전화번호로 기존 회원 검색
        Optional<MemberEntity> existingMemberOptional = memberRepository.findByPhone(memberDTO.getPhone());
        System.out.println(memberDTO.getPhone());

        MemberEntity member;

        if (existingMemberOptional.isPresent()) {
            // 기존 회원이 존재할 경우
            member = existingMemberOptional.get();
            updateMemberEntityFromDTO(member, memberDTO);
        } else {
            // 기존 회원이 없을 경우 새로운 회원 생성
            member = new MemberEntity(); // 새 회원 객체 생성
            updateMemberEntityFromDTO(member, memberDTO);
        }

        // 멤버 정보 저장
        memberRepository.save(member);


        if ("일일입장".equals(member.getMembership())) {
            // "일일입장"일 경우 QR 코드 생성을 하지 않음
        } else {
            // 그 외의 경우 QR 코드 생성
            try {
                QRService qrCodeService = new QRService();
                LocalDate birthDateLocal = member.getBirth(); // assuming this is LocalDate
                String birthDate = birthDateLocal.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 형식 변경
                String qrCodeText = "PQMA 출입증" +
                        "\n이름: " + member.getName() +
                        "\n전화번호: " + member.getPhone() +
                        "\n생년월일: " + birthDate; // 수정된 생년월일 사용

                // QR 코드를 파일로 저장
                String qrCodePath = "src/main/resources/static/qrcode/" + member.getPhone() + ".png"; // 전화번호를 사용하여 파일 이름 설정
                qrCodeService.generateQRCodeImage(qrCodeText, 300, 300, qrCodePath);
                member.setQrCodePath(qrCodePath); // QR 코드 경로를 member에 설정

                // QR 코드 저장 즉시 반영
                memberRepository.saveAndFlush(member);

            } catch (WriterException | IOException e) {
                e.printStackTrace(); // 에러 처리
            }
        }


        // 새로운 MembershipEntity 생성
        MembershipEntity membership = createMembershipEntityFromDTO(memberDTO, member);
        membership.setPhone(member.getPhone()); // 전화번호 설정

        boolean isDuplicate = member.getContracts().stream()
                .anyMatch(existingMembership -> {
                    String existingPhone = existingMembership.getPhone();
                    return existingPhone != null &&
                            existingPhone.equals(membership.getPhone()) &&
                            existingMembership.getName().equals(membership.getName()) &&
                            existingMembership.getMemstart().equals(membership.getMemstart());
                });

        if (!isDuplicate) {
            // 멤버의 모든 멤버십의 총 가격 계산
            int totalPrice = member.getContracts().stream()
                    .mapToInt(membershipEntity -> Integer.parseInt(String.valueOf(membershipEntity.getPrice())))
                    .sum() + membership.getPrice(); // 새 멤버십 가격 포함

            membership.setTotalprice(totalPrice);
            membership.setKakao(member.getKakao());
            // 새 멤버십을 멤버의 계약 목록에 추가
            member.getContracts().add(membership);

            // 새 멤버십 저장
            membershipRepository.save(membership);
        }

        // Membership 값이 "PM 1개월"인 경우 PTContractEntity에도 저장
        if ("PM 1개월 4회".equals(member.getMembership())) {
            PTContractEntity ptContract = new PTContractEntity();
            ptContract.setName(member.getName());
            ptContract.setGender(member.getGender());
            ptContract.setPhone(member.getPhone());
            ptContract.setBirth(member.getBirth());
            ptContract.setAddress(member.getAddress());
            ptContract.setKakao(member.getKakao());
            ptContract.setPurpose(member.getPurpose());
            ptContract.setPtmembership("PM 4회"); // PT 회원권 설정
            ptContract.setPtstart(member.getMemstart());
            ptContract.setCount(4); // 10회로 설정
            ptContract.setCredit(member.getCredit());
            ptContract.setPrice(String.valueOf(member.getPrice()));
            ptContract.setStatus("approved");
            ptContract.setSignature(member.getSignature());


            // PTContractRepository에 PT 계약 저장
            ptContractRepository.save(ptContract);
        } else if ("PM 1개월 8회".equals(member.getMembership())) {
            PTContractEntity ptContract = new PTContractEntity();
            ptContract.setName(member.getName());
            ptContract.setGender(member.getGender());
            ptContract.setPhone(member.getPhone());
            ptContract.setBirth(member.getBirth());
            ptContract.setAddress(member.getAddress());
            ptContract.setKakao(member.getKakao());
            ptContract.setPurpose(member.getPurpose());
            ptContract.setPtmembership("PM 8회"); // PT 회원권 설정
            ptContract.setPtstart(member.getMemstart());
            ptContract.setCount(8);
            ptContract.setCredit(member.getCredit());
            ptContract.setPrice(String.valueOf(member.getPrice()));
            ptContract.setStatus("approved");
            ptContract.setSignature(member.getSignature());

            // PTContractRepository에 PT 계약 저장
            ptContractRepository.save(ptContract);
        } else if ("PM 1개월 10회".equals(member.getMembership())) {
            PTContractEntity ptContract = new PTContractEntity();
            ptContract.setName(member.getName());
            ptContract.setGender(member.getGender());
            ptContract.setPhone(member.getPhone());
            ptContract.setBirth(member.getBirth());
            ptContract.setAddress(member.getAddress());
            ptContract.setKakao(member.getKakao());
            ptContract.setPurpose(member.getPurpose());
            ptContract.setPtmembership("PM 10회"); // PT 회원권 설정
            ptContract.setPtstart(member.getMemstart());
            ptContract.setCount(10); // 10회로 설정
            ptContract.setCredit(member.getCredit());
            ptContract.setPrice(String.valueOf(member.getPrice()));
            ptContract.setStatus("approved");
            ptContract.setSignature(member.getSignature());

            // PTContractRepository에 PT 계약 저장
            ptContractRepository.save(ptContract);
        }
    }


    @Transactional
    public void updateMemberContent(Long memberId, String content) {
        // 회원 엔티티를 가져옴
        MemberEntity member = (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 회원권 엔티티 리스트를 가져옴
        List<MembershipEntity> memberships = membershipRepository.findByMemberId(memberId);

        MembershipDTO membershipDTO = null;

        if (memberships != null && !memberships.isEmpty()) {
            // 첫 번째 회원권에 메모를 업데이트 (또는 특정 조건에 맞는 회원권 선택)
            MembershipEntity membership = memberships.get(0);
            membership.setContent(content);
            membershipRepository.save(membership);

            // Entity를 DTO로 변환
            membershipDTO = convertEntityToDTO(membership);
            // DTO에 content 설정
            membershipDTO.setContent(content);
        } else {
            throw new RuntimeException("해당 회원의 회원권이 존재하지 않습니다.");
        }

        // MemberEntity에 메모를 업데이트
        member.setContent(content);
        memberRepository.save(member);

        // 필요시 DTO를 반환하거나 다른 계층에 전달
        // 예를 들어, 다른 서비스 메서드로 전달할 수 있음
    }

    // Entity를 DTO로 변환하는 메서드 (예시)
    private MembershipDTO convertEntityToDTO(MembershipEntity membership) {
        MembershipDTO membershipDTO = new MembershipDTO();
        membershipDTO.setName(membership.getName());
        membershipDTO.setMembership(membership.getMembership());
        membershipDTO.setMemstart(membership.getMemstart());
        membershipDTO.setMemend(membership.getMemend());
        membershipDTO.setAddress(membership.getAddress());
        membershipDTO.setCoach(membership.getCoach());
        membershipDTO.setPhone(membership.getPhone());
        membershipDTO.setGender(membership.getGender());
        membershipDTO.setKakao(membership.getKakao());
        membershipDTO.setPurpose(membership.getPurpose());
        membershipDTO.setComein(membership.getComein());
        membershipDTO.setCredit(membership.getCredit());
        membershipDTO.setLocker(membership.getLocker());
        membershipDTO.setLocknum(membership.getLocknum());
        membershipDTO.setLockstart(membership.getLockstart());
        membershipDTO.setLockend(membership.getLockend());
        membershipDTO.setShirt(membership.getShirt());
        membershipDTO.setShirtstart(membership.getShirtstart());
        membershipDTO.setShirtend(membership.getShirtend());
        membershipDTO.setPrice(membership.getPrice());
        membershipDTO.setContent(membership.getContent());
        // 필요한 필드들을 모두 설정합니다.
        return membershipDTO;
    }


    //회원정보 수정, 삭제
    // ID로 회원 조회
    public MemberEntity getMemberByMemberId(Long id) {
        // MemberEntity를 직접 반환하도록 수정합니다.
        return (MemberEntity) memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
    }


    // 프로필 이미지를 저장하는 메서드
    public String saveProfileImage(MultipartFile profileImage) throws IOException {
        // profile 폴더가 src/main/resources 내에 있다고 가정
        String uploadDir = "src/main/resources/static/profile";  // 이미지 저장 디렉토리 경로
        Path uploadPath = Paths.get(uploadDir);

        // 프로필 이미지 폴더가 없다면 생성
        if (!uploadPath.toFile().exists()) {
            uploadPath.toFile().mkdirs();  // 폴더 생성
        }

        // 고유한 파일명 생성 (UUID 사용)
        String fileExtension = profileImage.getOriginalFilename().split("\\.")[1];  // 확장자 추출
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;  // 고유한 파일명

        // 파일을 지정된 경로에 저장
        File file = new File(uploadPath.toFile(), fileName);
        profileImage.transferTo(file);

        return "/profile/" + fileName;  // 저장된 파일 경로 반환 (웹 접근용 경로)
    }

    public String handleFileUpload(MultipartFile file) throws Exception {
        // 정적 파일 저장 경로 지정
        String projectPath = "src/main/resources/static/profile";  // 수정된 경로
        File directory = new File(projectPath);
        if (!directory.exists()) {
            directory.mkdirs();  // 디렉토리가 없으면 생성
        }

        String fileName = file.getOriginalFilename(); // 파일 이름
        File saveFile = new File(directory, fileName);  // 파일 저장 경로
        file.transferTo(saveFile); // 파일 저장

        return "/profile/" + fileName;  // DB에 저장할 경로 반환
    }



    // 회원 정보 업데이트
    public void updateMember(Long id, MemberEntity member) {
        // 1. MemberEntity 수정
        MemberEntity existingMember = (MemberEntity) memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 필요한 필드 업데이트
        existingMember.setName(member.getName());
        existingMember.setAddress(member.getAddress());
        existingMember.setPhone(member.getPhone());
        existingMember.setMemstart(member.getMemstart());
        existingMember.setMemend(member.getMemend());
        existingMember.setGender(member.getGender());
        existingMember.setCoach(member.getCoach());
        existingMember.setBirth(member.getBirth());
        existingMember.setPurpose(member.getPurpose());
        existingMember.setComein(member.getComein());
        existingMember.setCredit(member.getCredit());
        existingMember.setPrice(member.getPrice());
        existingMember.setKakao(member.getKakao());
        existingMember.setMembership(member.getMembership());
        existingMember.setLocker(member.getLocker());
        existingMember.setLocknum(member.getLocknum());
        existingMember.setLockstart(member.getLockstart());
        existingMember.setLockend(member.getLockend());
        existingMember.setShirt(member.getShirt());
        existingMember.setShirtstart(member.getShirtstart());
        existingMember.setShirtend(member.getShirtend());
        existingMember.setContent(member.getContent());
        existingMember.setStatus(member.getStatus());
        existingMember.setContent(member.getContent());
        existingMember.setProfile(member.getProfile());

        existingMember.setStatus("approved");
        // 나머지 필드도 동일하게 업데이트...

        // 2. MembershipEntity 수정 (마지막으로 생성된 레코드 업데이트)
        MembershipEntity latestMembership = membershipRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("회원권 정보가 존재하지 않습니다."));

        latestMembership.setMemstart(member.getMemstart());
        latestMembership.setMemend(member.getMemend());
        latestMembership.setName(member.getName());
        latestMembership.setAddress(member.getAddress());
        latestMembership.setCoach(member.getCoach());
        latestMembership.setPhone(member.getPhone());
        latestMembership.setGender(member.getGender());
        latestMembership.setKakao(member.getKakao());
        latestMembership.setPurpose(member.getPurpose());
        latestMembership.setComein(member.getComein());
        latestMembership.setCredit(member.getCredit());
        latestMembership.setLocker(member.getLocker());
        latestMembership.setLocknum(member.getLocknum());
        latestMembership.setLockstart(member.getLockstart());
        latestMembership.setLockend(member.getLockend());
        latestMembership.setShirt(member.getShirt());
        latestMembership.setShirtstart(member.getShirtstart());
        latestMembership.setShirtend(member.getShirtend());
        latestMembership.setPrice(member.getPrice());
        latestMembership.setContent(member.getContent());
        latestMembership.setMembership(member.getMembership());
        // 다른 필드도 필요에 따라 업데이트...

        // 3. 변경사항 저장
        memberRepository.save(existingMember);
        membershipRepository.save(latestMembership);
    }

    // 회원 삭제
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    // 엔티티를 DTO로 변환하는 메서드 (예시)
   /* private MemberDTO convertsToDTO(MemberEntity member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getId());
        memberDTO.setName(member.getName());
        memberDTO.setAddress(member.getAddress());
        memberDTO.setPhone(member.getPhone());
        // 필요한 다른 필드들도 여기에 추가하세요
        return memberDTO;
    }*/
    // 전화번호로 회원 조회
    public MemberDTO findByPhoneNumber(String phoneNumber) {
        // Optional에서 MemberEntity를 추출
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByPhone(phoneNumber);

        if (memberEntityOpt.isEmpty()) {
            return null;
        }

        MemberEntity memberEntity = memberEntityOpt.get();
        MemberDTO memberDTO = convertEntityToDTOs(memberEntity);

        // 회원권 정보 가져오기
        List<MembershipEntity> memberships = membershipRepository.findByMemberId(Long.valueOf(memberEntity.getId()));
        List<MembershipDTO> membershipDTOs = memberships.stream()
                .map(this::convertMembershipEntityToDTO)
                .collect(Collectors.toList());

        memberDTO.setMemberships(membershipDTOs);

        return memberDTO;
    }
    public Optional<MemberEntity> findByPhoneNumbera(String phoneNumber) {
        return memberRepository.findByPhone(phoneNumber); // 전화번호로 회원 찾기
    }

    // 수정된 메서드
    public MemberDTO convertEntityToDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId()); // getId() 사용
        // 나머지 필드 설정
        return memberDTO;
    }

    public MemberDTO convertEntityToDTOs(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setCoach(memberEntity.getCoach());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setGender(memberEntity.getGender());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setBirth(memberEntity.getBirth());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setKakao(memberEntity.getKakao());
        memberDTO.setPurpose(memberEntity.getPurpose());
        memberDTO.setComein(memberEntity.getComein());
        memberDTO.setMembership(memberEntity.getMembership());
        memberDTO.setCredit(memberEntity.getCredit());
        memberDTO.setMemstart(memberEntity.getMemstart());
        memberDTO.setMemend(memberEntity.getMemend());
        memberDTO.setRemainDays(memberEntity.getRemainDays());
        memberDTO.setLocker(memberEntity.getLocker());
        memberDTO.setLocknum(memberEntity.getLocknum());
        memberDTO.setLockstart(memberEntity.getLockstart());
        memberDTO.setLockend(memberEntity.getLockend());
        memberDTO.setShirt(memberEntity.getShirt());
        memberDTO.setShirtstart(memberEntity.getShirtstart());
        memberDTO.setShirtend(memberEntity.getShirtend());
        memberDTO.setPrice(memberEntity.getPrice());
        memberDTO.setRestcount(memberEntity.getRestcount());
        return memberDTO;
    }

    private MembershipDTO convertMembershipEntityToDTO(MembershipEntity membershipEntity) {
        MembershipDTO membershipDTO = new MembershipDTO();
        membershipDTO.setId(membershipEntity.getId());
        membershipDTO.setMembership(membershipEntity.getMembership());
        membershipDTO.setMemstart(membershipEntity.getMemstart());
        membershipDTO.setMemend(membershipEntity.getMemend());
        membershipDTO.setCoach(membershipEntity.getCoach());
        membershipDTO.setPhone(membershipEntity.getPhone());
        membershipDTO.setAddress(membershipEntity.getAddress());
        membershipDTO.setGender(membershipEntity.getGender());
        membershipDTO.setShirt(membershipEntity.getShirt());
        membershipDTO.setShirtstart(membershipEntity.getShirtstart());
        membershipDTO.setShirtend(membershipEntity.getShirtend());
        membershipDTO.setCredit(membershipEntity.getCredit());
        membershipDTO.setPrice(membershipEntity.getPrice());
        membershipDTO.setCredit(membershipEntity.getCredit());


        return membershipDTO;
    }


    // MemberEntity를 MemberDTO로 변환하는 메소드
    private MemberDTO convert(MemberEntity memberEntity) {
        MemberDTO memberDTOs = new MemberDTO();
        memberDTOs.setId(memberEntity.getId());
        memberDTOs.setCoach(memberEntity.getCoach());
        memberDTOs.setName(memberEntity.getName());
        memberDTOs.setGender(memberEntity.getGender());
        memberDTOs.setPhone(memberEntity.getPhone());
        memberDTOs.setBirth(memberEntity.getBirth());
        memberDTOs.setAddress(memberEntity.getAddress());
        memberDTOs.setKakao(memberEntity.getKakao());
        memberDTOs.setPurpose(memberEntity.getPurpose());
        memberDTOs.setComein(memberEntity.getComein());
        memberDTOs.setMembership(memberEntity.getMembership());
        memberDTOs.setCredit(memberEntity.getCredit());
        memberDTOs.setMemstart(memberEntity.getMemstart());
        memberDTOs.setMemend(memberEntity.getMemend());
        memberDTOs.setRemainDays(memberEntity.getRemainDays());
        memberDTOs.setLocker(memberEntity.getLocker());
        memberDTOs.setLocknum(memberEntity.getLocknum());
        memberDTOs.setLockstart(memberEntity.getLockstart());
        memberDTOs.setLockend(memberEntity.getLockend());
        memberDTOs.setShirt(memberEntity.getShirt());
        memberDTOs.setShirtstart(memberEntity.getShirtstart());
        memberDTOs.setShirtend(memberEntity.getShirtend());


        // 기타 필요한 필드들 추가
        return memberDTOs;
    }


    //PT 횟수 차감
    public List<PTContractEntity> getAllContracts() {
        return ptContractRepository.findAll();
    }

    public PTContractEntity getPTContract(Long id) {
        return ptContractRepository.findById(id).orElseThrow(() -> new RuntimeException("Contract not found"));
    }


    public List<PTContractEntity> findByMemberName(String name) {
        return ptContractRepository.findByName(name);
    }

    public void delete(MemberEntity member) {
        memberRepository.delete(member);
    }

    public void updateCount(Long id) { //PT 버튼 차감 시간
        PTContractEntity contract = ptContractRepository.findById(id).orElseThrow();
        contract.setCount(contract.getCount() - 1);
        ptContractRepository.save(contract);

        // Save the decrement record with the current date
        DecrementRecord record = new DecrementRecord();
        record.setPtContractId(id);
        record.setDecrementDate(LocalDate.now());
        decrementRecordRepository.save(record);
    }

    public List<DecrementRecord> getDecrementRecords(Long id) {
        return decrementRecordRepository.findByPtContractId(id);
    }

    public PTContractEntity getContractById(Long id) {
        return ptContractRepository.findById(id).orElseThrow(() -> new RuntimeException("Contract not found"));
    }

   /* public PTContractEntity updateCount(Long id) { //original
        PTContractEntity ptContract = getPTContract(id);
        int currentCount = Integer.parseInt(ptContract.getCount());
        if (currentCount > 0) {
            ptContract.setCount(String.valueOf(currentCount - 1));
            ptContractRepository.save(ptContract);
        }
        return ptContract;
    }*/


    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Optional<MemberEntity> findById(Integer id) {
        return memberRepository.findById(id);
    }


    public List<PTContractEntity> findPendingApplication(Sort sort) {
        return ptContractRepository.findByStatusAndCoachNot("pending", "환불", sort);
    }

    public List<RestEntity> findPendingApplicationing(Sort sort) {
        return restRepository.findByStatus("pending", sort);
    }

    public List<TransferEntity> findPendingApplicationin(Sort sort) {
        return trasnferRepository.findByStatus("pending", sort);
    }
//
//    public List<MemberEntity> findStat(Sort sort) {
//        return memberRepository.findAllByStat("장기미출석자", sort);
//    }

    public Optional<PTContractEntity> findByIds(Long id) {
        return ptContractRepository.findById(id);
    }

    public Optional<RestEntity> findByIam(Long id) {
        return restRepository.findById(id);
    }

    public Optional<RestEntity> findByPhone(String phone) {
        return restRepository.findByPhone(phone);
    }

    public Optional<TransferEntity> findByIa(Long id) {
        return trasnferRepository.findById(id);
    }

    public void saves(PTContractEntity members) {
        ptContractRepository.save(members);
    }

    public void saveing(RestEntity membering) {
        restRepository.save(membering);
    }

    public void saver(TransferEntity membar) {
        trasnferRepository.save(membar);
    }

    // phone으로 회원 조회
    public Optional<MemberEntity> findByPhoneing(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public Optional<PTContractEntity> findByPhonel(String phone) {
        return ptContractRepository.findByPhone(phone);
    }

////////////////////환불처리 서비스
/*public void requestRefund(String name, String phone) {
    // 회원이 환불을 요청하면 해당 요청을 DB에 저장하거나 관리자가 볼 수 있도록 처리
    // 이 부분에서는 환불 요청에 대한 논리만 처리합니다.
    // 여기서는 간단히 로그를 출력하지만, 실무에서는 요청 DB에 기록하거나, 관리 페이지에 표시하는 로직을 추가해야 합니다.
    System.out.println("Refund requested for: " + name + ", " + phone);
}

    public void approveRefund(Long memberId) {
        // 회원 정보 조회
        MemberEntity memberEntity = (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with ID: " + memberId));

        // 해당 회원의 Membership 정보도 조회
        MembershipEntity membershipEntity = membershipRepository.findByPhone(memberEntity.getPhone())
                .orElseThrow(() -> new EntityNotFoundException("Membership not found with phone: " + memberEntity.getPhone()));

        // 회원 정보 및 Membership 정보 초기화
        memberEntity.setMemstart(null);
        memberEntity.setMemend(null);
        memberEntity.setLocker(String.valueOf(0));
        memberEntity.setLockstart(null);
        memberEntity.setLockend(null);
        memberEntity.setShirt(String.valueOf(0));
        memberEntity.setShirtstart(null);
        memberEntity.setShirtend(null);
        memberRepository.save(memberEntity);

        membershipEntity.setMemstart(null);
        membershipEntity.setMemend(null);
        membershipEntity.setLocker(String.valueOf(0));
        membershipEntity.setLockstart(null);
        membershipEntity.setLockend(null);
        membershipEntity.setShirt(String.valueOf(0));
        membershipEntity.setShirtstart(null);
        membershipEntity.setShirtend(null);
        membershipRepository.save(membershipEntity);

        // 환불이 승인되었다는 로그 출력
        System.out.println("Refund approved for: " + memberEntity.getName());
    }

*/

    public List<MemberEntity> findPendingRefundApplications(Sort sort) {
        return memberRepository.findByCoachAndStatus("환불", "pending", sort);
    }

    public List<MemberEntity> findApprovedRefundMembers(Sort sort) {
        return memberRepository.findAll(sort).stream()
                .filter(member -> member.getPrice() != null && member.getCredit() != null)
                .filter(member -> !member.getCredit().isEmpty()) // 계좌번호가 입력된 회원만 필터링
                .collect(Collectors.toList());
    }


    public List<MemberEntity> findNonRefundPendingApplications(Sort sort) {
        return memberRepository.findByCoachNotAndStatus("환불", "pending", sort);
    }

    public List<PTContractEntity> findNonRefundPendingApplication(Sort sort) {
        return ptContractRepository.findByCoachAndStatus("환불", "pending", sort);
    }


    public MemberDTO findByNamis(String username) {
        Optional<MemberEntity> optionalMember = memberRepository.findByName(username);
        return MemberDTO.fromOptional(optionalMember);
    }

    public void deleteBoard(Integer id) {
        boardRepository.deleteById(id);
    }

    public void Ptsaved(MemberEntity member) {
        if ("PM 1개월".equals(member.getMembership())) {
            PTContractEntity ptContract = new PTContractEntity();
            ptContract.setName(member.getName());
            ptContract.setGender(member.getGender());
            ptContract.setPhone(member.getPhone());
            ptContract.setBirth(member.getBirth());
            ptContract.setAddress(member.getAddress());
            ptContract.setKakao(member.getKakao());
            ptContract.setPurpose(member.getPurpose());
            ptContract.setPtmembership("PM 10회");
            ptContract.setPtstart(member.getMemstart());
            ptContract.setCount(10); // 정수로 변경
            ptContract.setCredit(member.getCredit());
            ptContract.setPrice(String.valueOf(member.getPrice()));
            ptContract.setStatus("approved");

            // PTContractEntity를 DB에 저장
            ptContractRepository.save(ptContract);
        }
    }


    // 단일 MemberEntity를 DTO로 변환
    public MemberDTO convertingDTO(MemberEntity memberEntity) {
        if (memberEntity == null) {
            return null;
        }

        // MemberEntity의 속성들을 MemberDTO에 매핑
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setCoach(memberEntity.getCoach());
        memberDTO.setName(memberEntity.getName());
        memberDTO.setGender(memberEntity.getGender());
        memberDTO.setPhone(memberEntity.getPhone());
        memberDTO.setBirth(memberEntity.getBirth());
        memberDTO.setAddress(memberEntity.getAddress());
        memberDTO.setKakao(memberEntity.getKakao());
        memberDTO.setPurpose(memberEntity.getPurpose());
        memberDTO.setComein(memberEntity.getComein());
        memberDTO.setMembership(memberEntity.getMembership());
        memberDTO.setCredit(memberEntity.getCredit());
        memberDTO.setMemstart(memberEntity.getMemstart());
        memberDTO.setMemend(memberEntity.getMemend());
        memberDTO.setRemainDays(memberEntity.getRemainDays());
        memberDTO.setLocker(memberEntity.getLocker());
        memberDTO.setLocknum(memberEntity.getLocknum());
        memberDTO.setLockstart(memberEntity.getLockstart());
        memberDTO.setLockend(memberEntity.getLockend());
        memberDTO.setShirt(memberEntity.getShirt());
        memberDTO.setShirtstart(memberEntity.getShirtstart());
        memberDTO.setShirtend(memberEntity.getShirtend());
        memberDTO.setStatus(memberEntity.getStatus());
        memberDTO.setApplicationDate(memberEntity.getApplicationDate());

        return memberDTO;
    }

    // 여러 MemberEntity를 DTO로 변환
    public List<MemberDTO> convertsDTO(List<MemberEntity> memberEntities) {
        return memberEntities.stream()
                .map(this::convertingDTO)
                .collect(Collectors.toList());
    }


//    // 이름으로 검색하면서 여러 멤버십 정보 포함하여 조회
//    public List<MemberDTO> findByNamingWithMemberships(String searchName) {
//        List<MemberEntity> members = memberRepository.findByNameContaining(searchName);
//
//        // MemberEntity를 MemberDTO로 변환하면서 각 회원의 여러 멤버십 정보를 포함
//        return members.stream().map(member -> {
//            MemberDTO memberDTO = convertDTO(member);
//            return memberDTO;
//        }).collect(Collectors.toList());
//    }
//
//    // 모든 회원과 멤버십 정보 조회
//    public List<MemberDTO> findAllWithMemberships() {
//        List<MemberEntity> members = memberRepository.findAll();
//
//        return members.stream().map(member -> {
//            MemberDTO memberDTO = convertvDTO(member);
//            return memberDTO;
//        }).collect(Collectors.toList());
//    }

    // MemberEntity를 MemberDTO로 변환하는 공통 메서드
    private MembershipDTO convertvDTO(MembershipEntity membership) {

        MembershipDTO membershipDTO = new MembershipDTO();
        membershipDTO.setId(membership.getId());
        membershipDTO.setName(membership.getName());
        membershipDTO.setMembership(membership.getMembership());
        membershipDTO.setMemstart(membership.getMemstart());
        membershipDTO.setPurpose(membership.getPurpose());
        membershipDTO.setComein(membership.getComein());
        membershipDTO.setMemend(membership.getMemend());
        membershipDTO.setCoach(membership.getCoach());
        membershipDTO.setPhone(membership.getPhone());
        membershipDTO.setAddress(membership.getAddress());
        membershipDTO.setGender(membership.getGender());
        membershipDTO.setShirt(membership.getShirt());
        membershipDTO.setShirtstart(membership.getShirtstart());
        membershipDTO.setShirtend(membership.getShirtend());
        membershipDTO.setCredit(membership.getCredit());
        membershipDTO.setPrice(membership.getPrice());
        membershipDTO.setCredit(membership.getCredit());
        membershipDTO.setTotalprice(membership.getTotalprice());
        membershipDTO.setBirth(membership.getBirth());
        membershipDTO.setKakao(membership.getKakao());
        membershipDTO.setLocker(membership.getLocker());
        membershipDTO.setLocknum(membership.getLocknum());
        membershipDTO.setLockstart(membership.getLockstart());
        membershipDTO.setLockend(membership.getLockend());
        membershipDTO.setContent(membership.getContent());
        membershipDTO.setMember_id(membership.getId());

        return membershipDTO;


    }

    public List<MembershipDTO> findMembershipByMemberId(Long memberId) {
        // memberId로 MemberEntity 조회
        MemberEntity member = (MemberEntity) memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        // 해당 회원의 MembershipEntity 목록 조회
        List<MembershipEntity> memberships = membershipRepository.findAllByMember(member);

        // DTO로 변환 후 반환
        return memberships.stream()
                .map(entity -> new MembershipDTO(
                        entity.getId(),
                        entity.getName(),
                        entity.getMember(),
                        entity.getMemstart(),
                        entity.getMemend(),
                        entity.getLocker(),
                        entity.getLocknum(),
                        entity.getLockstart(),
                        entity.getLockend(),
                        entity.getShirt(),
                        entity.getShirtstart(),
                        entity.getShirtend(),
                        entity.getPrice(),
                        entity.getTotalprice(),
                        entity.getContent()
                ))
                .collect(Collectors.toList());
    }


    // 스케줄러 서비스
    @Transactional
    public void updateAbsentMembers() {
        // 오늘 날짜로부터 1주일 전까지의 범위
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // 모든 회원 가져오기
        List<MemberEntity> allMembers = memberRepository.findAll();

        for (MemberEntity member : allMembers) {
            // 남은 일수가 음수인 경우, 해당 회원은 제외
            if (member.getRemainDays() <= 0) {
                continue; // 음수일 경우 처리하지 않음
            }
            // 해당 회원의 최근 1주일 내 출입 기록이 있는지 확인
            boolean hasRecentRecord = entryRecordRepository.existsByMemberAndEntryTimeAfter(member, oneWeekAgo);

            // 출입 기록이 없으면 stat을 '장기미출석자'로 업데이트
            if (!hasRecentRecord) {
                member.setStat("장기미출석자");
                memberRepository.save(member); // 변경된 stat 값 저장
            }
        }
    }


    public void updateLongTermAbsentMembersStatus() {
        LocalDate today = LocalDate.now();

        // "장기미출석자" 상태의 회원 가져오기
        List<MemberEntity> longTermAbsentMembers = memberRepository.findByStat("장기미출석자");

        for (MemberEntity member : longTermAbsentMembers) {
            // 오늘 출석한 기록이 있는지 확인
            boolean hasEntryToday = entryRecordRepository.existsByMemberAndEntryDate(member, today);

            // 남은 일수 확인 (remainDays > 0)
            boolean hasRemainingDays = member.getRemainDays() > 0;

            if (hasEntryToday || hasRemainingDays) {
                // 상태를 null로 변경
                member.setStat(null);
                memberRepository.save(member);
                System.out.println("회원의 상태를 null로 변경: " + member.getName());
            }
        }
    }


    // 회원권 종료일이 지난 회원들의 QR 코드를 삭제하는 로직
    public void removeExpiredQrCodes() {
        // 오늘 날짜
        LocalDate today = LocalDate.now();

        // 회원권 종료일이 오늘보다 이전인 회원 검색
        List<MemberEntity> expiredMembers = memberRepository.findByMemendBefore(today);

        // 해당 회원들의 QR코드를 삭제
        for (MemberEntity member : expiredMembers) {
            member.setQrCodePath(null);  // QR 코드 삭제
            memberRepository.save(member);  // 변경 사항 저장
        }

        System.out.println("QR 코드 삭제 완료: " + expiredMembers.size() + "명의 회원");
    }




    public List<MemberEntity> getLongAbsentMembers() {
        // "장기미출석자"인 회원만 조회하여 리턴
        return memberRepository.findByStat("장기미출석자");
    }
    public List<MemberEntity> getLastDay() {

        return memberRepository.findByRemainDays(Long.parseLong("7"));
    }


    // 남은 회원권이 있는 회원들에게 QR 코드 생성
    public void generateQRCodeForMembersWithActiveMemberships() {
        // 회원권이 남아있는 회원들을 조회
        List<MemberEntity> activeMembers = memberRepository.findMembersWithActiveMemberships();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        // 각 회원에 대해 QR 코드를 생성

        for (MemberEntity member : activeMembers) {
            LocalDate birthDateLocal = member.getBirth(); // assuming this is LocalDate
            String birthDate = (birthDateLocal != null) ? birthDateLocal.format(dateFormatter) : "생년월일 없음";
            String qrCodeText = "PQMA 출입증" +
                    "\n이름: " + member.getName() +
                    "\n전화번호: " + member.getPhone() +
                    "\n생년월일: " + birthDate; // 수정된 생년월일 사용
            // QR 코드를 파일로 저장할 경로 설정 (전화번호의 공백 제거)
            String qrCodePath = "src/main/resources/static/qrcode/" + member.getPhone().trim() + ".png"; // 전화번호를 사용하여 파일 이름 설정

            try {
                // QR 코드 생성
                qrService.generateQRCodeImage(qrCodeText, 300, 300, qrCodePath);
                member.setQrCodePath(qrCodePath); // 생성된 QR 코드 경로를 저장

                // 멤버 정보 업데이트
                memberRepository.save(member);
            } catch (WriterException | IOException e) {
                e.printStackTrace(); // 에러 처리
            }
        }
    }

    public boolean isMembershipValid(String phone) {
        // 전화번호로 회원을 조회한 후, 종료일이 오늘을 지나지 않았는지 확인
        Optional<MemberEntity> member = memberRepository.findByPhone(phone);
        if (member != null) {
            LocalDate today = LocalDate.now();
            LocalDate endDate = member.get().getMemend(); // 회원의 종료일을 가져옴
            return !endDate.isBefore(today); // 종료일이 오늘보다 이전이면 false
        }
        return false; // 회원이 없거나 유효하지 않으면 false
    }

//    // 새로운 인바디 기록 추가
//    public void addInbodyRecord(Long memberId) {
//        MemberEntity member = (MemberEntity) memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
//
//        // 마지막 회차 조회 및 다음 회차 계산
//        int lastCounter = inbodyRepository.findLastCounterByMemberId(memberId)
//                .orElse(0); // 인바디 기록이 없으면 0으로 초기화
//        int nextCounter = lastCounter + 1;
//
//        InbodyEntity newRecord = new InbodyEntity();
//        newRecord.setMember(member);
////        newRecord.setCounter(nextCounter);
//        newRecord.setRecordDate(LocalDate.now());
//
//        inbodyRepository.save(newRecord);
//    }
//
//    // 인바디 기록 조회
//    public List<InbodyEntity> getInbodyRecordsByMemberId(Long memberId) {
//        return inbodyRepository.findByMemberIdOrderByRecordDateDesc(memberId);
//    }

    public void writes(MemberEntity member, MultipartFile file) throws Exception {

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\profile";


        String fileName =  file.getOriginalFilename();

        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        member.setProfile(fileName);


        memberRepository.save(member);
    }
    public void updateMemberProfileImage(Long id, String fileName) {
        MemberEntity member = (MemberEntity) memberRepository.findById(id).orElseThrow();
        member.setProfileImage(fileName);
        memberRepository.save(member);
    }

    @Transactional
    public String insertMember(List<Map<String, Object>> memberData) {

        if (memberData == null || memberData.isEmpty()) {
            return "회원 데이터가 비어 있습니다.";
        }

        try {

            System.out.println("memberData: " + memberData);
            for (Map<String, Object> data : memberData) {
                String phone = data.get("phone").toString();
                String name = data.get("name").toString();
                System.out.println("여긴 들어와?");
                System.out.println("전화번호: " + phone);



                // 중복 체크: 전화번호에서 공백 및 하이픈을 제거하여 비교
                String normalizedPhone = phone.replaceAll("[^0-9]", "");

                Optional<MemberEntity> existingMember = memberRepository.findByPhone(normalizedPhone);

                if (existingMember.isPresent()) {
                    // 이미 존재하는 회원이 있으면 클라이언트에게 메시지 반환
                    System.out.println("입력된 전화번호: " + phone);
                    return "이미 존재하는 회원입니다: " + phone;
                } else {
                    // Optional이 비어 있으면 해당 전화번호가 없는 것으로 처리
                    System.out.println("입력된 전화번호: " + phone);


                    // MemberEntity 생성 및 데이터 설정
                    MemberEntity member = new MemberEntity();
                    // id 값을 구글 Sheets에서 가져온 값으로 설정
                    Integer id = safeParseInt(data.get("id").toString());
                    if (id != null) {
                        member.setId(id);  // 기존 id를 그대로 설정
                    }
                    log.info("회원 데이터 삽입 시도: {}", member);

                    member.setCoach(safeParseString(data.get("coach")));
                    member.setName(safeParseString(data.get("name")));
                    member.setGender(safeParseString(data.get("gender")));
                    member.setPhone(safeParseString(data.get("phone")));
                    member.setBirth((LocalDate) parseDate(data.get("birth"),false));
                    member.setAddress(safeParseString(data.get("address")));
                    member.setKakao(safeParseString(data.get("kakao")));
                    member.setPurpose(safeParseString(data.get("purpose")));
                    member.setComein(safeParseString(data.get("comein")));
                    member.setCredit(safeParseString(data.get("credit")));
                    member.setPrice(safeParseInt(data.get("price").toString()));
                    member.setMembership(safeParseString(data.get("membership")));


                    // 날짜 필드 처리 (빈 문자열을 null로 변환)
                    member.setMemstart((LocalDate) parseDate(data.get("memstart"), false));   // LocalDate
                    member.setMemend((LocalDate) parseDate(data.get("memend"), false));       // LocalDate
                    member.setRemainDays(Long.valueOf(data.get("remainDays").toString()));
                    member.setLocker(data.get("locker").toString());
                    member.setLocknum(safeParseInt(data.get("locknum").toString()));
                    member.setLockstart((LocalDate) parseDate(data.get("lockstart"), false)); // LocalDate
                    member.setLockend((LocalDate) parseDate(data.get("lockend"), false));     // LocalDate
                    member.setShirt(safeParseString(data.get("shirt")));
                    member.setShirtstart((LocalDate) parseDate(data.get("shirstart"), false)); // LocalDate
                    member.setShirtend((LocalDate) parseDate(data.get("shirtend"), false));   // LocalDate
                    member.setContent(safeParseString(data.get("content")));
                    member.setStatus(safeParseString(data.get("status")));
                    member.setApplicationDate((LocalDateTime) parseDate(data.get("applicationDate"), true)); // LocalDateTime
                    member.setSignature(safeParseString(data.get("signature")));
                    member.setStat(safeParseString(data.get("stat")));
                    member.setQrCodePath(safeParseString(data.get("qrCodePath")));
                    member.setLongTime(safeParseString(data.get("longTime")));
                    member.setRing(safeParseString(data.get("ring")));
                    member.setProfile(safeParseString(data.get("profile")));
                    member.setProfileImage(safeParseString(data.get("profileImage")));
                    member.setRestcount(safeParseInt(data.get("restcount").toString()));

                    // 새 회원 데이터베이스에 저장
                    memberRepository.save(member);
                    log.info("Saving member: {}", member.getName());

                    // 추가적으로 flush()를 호출하여 DB에 즉시 반영될 수 있도록 할 수 있음
                    memberRepository.flush();
                }
            }

            return "회원 데이터가 성공적으로 삽입되었습니다.";
        } catch (Exception e) {
            log.error("회원 데이터 삽입 중 에러 발생: ", e);
            return "회원 데이터 삽입 중 에러 발생: " + e.getMessage();
        }
    }

    // LocalDateTime과 LocalDate를 안전하게 파싱하는 메서드
    // LocalDateTime과 LocalDate를 안전하게 파싱하는 메서드
    private Object parseDate(Object dateObj, boolean isDateTime) {
        if (dateObj == null || dateObj.toString().isEmpty()) {
            return null;  // 빈 값이면 null 반환
        }

        try {
            // LocalDateTime 파싱 시도
            if (isDateTime) {
                return LocalDateTime.parse(dateObj.toString());
            } else {
                // LocalDate 파싱 시도
                return LocalDate.parse(dateObj.toString());
            }
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 실패: {}", dateObj);
            return null;  // 파싱 실패 시 null 반환
        }

    }
    // 빈 문자열을 안전하게 Integer로 변환하는 메서드
// 빈 문자열을 안전하게 Integer로 변환하는 메서드
    private Integer safeParseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;  // 빈 문자열 또는 null일 경우 null 반환
        }

        try {
            return Integer.parseInt(value);  // 정상적으로 숫자 변환
        } catch (NumberFormatException e) {
            log.error("숫자 파싱 실패: {}", value);
            return null;  // 파싱 실패 시 null 반환
        }
    }
    // 해당 값이 비어있을 경우, null로 처리하는 메서드
    private String safeParseString(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;  // 빈 문자열 또는 null일 경우 null 반환
        }
        return value.toString();  // 정상적으로 문자열 반환
    }





    // Null 또는 IndexOutOfBounds 예외를 방지하는 안전한 값 추출 메서드
    private String getSafeValue(List<Object> data, int index) {
        if (data == null || data.size() <= index || data.get(index) == null) {
            return "";
        }
        return data.get(index).toString();
    }

//    // 이름과 전화번호로 회원 검색
//    public MemberDTO findMemberByNameAndPhone(String name, String phone) {
//        // 데이터베이스에서 회원을 검색하는 로직
//        Optional<MemberEntity> memberOptional = memberRepository.findByNameAndPhone(name, phone);
//
//        // 회원이 존재하면 MemberDto로 변환하여 반환
//        if (memberOptional.isPresent()) {
//            MemberEntity member = memberOptional.get();
//            return new MemberDTO(member.getName(), member.getPhone(), member.getAddress());
//        }
//        return null; // 회원이 존재하지 않으면 null 반환
//    }
//    public void addNewMember(String name, String phone) {
//        MemberEntity newMember = new MemberEntity();
//        newMember.setName(name);
//        newMember.setPhone(phone);
//        // 다른 필요한 속성도 추가
//        memberRepository.save(newMember);
//    }

}


