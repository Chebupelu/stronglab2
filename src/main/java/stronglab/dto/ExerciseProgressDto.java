package stronglab.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExerciseProgressDto {
    private String exerciseName;         // Название упражнения
    private LocalDate date;              // Дата тренировки
    private double weight;               // Вес на этой тренировке
    private int reps;                    // Количество повторений
    private int sets;                    // Количество подходов
}
