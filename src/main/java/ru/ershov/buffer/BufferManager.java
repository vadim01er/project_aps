package ru.ershov.buffer;

import lombok.Getter;
import ru.ershov.Main;
import ru.ershov.dto.AnswerStep;
import ru.ershov.source.SourceManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BufferManager {


    private int countRequest;
    private double allTime = 0;
    private int countBuffers;
    private final Buffer[] buffers;
    private final Main main;

    AnswerStep[] answerSteps;

    AnswerStep ref;

    public BufferManager(int countBuffers, Main main) {
        this.main = main;
        this.countBuffers = countBuffers;
        buffers = new Buffer[countBuffers];
        answerSteps = new AnswerStep[countBuffers];
        for (int i = 0; i < countBuffers; i++) {
            buffers[i] = new Buffer(i + 1, main);
        }
        System.out.println(buffers.length);
    }

    public double extracted(SourceManager.Request poll, SourceManager sources) {
        for (int i = 0; i < buffers.length; i++) {
            Buffer buffer = buffers[i];
            System.out.println(i);
            if (buffer.isEmpty()) {
                System.out.println("find " + buffer.getNumber());
                buffer.add(poll, poll.getSourceNumber());
                answerSteps[i] = buffer.getAnswerStep();
                countRequest++;
                break;
            }
        }
        if (!poll.isInBuffer()) {
            Buffer buffer = buffers[countBuffers - 1];

            SourceManager.Request req = buffer.getRequest();
            req.setInRefusal(true);
            ref = new AnswerStep();
            ref.setNameSource(req.getName());
            allTime += buffer.delete();
            answerSteps[countBuffers - 1] = buffer.getAnswerStep();
            sources.markQueryOutput(buffer.getNumberSource() - 1);

            sources.setTimeBuffer(buffer.getNumberSource() - 1, buffers[countBuffers - 1].getTForSource());

            buffer.add(poll, poll.getSourceNumber());
            answerSteps[countBuffers - 1] = buffer.getAnswerStep();
            countRequest++;
            return buffer.getTimeAdd();
        }
        return -1;
    }

    public void addToDevice() {
        boolean isBuffersEmpty = true;
        for (Buffer buffer : buffers) {
            if (!buffer.isEmpty())
                isBuffersEmpty = false;
        }

        if (!isBuffersEmpty) {
            SourceManager.Request req = null;
            int numberSource = 0;
            double maxTimeGenerated = 0;
            int numberRequest = main.getCountRequests();

            for (Buffer buffer : buffers) {
                if (!buffer.isEmpty()
                        && buffer.getRequest().getTimeGenerated() > maxTimeGenerated
                        && buffer.getNumberSource() != 0
                ) {
                    numberSource = buffer.getNumberSource();
                    maxTimeGenerated = buffer.getRequest().getTimeGenerated();
                }
            }

            int numberBuffer = 0;
            for (int i = 0; i < buffers.length; i++) {
                if (buffers[i].getNumberSource() == numberSource
                        && buffers[i].getRequest().getNumber() < numberRequest
                        && buffers[i].getRequest().getNumber() != 0
                ) {
                    numberRequest = buffers[i].getRequest().getNumber();
                    req = buffers[i].getRequest();
                    numberBuffer = i;
                }
            }

            allTime += buffers[numberBuffer].delete();
            answerSteps[numberBuffer] = buffers[numberBuffer].getAnswerStep();
            main.getSourceManager().setTimeBuffer(numberSource - 1, buffers[numberBuffer].getTForSource());

            main.setSystemTime(buffers[numberBuffer].getTimeOut());
            if (req != null) {
                main.getDeviceManager().setRequest(req, numberSource);
            }

//            for (int i = numberBuffer; i < buffers.length - 1; i++) {
//                buffers[i] = buffers[i + 1];
//            }
            buffers[main.getCountBuffers() - 1].clear();
//            buffers.get(main.getCountBuffers() - 1).setNumber(main.getCountBuffers());
        }
    }


}
