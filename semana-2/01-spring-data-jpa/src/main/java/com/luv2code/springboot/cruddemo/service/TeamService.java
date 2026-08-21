package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.entity.Team;

import java.util.List;

public interface TeamService {
    List<Team> findAll();

    Team findById(int id);

    Team save(Team team);

    void deleteById(int id);
}
