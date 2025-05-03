package com.resourceradar.service;

import java.util.List;

import com.resourceradar.dto.ClientRequest;
import com.resourceradar.dto.ClientResponse;
import com.resourceradar.exception.ClientNotFoundException;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;

public interface ClientService {

	public ClientRequest createClient(ClientRequest client, String orgId) throws OrgIdNotFoundException;

	public ClientRequest updateClientById(String clientid, ClientRequest client)
			throws ClientNotFoundException, ResourceNotFoundException;

	public ClientResponse getClientById(String id) throws ClientNotFoundException;

	public List<ClientResponse> getAllClientsWithManager() throws ClientNotFoundException;

	public String deleteCLient(String clientid) throws ClientNotFoundException;

}
