package com.luv2code.springboot.cruddemo.dao;

import com.luv2code.springboot.cruddemo.entity.SoccerPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoccerPlayerRepository extends JpaRepository<SoccerPlayer, Integer> {

    // that's it ... no need to write any code LOL!

}
