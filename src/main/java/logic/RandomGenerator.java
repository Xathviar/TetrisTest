package logic;

import logic.pieces.*;

import java.util.*;

public class RandomGenerator {
    public final Queue<Tetromino> tetrisPieceOrder = new ArrayDeque<>();
    private final TetrisField field;

    private final List<Tetromino> allTetrisPieces = new ArrayList<>();

    public RandomGenerator(TetrisField field) {
        this.field = field;
        populateArrayList();
        generateNewOrder();
    }

    private void populateArrayList() {
        allTetrisPieces.add(new TPiece(field));
        allTetrisPieces.add(new IPiece(field));
        allTetrisPieces.add(new JPiece(field));
        allTetrisPieces.add(new LPiece(field));
        allTetrisPieces.add(new OPiece(field));
        allTetrisPieces.add(new SPiece(field));
        allTetrisPieces.add(new ZPiece(field));
    }

    private void generateNewOrder() {
        Collections.shuffle(allTetrisPieces);
        tetrisPieceOrder.addAll(allTetrisPieces);
    }

    public Tetromino getNext() {
        if (tetrisPieceOrder.size() < 4) {
            generateNewOrder();
        }
        return tetrisPieceOrder.poll();
    }

    public List<Tetromino> peek(int number) {
        while (number > tetrisPieceOrder.size()) {
            generateNewOrder();
        }

        List<Tetromino> ret = new ArrayList<>();
        Object[] tetrisPieceOrderArr = tetrisPieceOrder.toArray();
        for (int i = 0; i < number; i++) {
            ret.add((Tetromino) tetrisPieceOrderArr[i]);
        }
        return ret;
    }

}
