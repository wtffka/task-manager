package hexlet.code.service.impls;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;


    @Override
    public Label createLabel(LabelDto labelDto) {
        final Label label = new Label();
        setLabelData(labelDto, label);
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(LabelDto labelDto, Long id) {
        final Label label = labelRepository.findById(id).
                orElseThrow(() -> new NoSuchElementException("Label with that id not found"));
        setLabelData(labelDto, label);
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long id) {
        labelRepository.delete(labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label with that id not found")));
    }

    private void setLabelData(LabelDto labelDto, Label label) {
        label.setName(labelDto.getName());
    }
}
