package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.ConnectionDetails;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.ConnectionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionDetailsService {
    @Autowired
    private ConnectionDetailsRepository connectionDetailsRepository;

    public void saveConnectionDetails(User user, String ipAddress, String userAgent, String device) {
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setUser(user);
        connectionDetails.setIpAddress(ipAddress);
        connectionDetails.setUserAgent(userAgent);
        connectionDetails.setDevice(device);
        connectionDetailsRepository.save(connectionDetails);
    }

    public void deleteConnectionDetails(String id) {
        connectionDetailsRepository.deleteById(Long.valueOf(id));
    }

    public void deleteConnectionDetails(User user) {
        connectionDetailsRepository.deleteAllByUser(user);
    }

    public List<ConnectionDetails> getAllConnectionsForUser(User user) {
        return connectionDetailsRepository.findAllByUser(user);
    }

    public boolean isDifferentConnection(User user, String ipAddress, String userAgent, String device) {
        List<ConnectionDetails> connections = connectionDetailsRepository.findAllByUser(user);
        if(connections == null) {
            return true;
        }
        for(ConnectionDetails connection : connections) {
            if(connection.getIpAddress().equals(ipAddress) && connection.getUserAgent().equals(userAgent) && connection.getDevice().equals(device)) {
                return false;
            }
        }
        return true;
    }
}
