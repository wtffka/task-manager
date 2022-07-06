package hexlet.code.app.controllerTests;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.testUtils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;


import static hexlet.code.app.testUtils.Utils.toJson;
import static hexlet.code.app.utils.AppConstants.BASE_URL_FOR_LABEL_CONTROLLER;
import static hexlet.code.app.utils.AppConstants.ID;
import static hexlet.code.app.utils.AppConstants.TEST_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class TestLabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private Utils utils;

    private static final LabelDto TEST_LABEL_DTO = new LabelDto("BUG");
    private static final LabelDto TEST_LABEL_DTO_2 = new LabelDto("NOT BUG");

    @BeforeEach
    void init() throws Exception {
        utils.regDefaultUser();
    }


    @Test
    public void testGetLabel() throws Exception {
        utils.regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletResponse response = utils.perform(
                get(BASE_URL_FOR_LABEL_CONTROLLER + ID, expectedLabel.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(expectedLabel.getName());
        assertThat(response.getContentAsString()).contains(String.valueOf(expectedLabel.getId()));
    }

    @Test
    public void testGetLabels() throws Exception {
        utils.regLabelPositive(TEST_LABEL_DTO);
        utils.regLabelPositive(TEST_LABEL_DTO_2);

        final Label expectedLabel1 = labelRepository.findAll().get(0);
        final Label expectedLabel2 = labelRepository.findAll().get(1);

        final MockHttpServletResponse response = utils.perform(
                        get(BASE_URL_FOR_LABEL_CONTROLLER), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(expectedLabel1.getName());
        assertThat(response.getContentAsString()).contains(expectedLabel2.getName());
        assertThat(response.getContentAsString()).contains(String.valueOf(expectedLabel1.getId()));
        assertThat(response.getContentAsString()).contains(String.valueOf(expectedLabel2.getId()));

    }

    @Test
    public void testCreateLabelPositive() throws Exception {
        assertThat(labelRepository.count()).isEqualTo(0);
        utils.regLabelPositive(TEST_LABEL_DTO);
        assertThat(labelRepository.count()).isEqualTo(1);
    }

    @Test
    public void testCreateLabelNegative() throws Exception {
        assertThat(labelRepository.count()).isEqualTo(0);
        utils.regLabelNegative(TEST_LABEL_DTO);
        assertThat(labelRepository.count()).isEqualTo(0);
    }

    @Test
    public void testUpdateLabelPositive() throws Exception {
        utils.regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(BASE_URL_FOR_LABEL_CONTROLLER + ID, expectedLabel.getId())
                .content(toJson(TEST_LABEL_DTO_2))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(request, TEST_USERNAME);

        final Label updatedLabel = labelRepository.findAll().get(0);

        assertThat(updatedLabel.getName()).isEqualTo(TEST_LABEL_DTO_2.getName());
        assertThat(updatedLabel.getId()).isEqualTo(expectedLabel.getId());
        assertThat(updatedLabel.getCreatedAt()).isEqualTo(expectedLabel.getCreatedAt());
    }

    @Test
    public void testUpdateLabelNegative() throws Exception {
        utils.regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put(BASE_URL_FOR_LABEL_CONTROLLER + ID, expectedLabel.getId())
                .content(toJson(TEST_LABEL_DTO_2))
                .contentType(MediaType.APPLICATION_JSON);

        utils.perform(request);

        final Label updatedLabel = labelRepository.findAll().get(0);

        assertThat(updatedLabel.getName()).isEqualTo(TEST_LABEL_DTO.getName());
        assertThat(updatedLabel.getId()).isEqualTo(expectedLabel.getId());
        assertThat(updatedLabel.getCreatedAt()).isEqualTo(expectedLabel.getCreatedAt());
    }

    @Test
    public void testDeleteLabelPositive() throws Exception {
        assertThat(labelRepository.count()).isEqualTo(0);
        utils.regLabelPositive(TEST_LABEL_DTO);
        assertThat(labelRepository.count()).isEqualTo(1);

        final Label labelToDelete = labelRepository.findAll().get(0);

        final Long idLabelToDelete = labelToDelete.getId();

        utils.perform(delete(BASE_URL_FOR_LABEL_CONTROLLER + ID, idLabelToDelete), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(labelRepository.count()).isEqualTo(0);

    }
}
