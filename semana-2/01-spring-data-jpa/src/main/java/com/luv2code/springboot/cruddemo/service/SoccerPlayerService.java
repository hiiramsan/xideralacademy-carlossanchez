package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dto.SoccerPlayerDTO;
import com.luv2code.springboot.cruddemo.entity.SoccerPlayer;

import java.util.List;

public interface SoccerPlayerService {

    List<SoccerPlayerDTO> findAll();

    SoccerPlayer findById(int theId);

    SoccerPlayer save(SoccerPlayer theSoccerPlayer);

    void deleteById(int theId);

    SoccerPlayerDTO findDTOById(int id);

}
