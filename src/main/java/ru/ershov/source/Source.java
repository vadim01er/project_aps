package ru.ershov.source;

import lombok.Getter;
import lombok.Setter;
import ru.ershov.dto.AnswerColumn;
import ru.ershov.dto.AnswerStep;

import java.util.Random;

@Getter
@Setter
public class Source {

    private int number;
    private double prevTime;               //время генерации предыдущей заявки
    private int countRequest = 0;          //количество заявок сгенерированных этим источником
    private int countRefusal = 0;          //количество заявок в отказе
    private double timeObc = 0.0;          //Время обслуживания заявок данного источника
    private double timeBuffer = 0.0;       //Время нахождения в буфере заявок данного источника
    private static Random random = new Random();

    private AnswerStep answerStep = new AnswerStep();

    public Source(int number) {
        prevTime = 0.0;
        this.number = number;
    }

    public void addTimeBuffer(double timeBuffer) {
        this.timeBuffer += timeBuffer;
    }

    public void addTimeObc(double timeObc) {
        this.timeObc += Math.abs(timeObc);
    }

    public SourceManager.Request generate() {
        double timeGenerated = prevTime + (double) random.nextInt(50) / 10 + 5;

        countRequest++;
        SourceManager.Request request = new SourceManager.Request(timeGenerated, countRequest, number);
        prevTime = timeGenerated;

        System.out.printf("И  | %1$10s | %2$6.1f | %3$20s | %4$5d | %5$5d %n",
                request.getName(), timeGenerated, "генерация и ожидание", countRequest, countRefusal);
        answerStep = new AnswerStep(String.valueOf(number), request.getName(), timeGenerated, "генерация и ожидание", countRequest, countRefusal);
        return request;
    }

    public AnswerColumn printAnswer() {

        double srTOb = timeObc / countRequest;
        double srTBP = timeBuffer / countRequest;

        System.out.printf(" %1$7s | %2$13d | %3$8.3f | %4$12.3f | %5$10.3f | %6$14.3f %n",
                number, countRequest-1, (double) countRefusal / countRequest, srTBP + srTOb, srTBP, srTOb);
        return new AnswerColumn(number, countRequest - 1, (double) countRefusal / countRequest, srTBP + srTOb, srTBP, srTOb);
    }

}
