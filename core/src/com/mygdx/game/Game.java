package com.mygdx.game;

import com.mygdx.character.Player;
import com.mygdx.character.Stat;
import com.mygdx.game.room.Room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
   private List<Room> rooms;

   private Player player;
   private int difficulty;
   private boolean isWin = false;

   public Game(List<Room> rooms,Player player,int difficulty){
      this.rooms = rooms;
      this.player = player;
      this.difficulty = difficulty;

   }
   public List<Room> getRooms() {
      return rooms;
   }

   public void setRooms(List<Room> rooms) {
      this.rooms = rooms;
   }

   public void setPlayer(Player player) {
      this.player = player;
   }

   public void setDifficulty(int difficulty) {
      this.difficulty = difficulty;
   }

   public boolean isWin() {
      return rooms.get(rooms.size()-1).getMonster().getStat().get(Stat.HP) <= 0;
   }

   public static Map<Stat, Integer> basicStat(int hp, int strength, int agility) {
      Map<Stat, Integer> stats = new HashMap<>();
      stats.put(Stat.HP, hp);
      stats.put(Stat.STRENGTH, strength);
      stats.put(Stat.AGILITY, agility);
      return stats;
   }

   public void setWin(boolean win) {
      isWin = win;
   }

   public Player getPlayer() {
      return player;
   }
   public int getDifficulty() {
      return difficulty;
   }

   public void play(){
   };
}