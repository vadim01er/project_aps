package ru.ershov.device;

import lombok.Getter;
import lombok.Setter;
import ru.ershov.Main;
import ru.ershov.dto.AnswerDevice;
import ru.ershov.dto.AnswerStep;
import ru.ershov.source.SourceManager;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DeviceManager {

    private int countRequest = 0;  //количество заявок обработанных всеми приборами
    private double allTime;       //время обработки всех заявок всеми приборами
    private int lastDevice;
    private final ArrayList<Device> devices;
    private final Main main;
    AnswerStep[] answerSteps;


    public DeviceManager(int countDevices, Main main) {
        this.main = main;
        devices = new ArrayList<>(countDevices);
        answerSteps = new AnswerStep[countDevices];
        for (int i = 0; i < countDevices; i++) {
            devices.add(new Device(i + 1, main));
        }
    }

    public double checkFreeDevice() {
        Device device = devices.get(0);
        for(Device d : devices){
            if(!d.isEmpty()) {
                device = d;
                break;
            }
        }

        double minTr = device.getTimeToTreatment();
        int numberDevice = device.getNumber();
        for (Device d : devices) {
            if (d.getTimeToTreatment() < minTr && !d.isEmpty()) {
                minTr = d.getTimeToTreatment();
                numberDevice = d.getNumber();
            }
        }

        double systemTime = -1;

        if (minTr < main.getSourceManager().peek().getTimeGenerated()) {
            systemTime = minTr;
            int numberSource = device.getNumberSource();
            sumAllTime(devices.get(numberDevice - 1).delete());
            answerSteps[numberDevice - 1] = devices.get(numberDevice - 1).getAnswerStep();
            countRequest++;
            main.getSourceManager().setTimeService(numberSource - 1, devices.get(numberDevice - 1).getTForSource());
        }
        return systemTime;
    }

    public boolean findLastDevice() {
        boolean isEmptyAnyone = false;

        for (int i = lastDevice; i < devices.size() + lastDevice; i++) {
            if (devices.get(i % devices.size()).isEmpty()) {
                isEmptyAnyone = true;
                lastDevice = devices.get(i % devices.size()).getNumber();
                break;
            }
        }
        return isEmptyAnyone;
    }

    public void setRequest(SourceManager.Request req, int numberSource) {
        devices.get(lastDevice - 1).add(req, numberSource);
        answerSteps[lastDevice - 1] = devices.get(lastDevice - 1).getAnswerStep();
        main.setSystemTime(devices.get(lastDevice - 1).getTimeAdd()); //Фиксируем время поступления заявки на прибор
    }

    public void sumAllTime(double time) {
        allTime += time;
    }

    public List<AnswerDevice> print() {
        List<AnswerDevice> answerDevices = new ArrayList<>();
        for (Device d : devices) {
            answerDevices.add(d.printAnswer());
        }
        return answerDevices;
    }
}
