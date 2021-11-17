package ru.ershov.buffer;

import lombok.Getter;
import lombok.Setter;
import ru.ershov.Main;
import ru.ershov.source.SourceManager;

@Getter
@Setter
class Buffer {

    private SourceManager.Request request;       //заявка в буфере
    private int numberSource;             //номер источника из которого появилась заявка (нужно для приоритета)
    private double timeAdd;               //время заявки в момент поступления;
    private double timeOut;              //воемя выхода заявки из буфера;
    private double tForSource;
    private int number;                  //номер буфера
    private Main main;

    public Buffer(int number, Main main){
        this.number = number;
        this.main = main;
    }

    public void add(SourceManager.Request request, int numberSource){
        this.request = request;
        this.numberSource = numberSource;
        timeAdd = main.getSystemTime();
        request.setInBuffer(true);

        System.out.printf("БA | %1$10s | %2$6.1f | %3$20s | %4$5s | %5$5s %n",
                number + " : " + request.getName(), timeAdd, "поступлние в буффер", "-", "-");
    }

    public boolean isEmpty() {
        return request == null;
    }

    public double delete() {
        System.out.println("    Заявка " + numberSource + "." + request.getNumber() + " удалена из буфера № " + number);

        request.setInBuffer(false);
        timeOut = main.getSystemTime();
        double allTime = timeOut - timeAdd;
        tForSource = timeOut - timeAdd;

        System.out.printf("БO | %1$10s | %2$6.1f | %3$20s | %4$5s | %5$5s %n",
                number + " : " + request.getName(), tForSource, "удаление из буффера", "-", "-");
        request = null;
        return allTime;
    }

    public void clear(){
        request = null;
        numberSource = 0;
        timeAdd = 0;
        timeOut = 0;
        tForSource = 0;
        number = 0;
    }
}
