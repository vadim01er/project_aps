package ru.ershov;

import lombok.Getter;
import lombok.Setter;
import ru.ershov.buffer.BufferManager;
import ru.ershov.device.DeviceManager;
import ru.ershov.dto.AnswerColumn;
import ru.ershov.dto.AnswerDevice;
import ru.ershov.dto.AnswerStep;
import ru.ershov.source.SourceManager;

import java.util.*;

@Getter
@Setter
public class Main {
    private double systemTime = 0.0;      //системное время моделирования
    private double alpha = 1.0;           //параметр alpha для времени генерации заявки
    private double beta = 1.3;            //параметр beta для времени генерации заявки
    public static double lambda = 0.4;        //параметр lambda для экспоненциального закона распределения обработки прибора
    private int countSources = 3;             //количество источников в системе
    public int countBuffers = 2;             //количество буферов в системе
    private int countDevices = 2;             //количество приборов в системе
    public int countRequests = 10000;            //количество моделируемых заявок
    private boolean step = false;
    private SourceManager sourceManager;
    private DeviceManager deviceManager;
    private BufferManager bufferManager;

    public static void main(String[] args) {
        new Main().run();
    }

    public void init(int source, int buffer, int device, boolean isStep) {
        countSources = source;
        countBuffers = buffer;
        countDevices = device;
        step = isStep;

        sourceManager = new SourceManager(countSources, this);

        bufferManager = new BufferManager(countBuffers, this);

        deviceManager = new DeviceManager(countDevices, this);
    }

    public void run() {
//        System.out.print("Выбор режима: a - автоматический, p - пошаговый: ");
//        String ch = scanner.nextLine();
//        if (ch.equals("p"))
//            step = true;
//        System.out.print("Введите количество источников: ");
//        countSources = scanner.nextInt();
//        System.out.print("Введите количество буферов: ");
//        countBuffers = scanner.nextInt();
//        System.out.print("Введите количество приборов: ");
//        countDevices = scanner.nextInt();
//        System.out.print("Введите количество моделируемых заявок: ");
//        countRequests = scanner.nextInt();

        while (sourceManager.getCountAllRequest() != countRequests) {
            extracted();
        }

    }

    public Map<String, List<AnswerStep>> step() {
        if (sourceManager.getCountAllRequest() != countRequests) {
            HashMap<String, AnswerStep[]> extracted = extracted();
            HashMap<String, List<AnswerStep>> stringListHashMap = new HashMap<>();
            for (Map.Entry<String, AnswerStep[]> stringEntry : extracted.entrySet()) {
                ArrayList<AnswerStep> answerSteps = new ArrayList<>(Arrays.asList(stringEntry.getValue()));
                stringListHashMap.put(stringEntry.getKey(), answerSteps);
            }
            return stringListHashMap;
        }
        return new HashMap<>();
    }

    private HashMap<String, AnswerStep[]> extracted() {
        SourceManager.Request poll = sourceManager.poll(); //Удаляем самую раннюю сгенерированную заявку

        systemTime = poll.getTimeGenerated();

        double extracted = bufferManager.extracted(poll, sourceManager);
        setSystemTime(extracted);

        boolean isEmptyAnyone = getDeviceManager().findLastDevice(); //Проверяем, есть ли свободный прибор
        //Если нашли свободный прибор, то кидаем туда выбранную из буфера заявку, если свободных приборов нет, то ничего не делаем
        if (isEmptyAnyone) {
            //Проверяем есть ли у нас заявки в буферах, если есть, то кидаем их на приборы, если нет, то ничего не делаем
            getBufferManager().addToDevice();
        }
        double time = getDeviceManager().checkFreeDevice();
        setSystemTime(time);

        sourceManager.incrementCountAllRequest();
        sourceManager.generate(poll.getSourceNumber() - 1); //Добавляем новую заявку из того же источника
        HashMap<String, AnswerStep[]> stringHashMap = new HashMap<>();
        stringHashMap.put("source", sourceManager.getAnswerSteps());
        stringHashMap.put("device", deviceManager.getAnswerSteps());
        stringHashMap.put("buffer", bufferManager.getAnswerSteps());
        AnswerStep[] answerSteps = new AnswerStep[1];
        answerSteps[0] = bufferManager.getRef();
        stringHashMap.put("ref", answerSteps);
        return stringHashMap;
    }

    public void setSystemTime(double time) {
        systemTime = time == -1 ? systemTime : time;

    }

    public List<AnswerColumn> printSource() {
        System.out.println("\n\n\nРезультаты");
        System.out.println("Devices " + countDevices);
        System.out.println("Buffers " + countBuffers);
        System.out.println(" Lambda " + lambda);
        System.out.println();
        System.out.println("Источник | Кол-во заявок | P отказа | Т пребывания | Т ожидания | Т обслуживания");

        return sourceManager.print();

//        System.out.println();
//        System.out.println("Прибор | Коэф. использования");
//
//        deviceManager.print();

    }

    public List<AnswerDevice> printDevice() {

        System.out.println();
        System.out.println("Прибор | Коэф. использования");

        return deviceManager.print();
    }

}
