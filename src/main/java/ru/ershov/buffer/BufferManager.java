package ru.ershov.buffer;

import lombok.Getter;
import ru.ershov.Main;
import ru.ershov.device.DeviceManager;
import ru.ershov.source.SourceManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BufferManager {


    private int countRequest;
    private double allTime = 0;
    private int countBuffers;
    private final List<Buffer> buffers;
    private final Main main;

    public BufferManager(int countBuffers, Main main) {
        this.main = main;
        this.countBuffers = countBuffers;
        buffers = new ArrayList<>(countBuffers);
        for (int i = 0; i < countBuffers; i++) {
            buffers.add(new Buffer(i + 1, main));
        }
    }

    public double extracted(SourceManager.Request poll, SourceManager sources) {
        for (Buffer buffer : buffers) {
            if (buffer.isEmpty()) {
                buffer.add(poll, poll.getSourceNumber());
                countRequest++;
                break;
            }
        }
        if (!poll.isInBuffer()) {
            Buffer buffer = buffers.get(countBuffers - 1);

            SourceManager.Request req = buffer.getRequest();
            req.setInRefusal(true);

            allTime += buffer.delete();
            sources.markQueryOutput(buffer.getNumberSource() - 1);

            sources.setTimeBuffer(buffer.getNumberSource() - 1, buffers.get(countBuffers - 1).getTForSource());

            buffer.add(poll, poll.getSourceNumber());
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
            for (int i = 0; i < buffers.size(); i++) {
                if (buffers.get(i).getNumberSource() == numberSource
                        && buffers.get(i).getRequest().getNumber() < numberRequest
                        && buffers.get(i).getRequest().getNumber() != 0
                ) {
                    numberRequest = buffers.get(i).getRequest().getNumber();
                    req = buffers.get(i).getRequest();
                    numberBuffer = i;
                }
            }

            allTime += buffers.get(numberBuffer).delete();
            main.getSourceManager().setTimeBuffer(numberSource - 1, buffers.get(numberBuffer).getTForSource());

            main.setSystemTime(buffers.get(numberBuffer).getTimeOut());
            if (req != null) {
                main.getDeviceManager().setRequest(req, numberSource);
            }

            for (int i = numberBuffer; i < buffers.size() - 1; i++) {
                buffers.set(i, buffers.get(i + 1));
            }
            buffers.get(main.getCountBuffers() - 1).clear();
            buffers.get(main.getCountBuffers() - 1).setNumber(main.getCountBuffers());
        }
    }


}
