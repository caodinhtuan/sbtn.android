package onworld.sbtn.josonmodel.enums;

/**
 */
public enum HomeDataType {
    Banner(0),
    Header(1),
    BodyAudio(2),
    BodyVideo(3);


    private final int value;

    HomeDataType(int value) {
        this.value = value;
    }

    public static HomeDataType fromInt(int value) {
        switch (value) {
            case 0:
                return Banner;
            case 1:
                return Header;
            case 2:
                return BodyAudio;
            default:
                return BodyVideo;
        }
    }

    public int intValue() {
        return this.value;
    }
}
