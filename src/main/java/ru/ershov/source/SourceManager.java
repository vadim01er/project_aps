package ru.ershov.source;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.ershov.Main;
import ru.ershov.dto.AnswerColumn;
import ru.ershov.dto.AnswerStep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

@Getter
@Setter
public class SourceManager {

    private int countAllRequest = 0;
    private final Main main;
    List<Source> sources;
    Queue<Request> requestList;
    AnswerStep[] answerSteps;

    public SourceManager(int countSources, Main main) {
        this.main = main;
        sources = new ArrayList<>(countSources);
        answerSteps = new AnswerStep[countSources];
        for (int i = 0; i < countSources; i++) {
            sources.add(new Source(i + 1));
        }

        requestList = new PriorityQueue<>(Comparator.comparingDouble(Request::getTimeGenerated));
        for (Source s : sources)
            requestList.add(s.generate());
    }

    public void incrementCountAllRequest() {
        countAllRequest++;
    }

    public Request poll() {
        return requestList.poll();
    }

    public void markQueryOutput(int i) {
        Source source = sources.get(i);
        source.setCountRefusal(source.getCountRefusal() + 1);
    }

    public void setTimeBuffer(int i, double timeForSource) {
        sources.get(i).addTimeBuffer(timeForSource);
    }

    public void generate(int i) {
        requestList.add(sources.get(i).generate());
        answerSteps[i] = sources.get(i).getAnswerStep();
    }

    public Request peek() {
        return requestList.peek();
    }

    public void setTimeService(int i, double tForSource) {
        sources.get(i).addTimeObc(tForSource); // прибавление, а не замена
    }

    public List<AnswerColumn> print() {
        List<AnswerColumn> answerColumns = new ArrayList<>();
        for (Source s : sources) {
            answerColumns.add(s.printAnswer());
        }
        return answerColumns;
    }

    @Getter
    @Setter
    @ToString
    public static class Request {
        private static int countRefusal = 0;  //количество заявок, ушедших в отказ со всех источников
        private static int count = 0;         //количество всех заявок из этого источника

        private boolean inBuffer = false;     //сначал считаем, что все заявки попадают сначала в буфер, а потом на прибор
        private boolean inDevice = false;     //находится ли заявка на приборе
        private boolean inRefusal = false;    //ушла ли заявка в отказ

        private double timeGenerated;         //время гинерации заявки
        private int number;                   //номер заявки
        private int sourceNumber;

        public Request(double timeGenerated, int number, int sourceNumber) {
            this.timeGenerated = timeGenerated;
            this.number = number;
            this.sourceNumber = sourceNumber;
            count++;
        }

        public String getName() {
            return sourceNumber + "." + number;
        }
    }
}
