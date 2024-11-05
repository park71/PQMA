package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gym.member.dto.EntryRecordDTO;
import project.gym.member.entity.EntryRecordEntity;
import project.gym.member.entity.MemberEntity;
import project.gym.member.repository.EntryRecordRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryRecordService {

    @Autowired
    private EntryRecordRepository entryrecordRepository;



    public void saveEntryRecord(MemberEntity member) {
        EntryRecordEntity entryRecord = new EntryRecordEntity(member, LocalDateTime.now(), member.getName());
        entryrecordRepository.save(entryRecord);
    }
    public List<EntryRecordDTO> findByMemberName(String name) {
        List<EntryRecordEntity> entryRecords = entryrecordRepository.findByMemberName(name);
        List<EntryRecordDTO> entryRecordDTOs = new ArrayList<>();
        for (EntryRecordEntity entryRecord : entryRecords) {
            EntryRecordDTO entryRecordDTO = new EntryRecordDTO();
            entryRecordDTO.setEntryTime(entryRecord.getEntryTime());
            entryRecordDTO.setName(entryRecord.getName());
            entryRecordDTOs.add(entryRecordDTO);
        }
        return entryRecordDTOs;
    }
    public List<EntryRecordDTO> findByMemberId(Long memberId) {
        List<EntryRecordEntity> entryRecords = entryrecordRepository.findByMemberId(memberId);
        return entryRecords.stream()
                .map(record -> new EntryRecordDTO(record))  // EntryRecordDTO로 변환하는 로직
                .collect(Collectors.toList());
    }


        public List<EntryRecordDTO> getEntriesByDate(LocalDate date) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
            List<EntryRecordEntity> entries = entryrecordRepository.findByEntryTimeBetween(startOfDay, endOfDay);

            return entries.stream()
                    .map(record -> new EntryRecordDTO(record))  // EntryRecordDTO로 변환
                    .collect(Collectors.toList());
        }
}
