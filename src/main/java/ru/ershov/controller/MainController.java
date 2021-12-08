package ru.ershov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.ershov.Main;
import ru.ershov.dto.AnswerColumn;
import ru.ershov.dto.AnswerDevice;
import ru.ershov.dto.AnswerStep;
import ru.ershov.dto.NewSimulationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    Main main;

    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("newSimulation", new NewSimulationDto());
        return "main";
    }

    @PostMapping("/main")
    public String postMain(NewSimulationDto dto) {
        main = new Main();
        main.init(dto.getSource(), dto.getBuffer(), dto.getDevice(), dto.isStep());
        return "redirect:/" + (dto.isStep()? "step": "auto");
    }

    @GetMapping("/auto")
    public String auto(Model model) {
        main.run();
        List<AnswerColumn> run = main.printSource();
        model.addAttribute("columnsFirst", run);
        List<AnswerDevice> answerDevices = main.printDevice();
        model.addAttribute("columnsDevice", answerDevices);
        return "auto";
    }

    @GetMapping("/step")
    public String step(Model model) {
        Map<String, List<AnswerStep>> step = main.step();
        List<AnswerStep> source = step.get("source");
        model.addAttribute("sources", source);
        List<AnswerStep> device = step.get("device");
        model.addAttribute("devices", device);
        List<AnswerStep> buffer = step.get("buffer");
        model.addAttribute("buffers", buffer);
        model.addAttribute("refusal", step.get("ref"));
        System.out.println(buffer);
        return "step";
    }
}
