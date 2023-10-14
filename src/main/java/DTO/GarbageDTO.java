package DTO;

import com.googlecode.lanterna.TextColor;
import logic.OpponentTetrisField;
import logic.Point;
import lombok.Getter;

@Getter
public class GarbageDTO {

    private final int garbageHeight;
    private final int garbageGap;

    public GarbageDTO(int garbageHeight, int garbageGap) {
        this.garbageHeight = garbageHeight;
        this.garbageGap = garbageGap;
    }

}
