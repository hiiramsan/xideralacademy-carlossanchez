package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dao.TeamRepository;
import com.luv2code.springboot.cruddemo.entity.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {
    private TeamRepository teamRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository theTeamRepository) {
        teamRepository = theTeamRepository;
    }

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public Team findById(int id) {
        Optional<Team> result = teamRepository.findById(id);

        Team theTeam = null;

        if (result.isPresent()) {
            theTeam = result.get();
        }
        else {
            throw new RuntimeException("Did not find team id - " + id);
        }

        return theTeam;
    }

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public void deleteById(int id) {
        teamRepository.deleteById(id);
    }
}
