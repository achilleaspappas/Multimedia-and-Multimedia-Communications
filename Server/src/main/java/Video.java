public class Video {

    /* The video object contains the full name of the file, the name of the video, the resolution and the format. */
    private String fullName;
    private String name;
    private Integer resolution;
    private String format;

    Video(String fullName, String name, Integer resolution, String format) {
        this.fullName = fullName;
        this.name = name;
        this.resolution = resolution;
        this.format = format;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public Integer getResolution() {
        return resolution;
    }

    public String getFormat() {
        return format;
    }
}
