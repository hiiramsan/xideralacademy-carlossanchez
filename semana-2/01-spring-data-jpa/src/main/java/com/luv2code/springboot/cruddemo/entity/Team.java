package com.luv2code.springboot.cruddemo.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="stadium")
    private String stadium;

    @OneToMany(mappedBy = "team")
    private List<SoccerPlayer> players = new ArrayList<>();

    public Team() {
    }

    public Team(String name, String stadium) {
        this.name = name;
        this.stadium = stadium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public List<SoccerPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<SoccerPlayer> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stadium='" + stadium + '\'' +
                '}';
    }

}