public class Entry {
    private String num;
    private String timeStamp;
    private String speech;

    public Entry(String num, String timeStamp, String speech) {
        this.num = num;
        this.timeStamp = timeStamp;
        this.speech = speech;
    }

    public static double duration(String timeStamp) {
        double startTime = timeConvert(timeStamp.substring(0, timeStamp.indexOf("-")));
        double endTime = timeConvert(timeStamp.substring(timeStamp.indexOf(">")+1));
        return endTime - startTime;
    }

    public static double timeConvert(String timeStamp) {
        int firstColon = timeStamp.indexOf(":");
        int secondColon = timeStamp.indexOf(":", firstColon+1);
        double hour = Double.parseDouble(timeStamp.substring(0, firstColon)) * 60;
        double minute = Double.parseDouble(timeStamp.substring(firstColon+1, secondColon));
        double second = Double.parseDouble(timeStamp.substring(secondColon+1))/60;
        return hour + minute + second;
    }

    public static double getTotalLength(String lastTimeStamp) {
        double endTime = timeConvert(lastTimeStamp.substring(lastTimeStamp.indexOf(">")+1));
        return endTime;
    }

    public String getSpeaker() {
        return this.speech.substring(0, this.speech.indexOf(":"));
    }

    public static String condenseSpeechFragments(String speech) {
        int colon = speech.indexOf(":");
        return speech.substring(colon+1);
    }

    public String getSpeech() {
        return this.speech;
    }

    public String toString() {
        return speech;
    }
}