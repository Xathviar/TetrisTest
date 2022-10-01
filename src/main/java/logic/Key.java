package logic;

import java.awt.event.KeyEvent;

@SuppressWarnings("SpellCheckingInspection")
public enum Key {
    MOVELEFT(5) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.moveLeft();
            }
            handleLogic(this,25);
        }
    },
    MOVERIGHT(5) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.moveRight();
            }
            handleLogic(this, 25);
        }
    },
    ROTATECLOCKWISE(100) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.rotateClockwise();
            }
            if (this.counter == this.interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    SOFTDROP(10) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.softDrop();
            }
            if (this.counter == this.interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    HARDDROP(100) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.hardDrop();
            }
            if (this.counter == interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    HOLD(100) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.swapHold();
            }
            if (this.counter == interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    ROTATECOUNTERCLOCKWISE(100) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.rotateCClockwise();
            }
            if (this.counter == interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    NOOP(10000) {
        @Override
        public void handleKeyInput(TetrisField field) {
        }
    };


    final int interval;
    boolean firstTimePressed;
    int counter;

    Key(int interval) {
        this.interval = interval;
        counter = 0;
        firstTimePressed = true;
    }

    public abstract void handleKeyInput(TetrisField field);

    public void resetKey() {
        this.counter = 0;
        this.firstTimePressed = true;
    }

    public static Key getEnumFromKeyCode(int keycode) {
        switch (keycode) {
            case KeyEvent.VK_LEFT -> {
                return MOVELEFT;
            }
            case KeyEvent.VK_RIGHT -> {
                return MOVERIGHT;
            }
            case KeyEvent.VK_DOWN -> {
                return SOFTDROP;
            }
            case KeyEvent.VK_UP -> {
                return ROTATECLOCKWISE;
            }
            case KeyEvent.VK_SPACE -> {
                return HARDDROP;
            }
            case KeyEvent.VK_SHIFT -> {
                return HOLD;
            }
            case KeyEvent.VK_CONTROL -> {
                return ROTATECOUNTERCLOCKWISE;
            }

        }
        return NOOP;
    }

    private static void handleLogic(Key key,int initialDelay) {
        if (key.firstTimePressed) {
            if (key.counter == initialDelay) {
                key.counter = 0;
                key.firstTimePressed = false;
            } else {
                key.counter++;
            }
        } else {
            if (key.counter == key.interval) {
                key.counter = 0;
            } else {
                key.counter++;
            }
        }
    }


}
