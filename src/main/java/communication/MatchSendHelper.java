package communication;

import DTO.GarbageDTO;
import DTO.ReadyDTO;
import DTO.UpdateBoardStateDTO;
import logic.TetrisField;
import logic.pieces.Tetromino;
import lombok.extern.slf4j.Slf4j;
import nakama.com.google.gson.Gson;
import nakama.com.google.gson.reflect.TypeToken;
import screens.LobbyWaitingScreen;
import screens.MainClass;
import screens.PlayOnlineScreen;

@Slf4j
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

    // TODO Implement Starting the Game Logic
    // TODO Counting down from three?
    START(2) {
        @Override
        public void sendUpdate(Object... o) {
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), START.opcode, new byte[0]);
        }

        @Override
        public void receiveUpdate(String json) {
            LobbyWaitingScreen.startGame();
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
    },
    UPDATEGARBAGE(7) {
        @Override
        public void sendUpdate(Object... o) {
            int lines = (int) o[0];
            int garbageGap = (int) o[1];
            GarbageDTO packet = new GarbageDTO(lines, garbageGap);
            String json = new Gson().toJson(packet, packet.getClass());
            MainClass.aClass.socket.sendMatchData(MainClass.aClass.match.getMatchId(), UPDATEGARBAGE.opcode, json.getBytes());

        }

        @Override
        public void receiveUpdate(String json) {
            GarbageDTO received = new Gson().fromJson(json, new TypeToken<GarbageDTO>() {
            }.getType());
            int lines = received.getGarbageHeight();
            int garbageGap = received.getGarbageGap();
            PlayOnlineScreen.opponentTetrisField.addGarbage(lines, garbageGap);
        }
    };

    private final int opcode;

    MatchSendHelper(int opcode) {
        this.opcode = opcode;
    }

    public abstract void sendUpdate(Object... o);

    public abstract void receiveUpdate(String json);

    public static void receiveUpdate(int opcode, String json) {
        log.debug(String.format("Received Opcode %d with Message: \n%s", opcode, json));
        switch (opcode) {
            case 1:
                READY.receiveUpdate(json);
                break;
            case 2:
                START.receiveUpdate(json);
                break;
            case 3:
                SENDGARBAGE.receiveUpdate(json);
                break;
            case 4:
                LOOSE.receiveUpdate(json);
                break;
            case 5:
                UPDATEBOARD.receiveUpdate(json);
                break;
            case 6:
                GAMETICK.receiveUpdate(json);
                break;
            case 7:
                UPDATEGARBAGE.receiveUpdate(json);
        }
    }

}
