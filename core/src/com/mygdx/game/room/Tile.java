package com.mygdx.game.room;

import com.mygdx.interfaces.TileDisplay;

import java.util.Objects;

public class Tile {
    private static int id = 0;
    private int x;
    private int y;
    private boolean isDoor;

    private TileDisplay tileDisplay;



    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", isDoor=" + isDoor +
                ", tileDisplay='" + tileDisplay + '\'' +
                '}';
    }

    public Tile(int x, int y, boolean isDoor, TileDisplay tileDisplay) {
        Tile.id = id + 1;
        this.x = x;
        this.y = y;
        this.isDoor = isDoor;
        this.tileDisplay = tileDisplay;
        if (isDoor) {
            this.tileDisplay.setTexturePath("allTextures/closedDoor.png");
        }
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        Tile.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isDoor() {
        return isDoor;
    }

    public void setDoor(boolean door) {
        isDoor = door;
    }

    public String getPathToAsset() {
        return tileDisplay.getTexturePath();
    }
    public boolean isNeighbor(Room room, Tile otherTile){
        return (((this.x == otherTile.getX() && this.y == otherTile.getY() + room.getRelativeHeight()) ||
                (this.x == otherTile.getX() && this.y == otherTile.getY() - room.getRelativeHeight()) ||
                (this.y == otherTile.getY() && this.x == otherTile.getX() + room.getRelativeWidth() ) ||
                (this.y == otherTile.getY() && this.x == otherTile.getX() - room.getRelativeWidth())) && !otherTile.getTileDisplay().isBorder()) ;
    }

    public boolean isInTile(Room room, int x, int y){
//        return UtilsFunc.isClickIn(this.x, room.getRelativeWidth(),x,y);
        return (this.x <= x && this.x + room.getRelativeWidth() >= x) && (this.y <= y && this.y + room.getRelativeHeight() >= y);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    public TileDisplay getTileDisplay() {
        return tileDisplay;
    }

    public void setTileDisplay(TileDisplay tileDisplay) {
        this.tileDisplay = tileDisplay;
    }


}
