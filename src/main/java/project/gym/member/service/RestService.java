package project.gym.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.gym.member.entity.RestEntity;
import project.gym.member.repository.MemberRepository;
import project.gym.member.repository.RestRepository;

import java.util.List;

@Service
public class RestService {

    @Autowired
    private static RestRepository restRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    public void PauseRequestService(RestRepository restRepository) {
        this.restRepository = restRepository;
    }

    public static void savePauseRequest(RestEntity restEntity) {
        restRepository.save(restEntity);
    }

    public List<RestEntity> findAll() {
        return restRepository.findAll();
    }
}
