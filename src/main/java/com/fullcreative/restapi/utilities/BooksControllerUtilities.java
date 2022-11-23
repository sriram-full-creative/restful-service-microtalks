package com.fullcreative.restapi.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fullcreative.restapi.models.Book;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author Sriram
 *
 */
public class BooksControllerUtilities {

	/** Utility Methods for HttpServletRequest Processing and Validation **/

	/**
	 * <p>
	 * Checks if the HttpServletRequest URI has a ID or not.
	 * </p>
	 * 
	 * @param requestURI
	 * @return boolean
	 */
	public static boolean hasBookID(String requestURI) {
		List<String> requestsArray = Arrays.asList(requestURI.split("/"));
		Integer count = requestsArray.size();
		if (count == 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Checks if the Requested End Point (URI) in the HttpServletRequest is valid or
	 * not. Although the HttpServlet is configured to handle requests sent to
	 * specific end points, This method verifies if the Sub Path is valid or not.
	 * <p>
	 * 
	 * @param requestURI
	 * @return boolean
	 */
	public static boolean isValidEndPoint(String requestURI) {
		List<String> requestsArray = Arrays.asList(requestURI.split("/"));
		Integer count = requestsArray.size();
		if (count == 3 || count == 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * Extracts the ID from the HttpServletRequest URI.
	 * <p>
	 * 
	 * @param request
	 * @return String
	 */
	public static String getBookIDFromUri(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		String[] requestsArray = requestUri.split("/");
		String bookID = requestsArray[requestsArray.length - 1];
		return bookID;
	}

	/**
	 * <p>
	 * Extracts the JSON Payload from the HttpServletRequest
	 * </p>
	 * 
	 * @param request
	 * @return String
	 * @throws IOException
	 */
	public static String payloadFromRequest(HttpServletRequest request) throws IOException {
		StringBuffer jsonString = new StringBuffer();
		String line = null;
		BufferedReader reader = request.getReader();
		while ((line = reader.readLine()) != null) {
			jsonString.append(line);
		}
		return jsonString.toString();
	}

	/**
	 * <p>
	 * Generates a Error Response Map for when a requested End Point is Invalid.
	 * </p>
	 * 
	 * @param responseMap
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> invalidRequestEndpointResponse(Map<String, Object> responseMap) {
		/**
		 * The HTTP 414 URI Too Long response status code indicates that the URI
		 * requested by the client is longer than the server is willing to interpret.
		 */
		responseMap.put("ERROR", "Recheck the URI");
		responseMap.put("STATUS_CODE", 400);
		return responseMap;
	}


	/** Utility Methods to Manipulate Data Structures and Data Types **/

	/**
	 * <p>
	 * Converts a LinkedHashMap into a JSON Formatted String.
	 * </p>
	 * 
	 * @param map
	 * @return String
	 */
	public static String mapToJsonString(LinkedHashMap<String, Object> map) {
		Gson gson = new Gson();
		Book book = new Book();
		book.setId((String) map.get("id"));
		book.setAuthor((String) map.get("author"));
		book.setTitle((String) map.get("title"));
		book.setLanguage((String) map.get("language"));
		book.setPages((Integer) map.get("pages"));
		book.setReleaseYear((Integer) map.get("releaseYear"));
		String jsonString = gson.toJson(book);
		return jsonString;
	}

	/**
	 * <p>
	 * Creates a List of Book POJOs from a List of Entities.
	 * </p>
	 * 
	 * @param entities
	 * @return List<Book>
	 */
	public static List<Book> booksFromEntities(List<Entity> entities) {
		List<Book> books = new ArrayList<>();
		for (Entity entity : entities) {
			books.add(bookFromEntity(entity));
		}

		return books;
	}

	/**
	 * <p>
	 * Converts the Book POJO into a LinkedHashMap.
	 * </p>
	 * 
	 * @param book
	 * @param map
	 * @return LinkedHashMap<String, Object>
	 */
	private static LinkedHashMap<String, Object> mapFromBook(Book book, LinkedHashMap<String, Object> map) {
		map.put("id", book.getId());
		map.put("author", book.getAuthor());
		map.put("title", book.getTitle());
		map.put("language", book.getLanguage());
		map.put("pages", book.getPages());
		map.put("releaseYear", book.getReleaseYear());
		return map;
	}

	/**
	 * <p>
	 * Creates a Book POJO out of the Properties form the Entity.
	 * </p>
	 * 
	 * @param entity
	 * @return Book
	 */
	private static Book bookFromEntity(Entity entity) {
		Book book = new Book();
		book.setId(entity.getKey().getName());
		book.setAuthor(entity.getProperty("Author").toString());
		book.setTitle(entity.getProperty("Title").toString());
		book.setLanguage(entity.getProperty("Language").toString());
		book.setPages(Integer.parseInt(entity.getProperty("Pages").toString()));
		book.setReleaseYear(Integer.parseInt(entity.getProperty("ReleaseYear").toString()));
		return book;
	}

	/**
	 * <p>
	 * Converts the Properties of Book POJO into an Datastore Entity.
	 * </p>
	 * <p>
	 * Call this only when creating a Book.
	 * </p>
	 * 
	 * @param book
	 * @param bookID
	 * @return Entity
	 */
	private static Entity entityFromBook(Book book, String bookID) {
		Entity entity = new Entity("Books", bookID);
		entity.setProperty("Author", book.getAuthor().toString().replaceAll("(^\\[|\\]$)", "").replaceAll(", ", ","));
		entity.setProperty("Title", book.getTitle());
		entity.setProperty("Language", book.getLanguage());
		entity.setProperty("Pages", book.getPages());
		entity.setProperty("ReleaseYear", book.getReleaseYear());
		entity.setProperty("CreatedOrUpdated", Time.from(Instant.now()));
		return entity;
	}

	/**
	 * <p>
	 * Converts the Properties of Book POJO into an Datastore Entity.
	 * </p>
	 * <p>
	 * Call this when updating an entity. If the Book POJO has a null value for a
	 * property, the corresponding entity property will be updated from the existing
	 * Datastore Entity.
	 * </p>
	 * 
	 * @param book
	 * @param bookID
	 * @return Entity
	 * @throws EntityNotFoundException
	 */
	private static Entity entityFromBookForUpdate(Book book, String bookID) throws EntityNotFoundException {
		Entity entity = new Entity("Books", bookID);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity datastoreEntity = datastore.get(entity.getKey());

		if (book.getAuthor() != null) {
			entity.setProperty("Author",
					book.getAuthor().toString().replaceAll("(^\\[|\\]$)", "").replaceAll(", ", ","));
		} else {
			entity.setProperty("Author", datastoreEntity.getProperty("Author"));
		}
		if (book.getTitle() != null) {
			entity.setProperty("Title", book.getTitle());
		} else {
			entity.setProperty("Title", datastoreEntity.getProperty("Title"));
		}
		if (book.getLanguage() != null) {
			entity.setProperty("Language", book.getLanguage());
		} else {
			entity.setProperty("Language", datastoreEntity.getProperty("Language"));
		}
		if (book.getPages() != null) {
			entity.setProperty("Pages", book.getPages());
		} else {
			entity.setProperty("Pages", datastoreEntity.getProperty("Pages"));
		}
		if (book.getReleaseYear() != null) {
			entity.setProperty("ReleaseYear", book.getReleaseYear());
		} else {
			entity.setProperty("ReleaseYear", datastoreEntity.getProperty("ReleaseYear"));
		}
		entity.setProperty("CreatedOrUpdated", Time.from(Instant.now()));
		return entity;
	}

	/**
	 * Controller Methods
	 */
	/**
	 * @param response
	 * @param responseMap
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void sendPrettyJsonResponse(HttpServletResponse response, Map<String, Object> responseMap)
			throws NumberFormatException, IOException {
		int code = Integer.parseInt(responseMap.remove("STATUS_CODE").toString());
		String responseAsJson = new GsonBuilder().setPrettyPrinting().create().toJson(responseMap);
		response.setContentType("application/json");
		response.getWriter().println(responseAsJson);
		response.setStatus(code);
	}

	/**
	 * @param response
	 * @param responseMap
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void sendJsonResponse(HttpServletResponse response, Map<String, Object> responseMap)
			throws NumberFormatException, IOException {
		int code = Integer.parseInt(responseMap.remove("STATUS_CODE").toString());
		String responseAsJson = new GsonBuilder().setPrettyPrinting().create().toJson(responseMap);
		response.setContentType("application/json");
		response.getWriter().print(responseAsJson);
		response.setStatus(code);
	}

	/**
	 * @param response
	 * @param arrayOfBooks
	 * @throws IOException
	 */
	public static void sendGetAllJsonResponse(HttpServletResponse response, LinkedList<String> arrayOfBooks)
			throws IOException {
		response.setContentType("application/json");
		response.getWriter().println(arrayOfBooks);
		response.setStatus(200);
	}

	/**
	 * @param response
	 * @param jsonData
	 * @throws IOException
	 */
	public static void sendJsonResponse(HttpServletResponse response, String jsonData) throws IOException {
		response.setContentType("application/json");
		response.getWriter().println(jsonData);
		response.setStatus(200);
	}

	/**
	 * @param response
	 * @param e
	 * @throws IOException
	 */
	public static void sendInternalServerErrorResponse(HttpServletResponse response, Exception e) throws IOException {
		e.printStackTrace();
		Map<String, String> internalServerErrorMap = new LinkedHashMap<String, String>();
		response.setContentType("application/json");
		internalServerErrorMap.put("500", "Something went wrong");
		String internalServerError = new Gson().toJson(internalServerErrorMap);
		response.getWriter().println(internalServerError);
		response.setStatus(500);
	}

	/**
	 * @param response
	 * @throws IOException
	 */
	public static void sendEmptyRequestErrorResponse(HttpServletResponse response) throws IOException {
		Map<String, String> requestErrorMap = new LinkedHashMap<String, String>();
		response.setContentType("application/json");
		requestErrorMap.put("EMPTY_REQUEST_ERROR", "Request should contain json body");
		String requestError = new Gson().toJson(requestErrorMap);
		response.getWriter().println(requestError);
		response.setStatus(400);
	}

	/**
	 * @param queryParameters
	 * @return
	 * @throws JsonSyntaxException
	 */
	public static String processGetAllRequest() throws JsonSyntaxException {
		LinkedHashMap<String, Object> results = new LinkedHashMap<String, Object>();
		results = BooksControllerUtilities.getAllBooks();
		String result = new Gson().newBuilder().setPrettyPrinting().create().toJson(results, LinkedHashMap.class)
				.toString();
		return result;
	}

	/**
	 * @param responseMap
	 * @param jsonRequestString
	 * @return
	 * @throws NullPointerException
	 * @throws EntityNotFoundException
	 */
	public static Map<String, Object> processCreateRequest(Map<String, Object> responseMap, String jsonRequestString)
			throws NullPointerException, EntityNotFoundException {
		// Request is empty
		if (jsonRequestString.length() == 0 || jsonRequestString.substring(1).replaceAll("}", "").length() == 0) {
			throw new NullPointerException();
		}
		// Request has only book details to be updated
		else if (jsonRequestString != null) {
			System.out.println("Request Has JSON Body");
			System.out.println("Request JSON Body: " + jsonRequestString);
			responseMap = BooksControllerUtilities.createNewBook(jsonRequestString);
		}
		return responseMap;
	}

	/**
	 * @param responseMap
	 * @param bookID
	 * @param jsonRequestString
	 * @return
	 * @throws NullPointerException
	 * @throws EntityNotFoundException
	 */
	public static Map<String, Object> processUpdateRequest(Map<String, Object> responseMap, String bookID,
			String jsonRequestString) throws NullPointerException, EntityNotFoundException {
		// Request is empty
		if (jsonRequestString.length() == 0 || jsonRequestString.substring(1).replaceAll("}", "").length() == 0) {
			throw new NullPointerException();
		}
		// Request has only book details to be updated
		else {
			System.out.println("Request Has JSON Body");
			System.out.println("Request JSON Body: " + jsonRequestString);
			responseMap = BooksControllerUtilities.updateBook(jsonRequestString, bookID);
		}
		return responseMap;
	}

	/**
	 * @param bookID
	 * @return
	 */
	public static LinkedHashMap<String, Object> processGetOneBookRequest(String bookID) {
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			responseMap = BooksControllerUtilities.getOneBook(bookID);
			responseMap.put("STATUS_CODE", 200);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			responseMap.put("ERROR", "Book not Found. Invalid Key");
			responseMap.put("STATUS_CODE", 404);
		}
		return responseMap;
	}

	/**
	 * Utility Methods used to Perform Validations on data sent in
	 * HttpServletRequest.
	 **/

	/**
	 * <p>
	 * Validates the bookID by checking the datastore for the presence of the ID.
	 * </p>
	 * 
	 * @param bookID
	 * @return boolean
	 */
	public static boolean bookIDValidator(String bookID) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		// Validating the bookID
		Entity entity = new Entity("Books", bookID);
		try {
			datastore.get(entity.getKey());
			return true;
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <p>
	 * Validates the book details created from the HttpServeltRequest Body.
	 * </p>
	 * 
	 * <p>
	 * Call this method to validate data only when creating a new book.
	 * </p>
	 * 
	 * @param book
	 * @param errorMap
	 * @return LinkedHashMap<String, Object>
	 */
	private static LinkedHashMap<String, Object> requestBookValidator(Book book,
			LinkedHashMap<String, Object> errorMap) {

		int flag = 0;

		if (book.getAuthor().replaceAll(" ", "").length() == 0 || book.getAuthor().length() <= 0
				|| book.getAuthor() == null) {
			errorMap.put("AUTHOR_NAME_EMPTY_ERROR", "Author Name should contain atleast 1 character");
			flag = 1;
		} else if (book.getAuthor().replaceAll(" ", "").matches("[a-zA-Z.]+") == false
				&& book.getAuthor().replaceAll(" ", "").length() != 0) {
			errorMap.put("AUTHOR_NAME_FORMAT_ERROR", "Author Name should contain only alphabets");
			flag = 1;
		}

		if (book.getTitle().replaceAll(" ", "").length() == 0 || book.getTitle().length() <= 0
				|| book.getTitle() == null) {
			errorMap.put("TITLE_NAME_ERROR", "Title Name should contain atleast 1 character");
			flag = 1;
		}

		if (book.getLanguage().replaceAll(" ", "").length() == 0 || book.getLanguage().length() <= 0
				|| book.getLanguage() == null) {
			errorMap.put("LANGUAGE_EMPTY_ERROR", "Language field can't be empty");
			flag = 1;
		}
		if (book.getLanguage().replaceAll(" ", "").matches("[a-zA-Z]+") == false
				&& book.getLanguage().replaceAll(" ", "").length() != 0) {
			errorMap.put("LANGUAGE_FORMAT_ERROR", "Language field can contain only alphabets");
			flag = 1;
		}


		if (book.getPages() < 20) {
			if (book.getPages() < 0) {
				errorMap.put("PAGES_ERROR", "Page Count should be Positive");
				flag = 1;
			} else {
				errorMap.put("PAGES_ERROR", "Book should have atleast 20 pages");
				flag = 1;
			}
		}

		if (book.getReleaseYear() <= 0) {
			errorMap.put("YEAR_NEGATIVE_VALUE_ERROR", "Year should be positive");
			flag = 1;
		}
		if (book.getReleaseYear() > Year.now().getValue()) {
			errorMap.put("YEAR_FUTURE_VALUE_ERROR",
					"Year should be less than or equal to the current year -> '" + Year.now().getValue() + "'");
			flag = 1;
		}


		if (flag == 1) {
			errorMap.put("STATUS_CODE", 400);
		}
		return errorMap;

	}

	/**
	 * <p>
	 * Validates the book details created from the HttpServeltRequest Body.
	 * </p>
	 * 
	 * <p>
	 * Call this method to validate data only when updating the book details.
	 * </p>
	 * 
	 * @param book
	 * @param errorMap
	 * @return LinkedHashMap<String, Object>
	 */
	private static LinkedHashMap<String, Object> requestBookValidatorForUpdation(Book book,
			LinkedHashMap<String, Object> errorMap) {

		int flag = 0;

		if (book.getAuthor().replaceAll(" ", "").length() == 0 || book.getAuthor().length() <= 0) {
			errorMap.put("AUTHOR_NAME_EMPTY_ERROR", "Author Name should contain atleast 1 character");
			flag = 1;
		} else if (book.getAuthor().replaceAll(" ", "").matches("[a-zA-Z.]+") == false
				&& book.getAuthor().replaceAll(" ", "").length() != 0) {
			errorMap.put("AUTHOR_NAME_FORMAT_ERROR", "Author Name should contain only alphabets");
			flag = 1;
		}

		if (book.getTitle() != null) {
			if (book.getTitle().replaceAll(" ", "").length() == 0 || book.getTitle().length() <= 0) {
				errorMap.put("TITLE_NAME_ERROR", "Title Name should contain atleast 1 character");
				flag = 1;
			}
		}

		if (book.getLanguage() != null) {
			if (book.getLanguage().replaceAll(" ", "").length() == 0 || book.getLanguage().length() <= 0) {
				errorMap.put("LANGUAGE_EMPTY_ERROR", "Language field can't be empty");
				flag = 1;
			}
			if (book.getLanguage().replaceAll(" ", "").matches("[a-zA-Z]+") == false
					&& book.getLanguage().replaceAll(" ", "").length() != 0) {
				errorMap.put("LANGUAGE_FORMAT_ERROR", "Language field can contain only alphabets");
				flag = 1;
			}
		}

		if (book.getPages() != null) {

			if (book.getPages() < 20) {
				if (book.getPages() < 0) {
					errorMap.put("PAGES_ERROR", "Page Count should be Positive");
					flag = 1;
				} else {
					errorMap.put("PAGES_ERROR", "Book should have atleast 20 pages");
					flag = 1;
				}
			}
		}

		if (book.getReleaseYear() != null) {
			if (book.getReleaseYear() <= 0) {
				errorMap.put("YEAR_NEGATIVE_VALUE_ERROR", "Year should be positive");
				flag = 1;
			}
			if (book.getReleaseYear() > Year.now().getValue()) {
				errorMap.put("YEAR_FUTURE_VALUE_ERROR",
						"Year should be less than or equal to the current year -> '" + Year.now().getValue() + "'");
				flag = 1;
			}
		}
		if (flag == 1) {
			errorMap.put("STATUS_CODE", 400);
		}
		return errorMap;

	}

	/** Utility Methods to Perform CRUD Operations for the Application **/

	/** 1. CREATE Operation **/

	/**
	 * <p>
	 * Creates a New Entity in the Datastore with all the properties of the Book
	 * POJO.
	 * </p>
	 * 
	 * @param jsonInputString
	 * @return LinkedHashMap<String, Object>
	 * @throws EntityNotFoundException
	 */
	public static LinkedHashMap<String, Object> createNewBook(String jsonInputString) throws EntityNotFoundException {
		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		Book newBook = gson.fromJson(jsonInputString, Book.class);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			responseMap = requestBookValidator(newBook, responseMap);
		} catch (Exception e) {
			responseMap.put("REQUEST_BODY_ERROR", "The request body should contain a json body");
			responseMap.put("STATUS_CODE", 400);
		}
		if (responseMap.size() != 0) {
			return responseMap;
		} else {
			String bookID = UUID.randomUUID().toString();
			Entity entity = entityFromBook(newBook, bookID);
			Key keyObj = datastore.put(entity);
			try {
				Entity responseEntity = datastore.get(keyObj);
				Book responseBookData = new Book();
				responseBookData = bookFromEntity(responseEntity);
				responseMap = mapFromBook(responseBookData, responseMap);
				responseMap.put("STATUS_CODE", 200);
			} catch (Exception e) {
				System.out.println("Thrown from createNewBook Method");
				responseMap.put("ERROR", "Book was not created");
				responseMap.put("STATUS_CODE", 503);
				e.printStackTrace();
		}
			return responseMap;
		}
	}


	/** 2. READ Operation **/

	/**
	 * <p>
	 * Fetches <strong>all</strong> the Books from the Datastore.
	 * 
	 * <p>
	 * By default the recently updated or created books will be served first. User
	 * can also send parameters to sort the books based on properties both in the
	 * order of ascending or descending.
	 * </p>
	 * 
	 * <p>
	 * Parameter Names and their accepted values.
	 * </p>
	 * <ol>
	 * <li>sortOnProperty = {"author", "publication", "title", "pages",
	 * "releaseYear", "rating"}
	 * <li>sortDirection = {"ASCENDING", "DESCENDING"}
	 * </ol>
	 * <p>
	 * ParameterNames are <b>Case-Sensitive</b> while Values are.
	 * <b>Case-Insensitive</b>
	 * </p>
	 * 
	 * @param queryParameters
	 * @return LinkedList<String>
	 * @deprecated - <strong>DON'T USE</strong> THIS AS IT WILL CAUSE <strong> HEAP
	 *             SPACE ERROR.</strong>This isn't the right way as it fetches all
	 *             the books at once and that will cause major server issues when
	 *             scaled.
	 *             </p>
	 */
	@Deprecated
	public static LinkedList<String> getAllBooks(Map<String, String> queryParameters) {
		LinkedHashMap<String, Object> bookAsMap = new LinkedHashMap<String, Object>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String property = queryParameters.get("sortOnProperty");
		String direction = queryParameters.get("sortDirection");
		Query query = new Query("Books").addSort(property, SortDirection.DESCENDING);
		if (direction.equalsIgnoreCase("ascending")) {
			query = new Query("Books").addSort(property, SortDirection.ASCENDING);
		}
		List<Entity> bookEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		System.out.println(bookEntities);
		List<Book> booksFromEntities = BooksControllerUtilities.booksFromEntities(bookEntities);
		LinkedList<String> books = new LinkedList<>();
		for (Book book : booksFromEntities) {
			books.add(mapToJsonString(mapFromBook(book, bookAsMap)));
		}
		return books;
	}

	/**
	 * @param startCursor
	 * @return
	 */
	public static LinkedList<String> getAllBooks(String startCursor) {
		int PAGE_SIZE = 15;
		LinkedHashMap<String, Object> bookAsMap = new LinkedHashMap<String, Object>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(PAGE_SIZE);
		if (startCursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
		}
		Query query = new Query("Books").addSort("CreatedOrUpdated", SortDirection.DESCENDING);
		List<Entity> bookEntities = datastore.prepare(query).asQueryResultList(fetchOptions);
		List<Book> booksFromEntities = BooksControllerUtilities.booksFromEntities(bookEntities);
		LinkedList<String> books = new LinkedList<>();
		for (Book book : booksFromEntities) {
			books.add(mapToJsonString(mapFromBook(book, bookAsMap)));
		}
		return books;
	}

	/**
	 * @param queryParameters
	 * @param startCursor
	 * @return
	 */
	public static LinkedHashMap<String, Object> getAllBooks() {
		LinkedHashMap<String, Object> results = new LinkedHashMap<String, Object>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
		Query query = new Query("Books");
		List<Entity> bookEntities = datastore.prepare(query).asList(fetchOptions);
		List<Book> booksFromEntities = BooksControllerUtilities.booksFromEntities(bookEntities);
		results.put("books", booksFromEntities);
		return results;
	}

	/**
	 * <p>
	 * Serves the data of the Book when passed with the valid ID.
	 * </p>
	 * 
	 * @param bookID
	 * @return LinkedHashMap<String, Object>
	 * @throws EntityNotFoundException
	 */
	public static LinkedHashMap<String, Object> getOneBook(String bookID) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key bookKey = KeyFactory.createKey("Books", bookID);
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			Entity responseEntity = datastore.get(bookKey);
			Book responseBookData = BooksControllerUtilities.bookFromEntity(responseEntity);
			responseMap = mapFromBook(responseBookData, responseMap);
			responseMap.put("STATUS_CODE", 200);
		} catch (Exception e) {
			System.out.println("Caught in getOneBook method");
			e.printStackTrace();
			responseMap.put("ERROR", "Book not Found. Invalid Key");
			responseMap.put("STATUS_CODE", 404);
		}
		return responseMap;
	}

