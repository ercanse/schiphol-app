package nl.schiphol.schipholapp.service;

import nl.schiphol.schipholapp.entity.Destination;
import nl.schiphol.schipholapp.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {
    private DestinationRepository destinationRepository;

    public List<Destination> findAll() {
        return this.destinationRepository.findAll();
    }

    public List<Destination> findLimit(int limit) {
        return this.destinationRepository.findAll(new PageRequest(0, limit)).getContent();
    }

    public Destination findById(int id) {
        Optional<Destination> assetFlowStatus = this.destinationRepository.findById(id);
        return assetFlowStatus.orElse(null);
    }

    @Autowired
    public void setDestinationRepository(DestinationRepository assetFlowStatusRepository) {
        this.destinationRepository = assetFlowStatusRepository;
    }
}
