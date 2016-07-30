package onworld.sbtn.josonmodel.enums;

/**
 */
public enum HomeType {

    Audio(0),
    Video(1);

    private final int value;

    HomeType(int value) {
        this.value = value;
    }

    public static HomeType fromInt(int value) {
        switch (value) {
            case 0:
                return Audio;
            default:
                return Video;
        }
    }

    public int intValue() {
        return this.value;
    }
}