	/** 3. UPDATE Operation **/

	/**
	 * <p>
	 * Updates the Datastore Entity alone.
	 * </p>
	 * 
	 * <p>
	 * Call this Method to Update Fields of the Book.
	 * </p>
	 * 
	 * @param jsonInputString
	 * @param bookID
	 * @return LinkedHashMap<String, Object>
	 * @throws EntityNotFoundException
	 */
	public static LinkedHashMap<String, Object> updateBook(String jsonInputString, String bookID)
			throws EntityNotFoundException {
		Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		Book newBook = gson.fromJson(jsonInputString, Book.class);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			responseMap = requestBookValidatorForUpdation(newBook, responseMap);
			if (responseMap.size() != 0) {
				return responseMap;
			} else {
				Entity entity = entityFromBookForUpdate(newBook, bookID);
				Key keyObj = datastore.put(entity);
				Entity responseEntity = datastore.get(keyObj);
				Book responseBookData = new Book();
				responseBookData = bookFromEntity(responseEntity);
				responseMap = mapFromBook(responseBookData, responseMap);
				responseMap.put("STATUS_CODE", 200);
			}
		} catch (Exception e) {
			System.out.println("Thrown from updateBook Method");
			responseMap.put("ERROR", "Book not Found. Invalid Key");
			responseMap.put("STATUS_CODE", 404);
			e.printStackTrace();
		}
		return responseMap;
	}



	/** 4. DELETE Operation **/

	/**
	 * <p>
	 * Deletes the Book Entity from Datastore and does not interact with the GCS.
	 * </p>
	 * 
	 * @param bookID
	 * @return LinkedHashMap<String, Object>
	 * @throws EntityNotFoundException
	 */
	public static LinkedHashMap<String, Object> deleteBook(String bookID) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key entityKey = KeyFactory.createKey("Books", bookID);
		LinkedHashMap<String, Object> responseMap = new LinkedHashMap<>();
		try {
			datastore.get(entityKey);
			datastore.delete(entityKey);
			responseMap.put("SUCCESS", "Book was deleted");
			responseMap.put("STATUS_CODE", 200);
		} catch (Exception e) {
			System.out.println("Caught in deleteBook Method");
			e.printStackTrace();
			responseMap.put("ERROR", "Book not Found. Invalid Key");
			responseMap.put("STATUS_CODE", 404);
		}
		return responseMap;
	}


}