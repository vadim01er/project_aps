package ru.ershov.device;

import lombok.Getter;
import lombok.Setter;
import ru.ershov.Main;
import ru.ershov.dto.AnswerDevice;
import ru.ershov.dto.AnswerStep;
import ru.ershov.source.SourceManager;

@Getter
@Setter
class Device {

    private static double lambda = Main.lambda;
    private final Main main;

    private SourceManager.Request request;     //заявка, которая находится на приборе
    private int number;                 //номер прибора
    private double timeEmpty;           //время простоя (сумма timeAdd текущей заявки - timeOut предыдущей заявки
    private double timeAdd;             //время поступения заявки
    private double timeOut;             //время ухода из прибора
    private double timeInDevice;        //время обработки заявок этим прибором
    private double timeToTreatment;     //время, которое нужно на обработку
    private double tForSource;
    private int numberSource;
    private int countRequestThis;

    private AnswerStep answerStep = new AnswerStep();

    public Device(int number, Main main){
        this.number = number;
        this.main = main;
    }

    public void add(SourceManager.Request request, int numberSource){
        this.request = request;
        this.numberSource = numberSource;
        request.setInDevice(true);
        timeAdd = main.getSystemTime();
        timeEmpty = timeAdd - timeOut;

        System.out.printf("ПА | %1$10s | %2$6.1f | %3$20s | %4$5s | %5$5s %n",
                number + " : " + request.getName(), timeAdd, "поступление в прибор", "-", "-");
        answerStep = new AnswerStep("ПА", number + " : " + request.getName(), timeAdd, "поступление в прибор", -1, -1);
        timeToTreatment = timeAdd + Math.log(Math.random() * 1000 / 10) / (lambda);
    }

    public double delete() {
        timeOut = main.getSystemTime();
        timeInDevice += timeOut - timeAdd;
        double allTime = timeOut - timeAdd;
        tForSource = timeOut - timeAdd;
        countRequestThis++;
        request.setInDevice(false);

        System.out.printf("ПO | %1$10s | %2$6.1f | %3$20s | %4$5s | %5$5s %n",
                number + " : " + request.getName(), timeOut, "удаление из буффера", "-", "-");
        answerStep = new AnswerStep("ПO", number + " : " + request.getName(), timeAdd, "удаление из буффера", -1, -1);
        request = null;
        numberSource = 0;
        return allTime;
    }

    public boolean isEmpty() {
        return request == null;
    }

    public AnswerDevice printAnswer() {
        System.out.printf(" %1$5s | %2$19.5f %n",
                number, timeInDevice / main.getSystemTime());

        return new AnswerDevice(number, timeInDevice / main.getSystemTime());
    }
}
