package com.fullcreative.restapi.controllers;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullcreative.restapi.utilities.BooksControllerUtilities;

@WebServlet(name = "bookServlet", urlPatterns = { "/books", "/books/*" })
public class BooksController extends HttpServlet {

	private static final long serialVersionUID = -8271652320356442502L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, Object> responseMap = new LinkedHashMap<>();
			if (BooksControllerUtilities.isValidEndPoint(request.getRequestURI())) {
				if (BooksControllerUtilities.hasBookID(request.getRequestURI())) {
					String bookID = BooksControllerUtilities.getBookIDFromUri(request);
					responseMap = BooksControllerUtilities.processGetOneBookRequest(bookID);
					System.out.println(responseMap);
					BooksControllerUtilities.sendJsonResponse(response, responseMap);
				} else {
					String jsonData = BooksControllerUtilities.processGetAllRequest();
					BooksControllerUtilities.sendJsonResponse(response, jsonData);
				}
			} else {
				responseMap = BooksControllerUtilities.invalidRequestEndpointResponse(responseMap);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			}
		} catch (Exception e) {
			System.out.println("Caught in doGet servlet service method");
			BooksControllerUtilities.sendInternalServerErrorResponse(response, e);
		}
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, Object> responseMap = new LinkedHashMap<>();
			if (BooksControllerUtilities.hasBookID(request.getRequestURI()) == false
					&& BooksControllerUtilities.isValidEndPoint(request.getRequestURI())) {
				// Getting JSON request body and image Part
				String jsonRequestString = BooksControllerUtilities.payloadFromRequest(request);
				responseMap = BooksControllerUtilities.processCreateRequest(responseMap, jsonRequestString);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			} else {
				responseMap = BooksControllerUtilities.invalidRequestEndpointResponse(responseMap);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BooksControllerUtilities.sendEmptyRequestErrorResponse(response);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, Object> responseMap = new LinkedHashMap<>();
			if (BooksControllerUtilities.hasBookID(request.getRequestURI()) == true
					&& BooksControllerUtilities.isValidEndPoint(request.getRequestURI())) {
				String bookID = BooksControllerUtilities.getBookIDFromUri(request);
				// Getting JSON request body
				String jsonRequestString = BooksControllerUtilities.payloadFromRequest(request);
				responseMap = BooksControllerUtilities.processUpdateRequest(responseMap, bookID, jsonRequestString);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);

			} else {
				responseMap = BooksControllerUtilities.invalidRequestEndpointResponse(responseMap);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			BooksControllerUtilities.sendEmptyRequestErrorResponse(response);
		} catch (Exception e) {
			System.out.println("Caught in doPut servlet service method");
			BooksControllerUtilities.sendInternalServerErrorResponse(response, e);
		}
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, Object> responseMap = new LinkedHashMap<>();
			if (BooksControllerUtilities.hasBookID(request.getRequestURI()) == true
					&& BooksControllerUtilities.isValidEndPoint(request.getRequestURI())) {
				String bookID = BooksControllerUtilities.getBookIDFromUri(request);
				responseMap = BooksControllerUtilities.deleteBook(bookID);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			} else {
				responseMap = BooksControllerUtilities.invalidRequestEndpointResponse(responseMap);
				BooksControllerUtilities.sendJsonResponse(response, responseMap);
			}
		} catch (Exception e) {
			System.out.println("Caught in doDelete servlet service method");
			BooksControllerUtilities.sendInternalServerErrorResponse(response, e);
		}
	}
}