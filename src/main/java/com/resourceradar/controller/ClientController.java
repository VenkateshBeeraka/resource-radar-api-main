package com.resourceradar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resourceradar.config.EndPointConfig;
import com.resourceradar.dto.ClientRequest;
import com.resourceradar.dto.ClientResponse;
import com.resourceradar.exception.ClientNotFoundException;
import com.resourceradar.exception.DeleteResponse;
import com.resourceradar.exception.OrgIdNotFoundException;
import com.resourceradar.exception.ResourceNotFoundException;
import com.resourceradar.service.impl.ClientServiceImpl;
import com.resourceradar.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndPointConfig.API_V1 + EndPointConfig.CLIENT)
@Tag(name = "client")
@Slf4j
public class ClientController {

    @Autowired
    private ClientServiceImpl clientService;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
    @Operation(
            summary = "Create a new Client",
            description = "Create a new Client",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "X-Org-Id",
                            description = "Organization Id",
                            required = true,
                            schema = @Schema(type = "string"))},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ClientRequest.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<ClientRequest> createClient(@RequestBody ClientRequest clientRequest, HttpServletRequest request) throws OrgIdNotFoundException {
       	ClientRequest cli = clientService.createClient(clientRequest, request.getHeader(Constants.ORG_ID));
		if (cli == null) {
       		return ResponseEntity.notFound().build();
       	}
       return ResponseEntity.ok(cli);
    }
     
       
    
    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
    @Operation(
            summary = "Find a client by an Id",
            description = "Find a client by using clientId.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "	", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<ClientResponse> getClientById(@PathVariable String clientId) throws ClientNotFoundException {
        ClientResponse client = clientService.getClientById(clientId);
        if (client == null) {
        	return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(client, HttpStatus.OK);
    }
    
    @PutMapping("/{clientId}")
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
    @Operation(
            summary = "Update details of a specific client",
            description = "Updates client information by passing the client Id and a JSON representation of the updated client.",
            responses = {
                    @ApiResponse(
                            description = "Updated",
                            responseCode = "200",
                            links = @Link(name = "get", operationId = "get", parameters = @LinkParameter(name = "id", expression = "$request.body.id")),
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponse.class))
                    ),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
  )
	public ClientRequest updateClientStatusById(@PathVariable String clientId, @RequestBody ClientRequest clientDTO)
			throws ClientNotFoundException, ResourceNotFoundException {
	ClientRequest updateClientById = clientService.updateClientById(clientId, clientDTO);
     log.info(clientDTO.getStatus() + "    " + clientId);
    return updateClientById;
    }
    

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_HR') OR hasRole('ROLE_ResourceManager') OR hasRole('ROLE_Admin')")
    @Operation(
            summary = "Retrieve a list of all clients",
            description = "Retrieve a list of all clients with manager.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = ClientResponse.class))
                                    )
                            }
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
	public ResponseEntity<List<ClientResponse>> getAllClientsWithManager() throws ClientNotFoundException {
       List<ClientResponse> allClients = clientService.getAllClientsWithManager();
		if (allClients.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(allClients, HttpStatus.OK);
		}
	}
       
    	    
    @DeleteMapping("/{clientId}")
    @Operation(
            summary = "Delete a client",
            description = "Delete an client by clientId.",
            responses = {
                    @ApiResponse(description = "Deleted", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content)
            }
    )
    public ResponseEntity<String> delectClient(@PathVariable String clientId) throws ClientNotFoundException, DeleteResponse {
    	String deleteCLient = clientService.deleteCLient(clientId);
    	if (deleteCLient == null) {
    		return ResponseEntity.notFound().build();
    	}
    	throw new DeleteResponse(deleteCLient);
    }
    
}
