package logic;

import screens.Screen;

import java.awt.event.KeyEvent;

public enum Key {
    ARROW_LEFT(KeyEvent.VK_LEFT, 10) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.moveLeft();
            }
            if (this.counter == this.interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    ARROW_RIGHT(KeyEvent.VK_RIGHT, 10) {
        @Override
        public void handleKeyInput(TetrisField field) {
            if (this.counter == 0) {
                field.moveRight();
            }
            if (this.counter == this.interval) {
                counter = 0;
            } else {
                counter++;
            }
        }
    },
    ARROW_UP(KeyEvent.VK_UP, 25) {
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
    ARROW_DOWN(KeyEvent.VK_DOWN, 10) {
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
    SPACE(KeyEvent.VK_SPACE, 100) {
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
    SHIFT(KeyEvent.VK_SHIFT, 100) {
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
    CONTROL(KeyEvent.VK_CONTROL, 500) {
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
    };


    int keycode;
    int interval;
    int counter;

    Key(int keycode, int interval) {
        this.keycode = keycode;
        this.interval = interval;
        counter = 0;
    }

    Key() {
        counter = 0;
    }

    public abstract void handleKeyInput(TetrisField field);

    public void resetKey() {
        this.counter = 0;
    }

    public static Key getEnumFromKeyCode(int keycode) {
        switch (keycode) {
            case KeyEvent.VK_LEFT -> {
                return ARROW_LEFT;
            }
            case KeyEvent.VK_RIGHT -> {
                return ARROW_RIGHT;
            }
            case KeyEvent.VK_DOWN -> {
                return ARROW_DOWN;
            }
            case KeyEvent.VK_UP -> {
                return ARROW_UP;
            }
            case KeyEvent.VK_SPACE -> {
                return SPACE;
            }
            case KeyEvent.VK_SHIFT -> {
                return SHIFT;
            }
            case KeyEvent.VK_CONTROL -> {
                return CONTROL;
            }

        }
        return null;
    }


}
