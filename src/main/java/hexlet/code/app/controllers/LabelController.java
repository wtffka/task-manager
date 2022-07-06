package hexlet.code.app.controllers;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.impls.LabelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_LABEL_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.ID;

@RestController
@RequestMapping(BASE_URL_FOR_LABEL_CONTROLLER)
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelServiceImpl labelService;

    @GetMapping
    public List<Label> getLabels() {
        return labelRepository.findAll();
    }

    @GetMapping(ID)
    public Label getLabel(@PathVariable Long id) {
        return labelRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("Label with that ID not found"));
    }

    @PostMapping
    public Label createLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @PutMapping(ID)
    public Label updateLabel(@RequestBody @Valid LabelDto labelDto, @PathVariable Long id) {
        return labelService.updateLabel(labelDto, id);
    }

    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
