import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
public class reading {
    public static void main(String[] args) throws IOException {
        String lineNumber;
        String timeStamp;
        String speech;
        double totalSpeakingTime = 0;
        ArrayList<String> speakers = new ArrayList<>();
        int speakerSwitch = 0;
        ArrayList<Double> speakingTime = new ArrayList<>();
        ArrayList<String> currSpeaker = new ArrayList<>();
        String beforeName;
        String summary;
        String fileContents = readFile("data/DobervichPlanningSession1.vtt");
        String[] lines = fileContents.split("\n");
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }

        for (int i = 2; i < lines.length; i += 4) {
            lineNumber = lines[i];
            timeStamp = lines[i + 1];
            speech = lines[i + 2];
            Entry e = new Entry(lineNumber, timeStamp, speech);
            entries.add(e);
            totalSpeakingTime += e.duration(timeStamp);
            speakingTime.add(e.duration(timeStamp));
        }

        beforeName = entries.get(0).getSpeaker();
        writeDataToFile("summary.txt", beforeName + ":");

        for (int i = 0; i < entries.size(); i++) {
            if (isSpeakerGiven(entries.get(i).getSpeech())) {
                currSpeaker.add(beforeName);
                if (entries.get(i).getSpeaker().equals(beforeName)) {
                    summary = (entries.get(i).condenseSpeechFragments(entries.get(i).getSpeech()));
                    writeDataToFile("summary.txt", summary.trim());
                } else {
                    speakerSwitch++;
                    beforeName = entries.get(i).getSpeaker();
                    if (!speakers.contains(beforeName)) {
                        speakers.add(beforeName);
                    }
                    summary = beforeName + ":" + entries.get(i).condenseSpeechFragments(entries.get(i).getSpeech());
                    writeDataToFile("summary.txt", "\n");
                    writeDataToFile("summary.txt", summary);
                }
            } else {
                summary = entries.get(i).getSpeech();
                writeDataToFile("summary.txt", summary);
            }
        }

        writeDataToFile("meetingStats.txt", "Number of times speaker switched: " + speakerSwitch + "\n");
        double combinedSpeakingTime = Math.round(totalSpeakingTime * 100.0)/100.0;
        writeDataToFile("meetingStats.txt", "Total speaking time: " + combinedSpeakingTime + " minutes"+ "\n");
        writeDataToFile("meetingStats.txt", "Total length of session: " + Math.round(Entry.getTotalLength(lines[lines.length-2]) * 100.0)/100.0 + " minutes" + "\n");
        writeDataToFile("meetingStats.txt", "Number of unique speakers: " + speakers.size()+ "\n");
        writeDataToFile("meetingStats.txt", "\n");
        writeDataToFile("meetingStats.txt", "Total talk time" + "\n");
        for (int i = 0; i < speakers.size(); i++) {
            double talkTimePerSpeaker = Math.round((totalTalkTime(speakers.get(i), currSpeaker, speakingTime))*100.0)/100.0;
            writeDataToFile("meetingStats.txt", speakers.get(i) + ": " + talkTimePerSpeaker + " minutes - " + Math.round((talkTimePerSpeaker/combinedSpeakingTime) * 100.0) + "%" + "\n");

        }
        writeDataToFile("meetingStats.txt", "\n");
        writeDataToFile("meetingStats.txt", "Average length of a speech event" + "\n");
        for (int i = 0; i < speakers.size(); i++) {
            writeDataToFile("meetingStats.txt", speakers.get(i) + ": " + Math.round(averageSpeechEvent(speakers.get(i), currSpeaker, speakingTime) * 100.0)/100.0 + " minutes" + "\n");
        }

    }

    public static double averageSpeechEvent(String speaker, ArrayList<String> currSpeaker, ArrayList<Double> speakingTime) {
        double totalTime = totalTalkTime(speaker, currSpeaker, speakingTime);
        int timesSpoken = 0;
        for (int i = 0; i < currSpeaker.size(); i++) {
            if (currSpeaker.get(i).equals(speaker)) {
                timesSpoken++;
            }
        }
        return totalTime/timesSpoken;
    }

    public static double totalTalkTime(String speaker, ArrayList<String> currSpeaker, ArrayList<Double> speakingTime) {
        double totTime = 0;
        for (int i = 0; i < currSpeaker.size(); i++) {
                if (currSpeaker.get(i).equals(speaker)) {
                    totTime +=speakingTime.get(i);
                }
            }
        return totTime;
    }

    public static boolean isSpeakerGiven(String speech) {
        int loc = speech.indexOf(":");
        if (loc == -1) {
            return false;
        } else {
            return true;
        }
    }

    private static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append(System.getProperty("line.separator"));
                line = br.readLine();
            }
        } catch (Exception errorObj) {
            System.err.println("Couldn't read file: " + filePath);
            errorObj.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeDataToFile(String filePath, String data) throws IOException {
        try (FileWriter f = new FileWriter(filePath, true);
             BufferedWriter b = new BufferedWriter(f);
             PrintWriter writer = new PrintWriter(b);) {
            writer.print(data);
        } catch (IOException error) {
            System.err.println("There was a problem writing to the file: " + filePath);
            error.printStackTrace();
        }
    }
}