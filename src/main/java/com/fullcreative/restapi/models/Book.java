package com.fullcreative.restapi.models;

import com.google.gson.annotations.Expose;

/**
 * @author sriram
 *
 */
public class Book {

	@Expose(serialize = true, deserialize = true)
	private String id;
	@Expose(serialize = true, deserialize = true)
	private String author;
	@Expose(serialize = true, deserialize = true)
	private String language;
	@Expose(serialize = true, deserialize = true)
	private Integer pages;
	@Expose(serialize = true, deserialize = true)
	private String title;
	@Expose(serialize = true, deserialize = true)
	private Integer releaseYear;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}




}
