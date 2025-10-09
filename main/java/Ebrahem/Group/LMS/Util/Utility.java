package Ebrahem.Group.LMS.Util;



import org.springframework.stereotype.Component;

import java.time.Duration;
@Component
public class Utility {

    public Duration parseDuration(String formatted) {
        if (formatted == null || formatted.isBlank()) return Duration.ZERO;
        formatted = formatted.trim().toLowerCase();

        long hours = 0;
        long minutes = 0;

        if (formatted.contains(":")) {
            String[] parts = formatted.split(":");
            hours = Long.parseLong(parts[0].trim());
            minutes = Long.parseLong(parts[1].trim());
        } else if (formatted.contains("h")) {
            String[] parts = formatted.split("h");
            hours = Long.parseLong(parts[0].trim());
            if (parts.length > 1) {
                String m = parts[1].replace("m", "").trim();
                if (!m.isEmpty()) minutes = Long.parseLong(m);
            }
        } else if (formatted.endsWith("m")) {
            minutes = Long.parseLong(formatted.replace("m", "").trim());
        }

        return Duration.ofHours(hours).plusMinutes(minutes);
    }
    public String formatDuration(Duration duration) {
        if (duration == null) return "00h 00m";
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return String.format("%02dh %02dm", hours, minutes);
    }

}