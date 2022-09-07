package logic;

import logic.pieces.*;

import java.util.*;

public class RandomGenerator {
    public Queue<Tetromino> tetrisPieceOrder = new ArrayDeque<>();
    private final TetrisField field;

    public RandomGenerator(TetrisField field) {
        this.field = field;
        generateNewOrder();
    }

    private void generateNewOrder() {
        List<Tetromino> allTetrisPieces = new ArrayList<>();
        allTetrisPieces.add(new TPiece(field));
        allTetrisPieces.add(new IPiece(field));
        allTetrisPieces.add(new JPiece(field));
        allTetrisPieces.add(new LPiece(field));
        allTetrisPieces.add(new OPiece(field));
        allTetrisPieces.add(new SPiece(field));
        allTetrisPieces.add(new ZPiece(field));
        Collections.shuffle(allTetrisPieces);
        Collections.shuffle(allTetrisPieces);
        Collections.shuffle(allTetrisPieces);
        tetrisPieceOrder.addAll(allTetrisPieces);
        System.out.println(tetrisPieceOrder);
    }

    public Tetromino getNext() {
        Tetromino n = tetrisPieceOrder.poll();
        if (n == null) {
            generateNewOrder();
            n = tetrisPieceOrder.poll();
        }
        System.out.println(n);
        return n;
    }

}
