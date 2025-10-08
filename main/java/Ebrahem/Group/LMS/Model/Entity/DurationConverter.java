package Ebrahem.Group.LMS.Model.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(Duration duration) {
        // نخزن القيمة بصيغة معيارية وآمنة
        return duration != null ? duration.toString() : null; // مثال: PT3H43M
    }

    @Override
    public Duration convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            // لو القيمة بصيغة معيارية (PT3H43M)
            return Duration.parse(value);
        } catch (Exception e) {
            // دعم صيغ بشرية زي "3:43" أو "3h 43m"
            String v = value.trim().toLowerCase();
            long hours = 0;
            long minutes = 0;

            if (v.contains(":")) {
                String[] parts = v.split(":");
                hours = Long.parseLong(parts[0].trim());
                minutes = Long.parseLong(parts[1].trim());
            } else if (v.contains("h")) {
                String[] parts = v.split("h");
                hours = Long.parseLong(parts[0].trim());
                if (parts.length > 1) {
                    String m = parts[1].replace("m", "").trim();
                    if (!m.isEmpty()) minutes = Long.parseLong(m);
                }
            } else if (v.endsWith("m")) {
                minutes = Long.parseLong(v.replace("m", "").trim());
            }

            return Duration.ofHours(hours).plusMinutes(minutes);
        }
    }
}
