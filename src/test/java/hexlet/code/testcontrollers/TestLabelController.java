package hexlet.code.testcontrollers;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.utils.AppConstants;
import hexlet.code.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import static hexlet.code.utils.Utils.toJson;
import static hexlet.code.utils.AppConstants.TEST_USERNAME;
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

    private final LabelRepository labelRepository;

    private final Utils utils;

    @Autowired
    public TestLabelController(LabelRepository labelRepository, Utils utils) {
        this.labelRepository = labelRepository;
        this.utils = utils;
    }

    private static final LabelDto TEST_LABEL_DTO = new LabelDto("BUG");
    private static final LabelDto TEST_LABEL_DTO_2 = new LabelDto("NOT BUG");

    @BeforeEach
    void init() throws Exception {
        utils.regDefaultUser();
    }


    @Test
    public void testGetLabel() throws Exception {
        regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletResponse response = utils.perform(
                get("/api/labels/{id}", expectedLabel.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsString()).contains(expectedLabel.getName());
        assertThat(response.getContentAsString()).contains(String.valueOf(expectedLabel.getId()));
    }

    @Test
    public void testGetLabels() throws Exception {
        regLabelPositive(TEST_LABEL_DTO);
        regLabelPositive(TEST_LABEL_DTO_2);

        final Label expectedLabel1 = labelRepository.findAll().get(0);
        final Label expectedLabel2 = labelRepository.findAll().get(1);

        final MockHttpServletResponse response = utils.perform(
                        get("/api/labels"), TEST_USERNAME)
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
        regLabelPositive(TEST_LABEL_DTO);
        assertThat(labelRepository.count()).isEqualTo(1);
    }

    @Test
    public void testCreateLabelNegative() throws Exception {
        assertThat(labelRepository.count()).isEqualTo(0);
        regLabelNegative();
        assertThat(labelRepository.count()).isEqualTo(0);
    }

    @Test
    public void testUpdateLabelPositive() throws Exception {
        regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put("/api/labels/{id}", expectedLabel.getId())
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
        regLabelPositive(TEST_LABEL_DTO);

        final Label expectedLabel = labelRepository.findAll().get(0);

        final MockHttpServletRequestBuilder request = put("/api/labels/{id}", expectedLabel.getId())
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
        regLabelPositive(TEST_LABEL_DTO);
        assertThat(labelRepository.count()).isEqualTo(1);

        final Label labelToDelete = labelRepository.findAll().get(0);

        final Long idLabelToDelete = labelToDelete.getId();

        utils.perform(delete("/api/labels/{id}", idLabelToDelete), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(labelRepository.count()).isEqualTo(0);

    }


    private ResultActions regLabelPositive(final LabelDto labelDto) throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/labels")
                .content(toJson(labelDto))
                .contentType(MediaType.APPLICATION_JSON);
        return utils.perform(request, AppConstants.TEST_USERNAME);
    }

    private ResultActions regLabelNegative() throws Exception {
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/api/labels")
                .content(toJson(TestLabelController.TEST_LABEL_DTO))
                .contentType(MediaType.APPLICATION_JSON);
        return utils.perform(request);
    }
}
