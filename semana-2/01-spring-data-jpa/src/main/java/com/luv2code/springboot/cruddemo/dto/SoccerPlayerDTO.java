package com.luv2code.springboot.cruddemo.dto;

import com.luv2code.springboot.cruddemo.entity.SoccerPlayer;

public class SoccerPlayerDTO {

    private int id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String position;
    private String nationality;

    private String team;

    public SoccerPlayerDTO(SoccerPlayer soccerPlayer) {
        id = soccerPlayer.getId();
        firstName = soccerPlayer.getFirstName();
        lastName = soccerPlayer.getLastName();
        dateOfBirth = soccerPlayer.getDateOfBirth();
        position = soccerPlayer.getPosition();
        nationality = soccerPlayer.getNationality();
        team = soccerPlayer.getTeam() != null
                ? soccerPlayer.getTeam().getName()
                : null;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPosition() {
        return position;
    }

    public String getNationality() {
        return nationality;
    }

    public String getTeam() {
        return team;
    }
}
