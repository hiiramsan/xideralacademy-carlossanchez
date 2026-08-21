package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.entity.Team;
import com.luv2code.springboot.cruddemo.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TeamRestController {
    private TeamService teamService;

    private JsonMapper jsonMapper;

    @Autowired
    public TeamRestController(TeamService theTeamService, JsonMapper theJsonMapper) {
        teamService = theTeamService;
        jsonMapper = theJsonMapper;
    }

    @GetMapping("/teams")
    public List<Team> findAll() {
        return teamService.findAll();
    }

    @GetMapping("/teams/{teamId}")
    public Team getTeam(@PathVariable int teamId) {

        Team theTeam = teamService.findById(teamId);

        if (theTeam == null) {
            throw new RuntimeException("Team id not found - " + teamId);
        }

        return theTeam;
    }

    @PostMapping("/teams")
    public Team addTeam(@RequestBody Team theTeam) {

        theTeam.setId(0);

        Team dbTeam = teamService.save(theTeam);

        return dbTeam;
    }

    @PutMapping("/teams/{teamId}")
    public Team updateTeam(@PathVariable int teamId, @RequestBody Team theTeam) {

        Team tempTeam = teamService.findById(teamId);

        if (tempTeam == null) {
            throw new RuntimeException("Team id not found - " + teamId);
        }

        tempTeam.setName(theTeam.getName());
        tempTeam.setStadium(theTeam.getStadium());

        Team dbTeam = teamService.save(tempTeam);

        return dbTeam;
    }


    @DeleteMapping("/teams/{teamId}")
    public String deleteTeam(@PathVariable int teamId) {

        Team tempTeam = teamService.findById(teamId);

        if (tempTeam == null) {
            throw new RuntimeException("Team id not found - " + teamId);
        }

        teamService.deleteById(teamId);

        return "Deleted team id - " + teamId;
    }
}
