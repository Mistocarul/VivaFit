package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.ConnectionDetails;
import com.vivafit.vivafit.authentification.repositories.ConnectionDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionDetailsService {
    @Autowired
    private ConnectionDetailsRepository connectionDetailsRepository;

    public void saveConnectionDetails(String username, String ipAddress, String userAgent) {
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setUsername(username);
        connectionDetails.setIpAddress(ipAddress);
        connectionDetails.setUserAgent(userAgent);
        connectionDetailsRepository.save(connectionDetails);
    }

    public boolean isDifferentConnection(String username, String ipAddress, String userAgent) {
        List<ConnectionDetails> connections = connectionDetailsRepository.findAllByUsername(username);
        if(connections == null) {
            return true;
        }
        for(ConnectionDetails connection : connections) {
            if(connection.getIpAddress().equals(ipAddress) && connection.getUserAgent().equals(userAgent)) {
                return false;
            }
        }
        return true;
    }
}
