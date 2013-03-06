package com.gigaspaces.spark;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.Random;

import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.servlet.SparkApplication;

public class BooksService implements SparkApplication {

	/**
	 * Map holding the books
	 */

	private GigaSpace gigaSpace;

	public void init() {

		if (gigaSpace == null) {
			gigaSpace = new GigaSpaceConfigurer(new UrlSpaceConfigurer(
					"/./bookSpace").space()).gigaSpace();
		}

		// Creates a new book resource, will return the ID to the created
		// resource
		// author and title are sent as query parameters e.g.
		// /books?author=Foo&title=Bar
		post(new Route("/books") {
			Random random = new Random();

			@Override
			public Object handle(Request request, Response response) {
				String author = request.queryParams("author");
				String title = request.queryParams("title");
				Book book = new Book(author, title);

				int id = random.nextInt(Integer.MAX_VALUE);
				book.setId(id + "");

				gigaSpace.write(book);

				response.status(201); // 201 Created
				return id;

			}
		});

		// Gets the book resource for the provided id
		get(new Route("/books/:id") {
			@Override
			public Object handle(Request request, Response response) {
				Book template = new Book();

				template.setId(request.params(":id"));

				Book book = gigaSpace.read(template);
				if (book != null) {
					return "Title: " + book.getTitle() + ", Author: "
							+ book.getAuthor();
				} else {
					response.status(404); // 404 Not found
					return "Book not found";
				}
			}
		});

		// Updates the book resource for the provided id with new information
		// author and title are sent as query parameters e.g.
		// /books/<id>?author=Foo&title=Bar
		put(new Route("/books/:id") {
			@Override
			public Object handle(Request request, Response response) {
				String id = request.params(":id");

				Book template = new Book();

				template.setId(id);

				Book book = gigaSpace.read(template);

				if (book != null) {
					String newAuthor = request.queryParams("author");
					String newTitle = request.queryParams("title");
					if (newAuthor != null) {
						book.setAuthor(newAuthor);
					}
					if (newTitle != null) {
						book.setTitle(newTitle);
					}

					gigaSpace.write(book);
					return "Book with id '" + id + "' updated";
				} else {
					response.status(404); // 404 Not found
					return "Book not found";
				}
			}
		});

		// Deletes the book resource for the provided id
		delete(new Route("/books/:id") {
			@Override
			public Object handle(Request request, Response response) {
				String id = request.params(":id");
				
				Book template = new Book();

				template.setId(id);

				Book book = gigaSpace.take(template);
				
				if (book != null) {
					return "Book with id '" + id + "' deleted";
				} else {
					response.status(404); // 404 Not found
					return "Book not found";
				}
			}
		});

		// Gets all available book resources (id's)
		get(new Route("/books") {
			@Override
			public Object handle(Request request, Response response) {
				String ids = "";

				Book[] books = gigaSpace.readMultiple(new Book());
				
				for (Book book:books)
					ids = book.getId() + " " + ids;

				return ids;
			}
		});

	}
}
