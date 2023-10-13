package communication;

import DTO.ReadyDTO;
import DTO.UpdateBoardStateDTO;
import logic.TetrisField;
import logic.pieces.Tetromino;
import nakama.com.google.gson.Gson;
import nakama.com.google.gson.reflect.TypeToken;
import screens.LobbyWaitingScreen;
import screens.MainClass;
import screens.PlayOnlineScreen;

public enum MatchSendHelper {

    READY(1) {
        @Override
        public void sendUpdate(Object... o) {
            String playerid = (String) o[0];
            boolean readyState = (boolean) o[1];
            ReadyDTO payload = new ReadyDTO(playerid, readyState);
            String json = new Gson().toJson(payload, payload.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), READY.opcode, json.getBytes());
        }

        @Override
        public void receiveUpdate(String json) {
            ReadyDTO payload = new Gson().fromJson(json, new TypeToken<ReadyDTO>() {
            }.getType());
            LobbyWaitingScreen.updatePlayerState(payload.getPlayer_id(), payload.isReadyState());
        }
    },

    START(2) {
        @Override
        public void sendUpdate(Object... o) {
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), START.opcode, new byte[0]);
        }

        @Override
        public void receiveUpdate(String json) {

        }
    },
    SENDGARBAGE(3) {
        @Override
        public void sendUpdate(Object... o) {
            Integer lines = (int) o[0];
            String json = new Gson().toJson(lines, lines.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), SENDGARBAGE.opcode, json.getBytes());
        }

        @Override
        public void receiveUpdate(String json) {
            Integer lines = new Gson().fromJson(json, new TypeToken<Integer>() {
            }.getType());
            TetrisField.garbagePieceHandler.addGarbage(lines);
        }
    },
    LOOSE(4) {
        @Override
        public void sendUpdate(Object... o) {
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), LOOSE.opcode, new byte[0]);
        }

        @Override
        public void receiveUpdate(String json) {
            PlayOnlineScreen.win = true;
        }
    },
    UPDATEBOARD(5) {
        @Override
        public void sendUpdate(Object... o) {
            Tetromino tetromino = (Tetromino) o[0];
            int type = tetromino.getPieceId();
            int rotation = tetromino.getCurrentRotation();
            int x = tetromino.getX();
            int y = tetromino.getY();
            UpdateBoardStateDTO boardState = new UpdateBoardStateDTO(type, x, y, rotation);
            String json = new Gson().toJson(boardState, boardState.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), UPDATEBOARD.opcode, json.getBytes());
        }

        @Override
        public void receiveUpdate(String json) {
            UpdateBoardStateDTO boardState = new Gson().fromJson(json, new TypeToken<UpdateBoardStateDTO>() {
            }.getType());
            Tetromino t = boardState.getTetrominoFromBoardState();
            PlayOnlineScreen.opponentTetrisField.addPiece(t);
        }
    },

    GAMETICK(6) {
        @Override
        public void sendUpdate(Object... o) {
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), GAMETICK.opcode, new byte[0]);
        }

        @Override
        public void receiveUpdate(String json) {
            PlayOnlineScreen.gameTick();
        }
    };

    private int opcode;

    MatchSendHelper(int opcode) {
        this.opcode = opcode;
    }

    public abstract void sendUpdate(Object... o);

    public abstract void receiveUpdate(String json);

    public static void receiveUpdate(int opcode, String json) {
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
