package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dao.SoccerPlayerRepository;
import com.luv2code.springboot.cruddemo.dto.SoccerPlayerDTO;
import com.luv2code.springboot.cruddemo.entity.SoccerPlayer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SoccerPlayerServiceImpl implements SoccerPlayerService {

    private SoccerPlayerRepository soccerPlayerRepository;

    @Autowired
    public SoccerPlayerServiceImpl(SoccerPlayerRepository theSoccerPlayerRepository) {
        soccerPlayerRepository = theSoccerPlayerRepository;
    }

    @Override
    public List<SoccerPlayerDTO> findAll() {

        return soccerPlayerRepository.findAll().stream()
                .map(SoccerPlayerDTO::new)
                .toList();

    }

    @Override
    public SoccerPlayerDTO findDTOById(int id) {
        return new SoccerPlayerDTO(findById(id));
    }


    @Override
    public SoccerPlayer findById(int id) {
        Optional<SoccerPlayer> result = soccerPlayerRepository.findById(id);

        SoccerPlayer theSoccerPlayer = null;

        if (result.isPresent()) {
            theSoccerPlayer = result.get();
        }
        else {
            throw new RuntimeException("Did not find soccer player id - " + id);
        }

        return theSoccerPlayer;
    }

    @Override
    public SoccerPlayer save(SoccerPlayer soccerPlayer) {
        return soccerPlayerRepository.save(soccerPlayer);
    }

    @Override
    public void deleteById(int id) {
        soccerPlayerRepository.deleteById(id);
    }
}






