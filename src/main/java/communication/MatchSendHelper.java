package communication;

import logic.OpponentTetrisField;
import logic.TetrisField;
import logic.pieces.Tetromino;
import nakama.com.google.gson.Gson;
import nakama.com.google.gson.reflect.TypeToken;
import screens.MainClass;
import screens.PlayOnlineScreen;

public enum MatchSendHelper {
    SENDGARBAGE {
        @Override
        public void sendUpdate(Object... o) {
            Integer lines = (int) o[0];
            String json = new Gson().toJson(lines, lines.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), 3, json.getBytes());
        }

        @Override
        public void receiveUpdate(String json) {
            Integer lines = new Gson().fromJson(json, new TypeToken<Integer>() {
            }.getType());
            TetrisField.garbagePieceHandler.addGarbage(lines);
        }
    },
    LOOSE {
        @Override
        public void sendUpdate(Object... o) {
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), 4, new byte[0]);
        }

        @Override
        public void receiveUpdate(String json) {
            PlayOnlineScreen.win = true;
        }
    },
    UPDATEBOARD {
        @Override
        public void sendUpdate(Object... o) {
            Tetromino tetromino = (Tetromino) o[0];
            int type = tetromino.getPieceId();
            int rotation = tetromino.getCurrentRotation();
            int x = tetromino.getX();
            int y = tetromino.getY();
            UpdateBoardState boardState = new UpdateBoardState(type, x, y, rotation);
            String json = new Gson().toJson(boardState, boardState.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), 5, json.getBytes());
        }

        @Override
        public void receiveUpdate(String json) {
            UpdateBoardState boardState = new Gson().fromJson(json, new TypeToken<UpdateBoardState>() {
            }.getType());
            Tetromino t = boardState.getTetrominoFromBoardState();
            OpponentTetrisField.addPiece(t);
        }
    },

    GAMETICK {
        @Override
        public void sendUpdate(Object... o) {

        }

        @Override
        public void receiveUpdate(String json) {

        }
    };


    public abstract void sendUpdate(Object... o);

    public abstract void receiveUpdate(String json);

    public void receiveUpdate(int opcode, String json) {
        switch (opcode) {
            case 3:
                SENDGARBAGE.receiveUpdate(json);
                break;
            case 4:
                LOOSE.receiveUpdate(json);
                break;
            case 5:
                UPDATEBOARD.receiveUpdate(json);
                break;
        }
    }

}
