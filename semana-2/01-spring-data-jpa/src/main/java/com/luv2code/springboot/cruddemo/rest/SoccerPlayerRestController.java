package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.dto.SoccerPlayerDTO;
import tools.jackson.databind.json.JsonMapper;
import com.luv2code.springboot.cruddemo.entity.SoccerPlayer;
import com.luv2code.springboot.cruddemo.service.SoccerPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SoccerPlayerRestController {

    private SoccerPlayerService soccerPlayerService;

    private JsonMapper jsonMapper;

    @Autowired
    public SoccerPlayerRestController(SoccerPlayerService theSoccerPlayerService, JsonMapper theJsonMapper) {
        soccerPlayerService = theSoccerPlayerService;
        jsonMapper = theJsonMapper;
    }

    @GetMapping("/soccer-players")
    public List<SoccerPlayerDTO> findAll() {
        return soccerPlayerService.findAll();
    }

    @GetMapping("/soccer-players/{playerId}")
    public SoccerPlayerDTO getSoccerPlayer(@PathVariable int playerId) {

        SoccerPlayerDTO theSoccerPlayer = soccerPlayerService.findDTOById(playerId);

        if (theSoccerPlayer == null) {
            throw new RuntimeException("Soccer player id not found - " + playerId);
        }

        return theSoccerPlayer;
    }

    @PostMapping("/soccer-players")
    public SoccerPlayer addSoccerPlayer(@RequestBody SoccerPlayer theSoccerPlayer) {

        theSoccerPlayer.setId(0);

        SoccerPlayer dbSoccerPlayer = soccerPlayerService.save(theSoccerPlayer);

        return dbSoccerPlayer;
    }


    @PutMapping("/soccer-players")
    public SoccerPlayer updateSoccerPlayer(@RequestBody SoccerPlayer theSoccerPlayer) {

        SoccerPlayer dbSoccerPlayer = soccerPlayerService.save(theSoccerPlayer);

        return dbSoccerPlayer;
    }


    @PatchMapping("/soccer-players/{playerId}")
    public SoccerPlayer patchSoccerPlayer(@PathVariable int playerId,
                                          @RequestBody Map<String, Object> patchPayload) {

        SoccerPlayer tempSoccerPlayer = soccerPlayerService.findById(playerId);

        if (tempSoccerPlayer == null) {
            throw new RuntimeException("Soccer player id not found - " + playerId);
        }

        if (patchPayload.containsKey("id")) {
            throw new RuntimeException(
                    "Soccer player id cannot be modified. Remove 'id' from request body.");
        }

        SoccerPlayer patchedSoccerPlayer = jsonMapper.updateValue(tempSoccerPlayer, patchPayload);

        SoccerPlayer dbSoccerPlayer = soccerPlayerService.save(patchedSoccerPlayer);

        return dbSoccerPlayer;
    }


    @DeleteMapping("/soccer-players/{playerId}")
    public String deleteSoccerPlayer(@PathVariable int playerId) {

        SoccerPlayer tempSoccerPlayer = soccerPlayerService.findById(playerId);


        if (tempSoccerPlayer == null) {
            throw new RuntimeException("Soccer player id not found - " + playerId);
        }

        soccerPlayerService.deleteById(playerId);

        return "Deleted soccer player id - " + playerId;
    }

}
