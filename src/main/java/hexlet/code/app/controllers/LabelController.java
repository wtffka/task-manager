package hexlet.code.app.controllers;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.service.impls.LabelServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get list of all Labels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all Labels", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized")
    })
    public List<Label> getLabels() {
        return labelRepository.findAll();
    }

    @GetMapping(ID)
    @Operation(summary = "Get Label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "404", description = "Label not found")
    })
    public Label getLabel(@Parameter(description = "Label Id to show") @PathVariable Long id) {
        return labelRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("Label with that ID not found"));
    }

    @PostMapping
    @Operation(summary = "Create new Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label created", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "422", description = "Data validation failed")
    })
    public Label createLabel(@Parameter(description = "Data to create Label") @RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @PutMapping(ID)
    @Operation(summary = "Update Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label updated", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "404", description = "Label not found"),
            @ApiResponse(responseCode = "422", description = "Data validation failed")
    })
    public Label updateLabel(@Parameter(description = "Data to update Label") @RequestBody @Valid LabelDto labelDto,
                             @Parameter(description = "Label Id to update") @PathVariable Long id) {
        return labelService.updateLabel(labelDto, id);
    }

    @DeleteMapping(ID)
    @Operation(summary = "Delete Label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label deleted", content =
            @Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))),
            @ApiResponse(responseCode = "401", description = "User is unauthorized"),
            @ApiResponse(responseCode = "404", description = "Label not found"),
            @ApiResponse(responseCode = "422", description = "Can't delete label with existing task(s)"),
    })
    public void deleteLabel(@Parameter(description = "Label Id to delete") @PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
