package DTO;

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
