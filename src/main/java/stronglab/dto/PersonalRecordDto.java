package stronglab.dto;

import lombok.Data;

@Data
public class PersonalRecordDto {
    private String exerciseName;         // Название упражнения
    private double maxWeight;            // Максимальный вес
    private int maxReps;                 // Количество повторений при этом весе
    private String achievedDate;         // Дата достижения рекорда
}