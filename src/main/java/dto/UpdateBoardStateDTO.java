package dto;

import logic.pieces.*;
import lombok.Getter;

@Getter
public class UpdateBoardStateDTO {
    public final int opcode = 4;

    /**
     * This int stores which Piece got dropped
     * 0: I-Piece
     * 1: J-Piece
     * 2: L-Piece
     * 3: O-Piece
     * 4: S-Piece
     * 5: T-Piece
     * 6: Z-Piece
     */
    private final int droppedPiece;

    /**
     * This int stores at which x-location the Piece was dropped
     */
    private final int droppedX;

    /**
     * This int stores at which y-location the Piece was dropped
     */
    private final int droppedY;

    /**
     * This int stores the rotation of the piece when it was dropped
     */
    private final int rotation;

    public UpdateBoardStateDTO(int droppedPiece, int droppedX, int droppedY, int rotation) {
        this.droppedPiece = droppedPiece;
        this.droppedX = droppedX;
        this.droppedY = droppedY;
        this.rotation = rotation;
    }

    public Tetromino getTetrominoFromBoardState() {
        Tetromino t;
        switch (droppedPiece) {
            case 0:
                t = new IPiece(null);
                break;
            case 1:
                t = new JPiece(null);
                break;
            case 2:
                t = new LPiece(null);
                break;
            case 3:
                t = new OPiece(null);
                break;
            case 4:
                t = new SPiece(null);
                break;
            case 5:
                t = new TPiece(null);
                break;
            default:
                t = new ZPiece(null);
        }
        t.setX(droppedX);
        t.setY(droppedY);
        t.setCurrentRotation(rotation);
        return t;
    }
}

