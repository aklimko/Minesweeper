package pl.adamklimko.minesweeper;

enum Level {
    EASY(1),
    INTERMEDIATE(2),
    EXPERT(3);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Level getLevelFromInt(int level) {
        switch (level) {
            case 1:
                return EASY;
            case 2:
                return INTERMEDIATE;
            case 3:
                return EXPERT;
            default:
                return null;
        }
    }
}
